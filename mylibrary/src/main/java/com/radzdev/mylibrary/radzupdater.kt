package com.radzdev.mylibrary


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class RadzUpdater(private val activity: Activity, private val url: String) {

    fun checkForUpdates() {
        CoroutineScope(Dispatchers.IO).launch {
            val json = getUpdateJson(url)
            withContext(Dispatchers.Main) {
                handleUpdate(json)
            }
        }
    }

    private suspend fun getUpdateJson(url: String): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val response = readUrl(url)
                JSONObject(response)
            } catch (e: Exception) {
                null
            }
        }
    }

    private suspend fun readUrl(urlString: String): String {
        return withContext(Dispatchers.IO) {
            var reader: BufferedReader? = null
            try {
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
    }

    private fun handleUpdate(json: JSONObject?) {
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