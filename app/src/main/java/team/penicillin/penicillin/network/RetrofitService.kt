package team.penicillin.penicillin.network

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import team.penicillin.penicillin.utils.UserPref
import java.util.concurrent.TimeUnit

object RetrofitService {
    private const val X_AUTH_TOKEN = "token"

    val BASE_URL = "http://api.springinle.com"

    private var authToken = ""

    private var retrofit: Retrofit? = null


    fun instance(context: Context): Retrofit {

        if (retrofit == null)
            retrofit = build(context)
        else if (authToken != UserPref.authToken)
            retrofit = build(context)

        return retrofit!!
    }


    fun build(context: Context): Retrofit {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(60, TimeUnit.SECONDS)

        if(UserPref.authToken!=null)
            authToken = UserPref.authToken!!

        val client = builder.addInterceptor { chain ->
            val org = chain.request()
            val request = org.newBuilder().addHeader(X_AUTH_TOKEN, authToken).method(org.method(), org.body()).build()
            chain.proceed(request)
        }.addInterceptor(ChuckInterceptor(context)).addNetworkInterceptor(StethoInterceptor())
            .followSslRedirects(true).followRedirects(true).build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).build()
    }
}