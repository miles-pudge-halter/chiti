package team.penicillin.penicillin

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import team.penicillin.penicillin.network.RetrofitService
import team.penicillin.penicillin.network.apis.UserApiService
import team.penicillin.penicillin.utils.*
import java.io.File

const val PROFILE_PICTURE_CODE = 123

@RuntimePermissions
class RegisterActivity : AppCompatActivity() {

    private var profilePicture: File? = null
    private var isNew = false
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        image_holder.setOnClickListener {
            startImagePickerWithPermissionCheck(PROFILE_PICTURE_CODE)
        }
        isNew = intent.getBooleanExtra("new", false)

        submit_btn.setOnClickListener {
            if(validate()){
                saveDate(name_input.text.toString(), address_input.text.toString(), profilePicture)
            }
        }
    }

    private fun validate(): Boolean {
        name_input_layout.error = null
        address_input_layout.error = null
        if(name_input.text.isNullOrEmpty()){
            name_input_layout.error = "Please enter your name."
            return false
        }
        if(address_input.text.isNullOrEmpty()){
            address_input_layout.error = "Please enter your address."
            return false
        }
        return true
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun startImagePicker(code: Int){
        val cropIntent = CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
        cropIntent.setInitialCropWindowPaddingRatio(0f)
        cropIntent.setAspectRatio(1,1)

        startActivityForResult(cropIntent.getIntent(this), code)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PROFILE_PICTURE_CODE){
            val result = CropImage.getActivityResult(data)
            if(resultCode == Activity.RESULT_OK){
                profilePicture = FileUtils.mapUriToFile(this, result.uri)
                GlideApp.with(this).load(result.uri).into(image)
            } else {
                result?.error?.printStackTrace()
            }
        }
    }

    private fun saveDate(name: String, address: String, photo: File?){
        val nameBody = stringToRequestBody(name)
        val addressBody = stringToRequestBody(address)
        val photoPart = fileToMultipartBody(photo, "avatar")

        val dialog = indeterminateProgressDialog("Saving data...")
        dialog.setCancelable(false)
        dialog.show()

        disposables.add(RetrofitService.instance(this).create(UserApiService::class.java)
            .updateRetailer(nameBody, addressBody, photoPart)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dialog.dismiss()
                toast("Successfully updated")
                UserPref.user = it.user
                if(isNew){
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }, { err ->
                dialog.dismiss()
                err.printStackTrace()
            }))
    }
}
