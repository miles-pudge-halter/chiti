package team.penicillin.penicillin.utils

import com.marcinmoskala.kotlinpreferences.PreferenceHolder
import team.penicillin.penicillin.network.models.User
import java.util.*

object UserPref: PreferenceHolder() {
    var authToken: String? by bindToPreferenceFieldNullable()
    var user: User? by bindToPreferenceFieldNullable()

    var phoneNumber: String? by bindToPreferenceFieldNullable()
    var email: String? by bindToPreferenceFieldNullable()
    var name: String? by bindToPreferenceFieldNullable()
}