package com.deepak.periodsaathi.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.deepak.periodsaathi.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

@Singleton
class PocketBaseService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val baseUrl: String = BuildConfig.POCKETBASE_URL

    companion object {
        private const val TAG = "PocketBaseService"
        private const val COLLECTION = "forum_images"
    }

    suspend fun uploadImage(uriString: String): String? = withContext(Dispatchers.IO) {
        try {
            if (baseUrl.isBlank()) {
                Log.w(TAG, "PocketBase URL not configured")
                return@withContext null
            }

            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val maxSize = 800
            val scale = minOf(maxSize.toFloat() / original.width, maxSize.toFloat() / original.height, 1f)
            val resized = if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    original,
                    (original.width * scale).toInt(),
                    (original.height * scale).toInt(),
                    true
                )
            } else original

            val dir = File(context.cacheDir, "pb_uploads")
            dir.mkdirs()
            val fileName = "img_${UUID.randomUUID()}.jpg"
            val file = File(dir, fileName)
            file.outputStream().use { out ->
                resized.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }

            val boundary = "Boundary${System.currentTimeMillis()}"
            val url = URL("$baseUrl/api/collections/$COLLECTION/records")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            conn.doOutput = true
            conn.connectTimeout = 30_000
            conn.readTimeout = 30_000

            conn.outputStream.use { output ->
                val writer = OutputStreamWriter(output, Charsets.UTF_8)

                writer.write("--$boundary\r\n")
                writer.write("Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"\r\n")
                writer.write("Content-Type: image/jpeg\r\n\r\n")
                writer.flush()

                file.inputStream().use { fileIn ->
                    fileIn.copyTo(output)
                }

                writer.write("\r\n--$boundary--\r\n")
                writer.flush()
            }

            file.delete()

            val responseCode = conn.responseCode
            val responseBody = if (responseCode in 200..299) {
                BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
            } else {
                BufferedReader(InputStreamReader(conn.errorStream ?: conn.inputStream)).use { it.readText() }
            }

            conn.disconnect()

            if (responseCode in 200..299) {
                val json = JSONObject(responseBody)
                val record = json.optJSONObject("record")
                val id = record?.optString("id", "") ?: ""
                val fileField = record?.optString("file", "") ?: ""
                if (id.isNotBlank() && fileField.isNotBlank()) {
                    val fileUrl = "$baseUrl/api/files/$COLLECTION/$id/$fileField"
                    Log.d(TAG, "Image uploaded: $fileUrl")
                    return@withContext fileUrl
                }
            }

            Log.e(TAG, "Upload failed: $responseCode $responseBody")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Upload error", e)
            null
        }
    }
}
