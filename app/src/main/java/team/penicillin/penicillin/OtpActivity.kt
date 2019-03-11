package team.penicillin.penicillin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_otp.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import team.penicillin.penicillin.network.RetrofitService
import team.penicillin.penicillin.network.apis.UserApiService
import team.penicillin.penicillin.utils.UserPref

class OtpActivity : AppCompatActivity() {

    private var phone: String = ""
    private var passcode: String = ""
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        phone = intent.getStringExtra("phone")
    }
    fun onClick(view: View){
        val number : String = (view as Button).text.toString()
        when(passcode.length){
            0 -> {
                passcode = number
                field_1.text = number
            }
            1 -> {
                passcode += number
                field_2.text = number
            }
            2 -> {
                passcode += number
                field_3.text = number
            }
            3 -> {
                passcode += number
                field_4.text = number
            }
            4 -> {
                passcode += number
                field_5.text = number
                login()
            }
        }
        Log.e("The passcode is",passcode)
    }

    fun onDelete(view:View) {
        if(passcode.isNotEmpty()){
            when(passcode.length){
                1 -> field_1.text=""
                2 -> field_2.text=""
                3 -> field_3.text=""
                4 -> field_4.text=""
                5 -> field_5.text=""
            }
            passcode = passcode.substring(0,passcode.length-1)
        }
    }

    private fun login(){
        val dialog = indeterminateProgressDialog("Logging in...")
        dialog.setCancelable(false)
        dialog.show()

        disposables.add(RetrofitService.instance(this).create(UserApiService::class.java)
            .verifyOtp(phone, passcode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                dialog.dismiss()
                if(res.status == "success"){
                    RetrofitService.build(this)
                    UserPref.authToken = res.token
                    val intent = Intent(this, RegisterActivity::class.java)
                    intent.putExtra("new", true)
                    startActivity(intent)
                    finish()
                } else {
                    toast("Fail")
                }
            }, { err ->
                dialog.dismiss()
                toast("Error")
                err.printStackTrace()
            }))
    }

}
