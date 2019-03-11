package team.penicillin.penicillin

import android.app.Application
import com.google.gson.Gson
import com.marcinmoskala.kotlinpreferences.PreferenceHolder
import com.marcinmoskala.kotlinpreferences.gson.GsonSerializer

class ChitiApp: Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenceHolder.setContext(this)
        PreferenceHolder.serializer = GsonSerializer(Gson())
    }
}