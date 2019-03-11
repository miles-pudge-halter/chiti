package team.penicillin.penicillin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import team.penicillin.penicillin.network.RetrofitService
import team.penicillin.penicillin.network.apis.UserApiService


class LoginActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener {
            login("+959"+phone_input.text.toString())
        }
    }

    private fun login(phone: String){
        val dialog = indeterminateProgressDialog("Requesting OTP...")
        dialog.setCancelable(false)
        dialog.show()
        disposables.add(RetrofitService.instance(this).create(UserApiService::class.java)
            .requestOtp(phone)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                dialog.dismiss()
                if(res.status=="success"){
                    val intent = Intent(this, OtpActivity::class.java)
                    intent.putExtra("phone", phone)
                    startActivity(intent)
                    finish()
                } else {
                    toast("Failed")
                }
            }, { err ->
                dialog.dismiss()
                toast("Error")
                err.printStackTrace()
            }))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

}
