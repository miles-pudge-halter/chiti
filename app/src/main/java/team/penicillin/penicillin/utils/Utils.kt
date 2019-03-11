package team.penicillin.penicillin.utils

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.view.ViewManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.custom.ankoView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun View.setBackgroundTint(color: Int, context: Context){
    this.background.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_ATOP)
}

fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null)
            view = View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun stringToRequestBody(text: String): RequestBody = RequestBody.create(MediaType.parse("text/plain"), text)

    fun fileToMultipartBody(file: File?, name: String): MultipartBody.Part? {
        return if(file != null) {
            val body = RequestBody.create(MediaType.parse("image/jpeg"), file)
            MultipartBody.Part.createFormData(name, file.name, body)
        } else {
            null
        }
    }


fun String.getPrettyDate(): String{
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("MM"))
        val requiredFormat = SimpleDateFormat("MMM dd, yyyy", Locale("MM"))
        requiredFormat.format(dateFormat.parse(this))
    } catch(e: Exception){
        e.printStackTrace()
        this
    }
}

fun String.getDate(): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("MM"))
    return dateFormat.parse(this)
}

fun String.getDateTime(): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"	, Locale("MM"))
    return dateFormat.parse(this)
}

