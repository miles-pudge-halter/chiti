package team.penicillin.penicillin.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


/**
 * Utils for File
 */
object FileUtils {
    val URI_TYPE_FILE = 0
    val URI_TYPE_ASSET = 1
    val URI_TYPE_CONTENT = 2
    val URI_TYPE_RESOURCE = 3
    val URI_TYPE_DATA = 4
    val URI_TYPE_HTTP = 5
    val URI_TYPE_HTTPS = 6
    val URI_TYPE_UNKNOWN = -1
    private val LOCAL_FILE_PROJECTION = arrayOf("_data")

    fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun getUriType(uri: Uri): Int {
        val scheme = uri.scheme
        if (ContentResolver.SCHEME_CONTENT == scheme) {
            return URI_TYPE_CONTENT
        }
        if (ContentResolver.SCHEME_ANDROID_RESOURCE == scheme) {
            return URI_TYPE_RESOURCE
        }
        if (ContentResolver.SCHEME_FILE == scheme) {
            return if (uri.path!!.startsWith("/android_asset/")) {
                URI_TYPE_ASSET
            } else URI_TYPE_FILE
        }
        if ("data" == scheme) {
            return URI_TYPE_DATA
        }
        if ("http" == scheme) {
            return URI_TYPE_HTTP
        }
        return if ("https" == scheme) {
            URI_TYPE_HTTPS
        } else URI_TYPE_UNKNOWN
    }

    fun mapUriToFile(context: Context, uri: Uri): File? {
        when (getUriType(uri)) {
            URI_TYPE_FILE -> return File(uri.path!!)
            URI_TYPE_CONTENT -> {
                val cursor = context.contentResolver.query(uri, LOCAL_FILE_PROJECTION, null, null, null)
                if (cursor != null) {
                    try {
                        val columnIndex = cursor.getColumnIndex(LOCAL_FILE_PROJECTION[0])
                        if (columnIndex != -1 && cursor.count > 0) {
                            cursor.moveToFirst()
                            val realPath = cursor.getString(columnIndex)
                            if (realPath != null) {
                                return File(realPath)
                            }
                        }
                    } finally {
                        cursor.close()
                    }
                }
            }
        }
        return null
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri, context: Context): String {
        var path = ""
        if (context.contentResolver != null) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    @Throws(IOException::class)
    fun getBitmap(selectedimg: Uri, activity: Activity): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = 3
        var fileDescriptor: AssetFileDescriptor? = null
        fileDescriptor = activity.contentResolver.openAssetFileDescriptor(selectedimg, "r")
        return BitmapFactory.decodeFileDescriptor(
            fileDescriptor!!.fileDescriptor, null, options)
    }

    fun getUri(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }
}