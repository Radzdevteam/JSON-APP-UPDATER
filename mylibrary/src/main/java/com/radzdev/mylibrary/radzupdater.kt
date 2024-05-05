package com.radzdev.mylibrary


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import kotlin.Exception

class RadzUpdater(private val activity: Activity, private val url: String) {

    fun checkForUpdates() {
        CheckUpdateTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class CheckUpdateTask : AsyncTask<Void, Void, JSONObject>() {

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg voids: Void): JSONObject? {
            return getUpdateJson(url)
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(json: JSONObject?) {
            try {
                val latestVersion = json?.getString("latestVersion")
                val currentVersion = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName
                Log.d("Version Info", "Current version: $currentVersion")
                Log.d("Version Info", "Latest version: $latestVersion")

                if (latestVersion != currentVersion) {
                    showUpdateDialog(json)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun getUpdateJson(url: String): JSONObject? {
            return try {
                val response = readUrl(url)
                JSONObject(response)
            } catch (e: Exception) {
                null
            }
        }

        private fun readUrl(urlString: String): String {
            var reader: BufferedReader? = null
            return try {
                val url = URL(urlString)
                reader = BufferedReader(InputStreamReader(url.openStream()))
                val buffer = StringBuilder()
                val chars = CharArray(1024)
                var read: Int
                while (reader.read(chars).also { read = it } != -1) {
                    buffer.appendRange(chars, 0, read)
                }
                var response = buffer.toString()
                response = response.replace("the raw link shows", "").trim()
                response
            } finally {
                reader?.close()
            }
        }

        private fun showUpdateDialog(json: JSONObject?) {
            try {
                val latestVersion = json?.getString("latestVersion")
                val url = json?.getString("url")
                val releaseNotesArray = json?.getJSONArray("releaseNotes")

                val releaseNotes = StringBuilder()
                for (i in 0 until releaseNotesArray?.length()!!) {
                    releaseNotes.append(releaseNotesArray.getString(i))
                    if (i < releaseNotesArray.length() - 1) {
                        releaseNotes.append("\n")
                    }
                }

                val dialog = AlertDialog.Builder(activity)
                    .setTitle("New update available!")
                    .setMessage("Update $latestVersion is available to download.\n\n${releaseNotes.toString()}")
                    .setCancelable(false)
                    .setPositiveButton("Update", null)
                    .create()

                dialog.setOnShowListener { dialogInterface ->
                    val button = (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    button.setOnClickListener {
                        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                }

                dialog.show()
            } catch (_: JSONException) {
            }
        }
    }
}