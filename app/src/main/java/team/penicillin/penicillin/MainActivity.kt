package team.penicillin.penicillin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import team.penicillin.penicillin.utils.UserPref

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(1000)


        val intent = if (UserPref.authToken!=null)
            Intent(this, HomeActivity::class.java)
        else
            Intent(this, LoginActivity::class.java)

        startActivity(intent)
        finish()
    }
}
