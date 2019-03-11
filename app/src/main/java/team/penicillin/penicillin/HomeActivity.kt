package team.penicillin.penicillin

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_group.view.*
import org.jetbrains.anko.toast
import team.penicillin.penicillin.network.RetrofitService
import team.penicillin.penicillin.network.TransactionApiService
import team.penicillin.penicillin.network.apis.UserApiService
import team.penicillin.penicillin.network.models.Group
import team.penicillin.penicillin.utils.*

class HomeActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private var groups: List<Group> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(bottom_app_bar)

        new_group_fab.setOnClickListener {
            val intent = Intent(this, NewGroupActivity::class.java)
            startActivity(intent)
        }
        groups_recycler.bind(groups, R.layout.item_group){ group ->
            this.group_name.text = group.name
            this.member_count.text = "${group.users.size} members"
            this.status.text = group.status
            when(group.status){
                "active" -> this.status_indicator.setColorFilter(ContextCompat.getColor(context,R.color.colorStatusActive), PorterDuff.Mode.SRC_ATOP)
                "pending" -> this.status_indicator.setColorFilter(ContextCompat.getColor(context,R.color.colorStatusPending), PorterDuff.Mode.SRC_ATOP)
                "completed" -> this.status_indicator.setColorFilter(ContextCompat.getColor(context,R.color.colorStatusCompleted), PorterDuff.Mode.SRC_ATOP)
                "cancelled" -> this.status_indicator.setColorFilter(ContextCompat.getColor(context,R.color.colorStatusCancelled), PorterDuff.Mode.SRC_ATOP)
            }
            this.setOnClickListener {
                val intent = Intent(this@HomeActivity, GroupActivity::class.java)
                intent.putExtra("id", group.id)
                startActivity(intent)
            }
        }

        saveDeviceToken()
    }

    private fun saveDeviceToken(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result?.token
                RetrofitService.instance(applicationContext).create(UserApiService::class.java)
                    .saveDeviceToken(token!!)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe({
                    },{
                        it.printStackTrace()
                    })
                Log.e("FCM", token)
            })

    }

    override fun onResume() {
        super.onResume()
        updateUserData()
        getUserData()
        getGroups()
    }

    private fun getGroups(){
        disposables.add(RetrofitService.instance(this).create(TransactionApiService::class.java)
            .getGroups()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                groups = res.groups
                groups_recycler.update(groups)
            }, { err ->
                err.printStackTrace()
                toast("Can't fetch group list")
            }))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_noti, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                HomeMenuListDialogFragment().show(supportFragmentManager, "drawer_menu")
            }
            R.id.action_notifications -> {
                val intent = Intent(this, NotificationActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUserData(){
        disposables.add(RetrofitService.instance(this).create(UserApiService::class.java)
            .getProfileData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                UserPref.user = res.user
                updateUserData()
            }, { err ->
                toast("Error")
                err.printStackTrace()
            }))
    }

    private fun updateUserData(){
        if(UserPref.user!=null){
            profile_name.text = UserPref.user!!.name
            GlideApp.with(this).load(UserPref.user!!.avatar)
                .error(R.drawable.user_mark).into(profile_image)
            balance.text = UserPref.user!!.balance.toString()+ " Ks"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}
