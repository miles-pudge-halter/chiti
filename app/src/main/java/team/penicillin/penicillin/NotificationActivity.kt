package team.penicillin.penicillin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.item_notification.view.*
import org.jetbrains.anko.toast
import team.penicillin.penicillin.network.RetrofitService
import team.penicillin.penicillin.network.apis.UserApiService
import team.penicillin.penicillin.network.models.NotiData
import team.penicillin.penicillin.utils.bind
import team.penicillin.penicillin.utils.update

class NotificationActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()
    private var notis: List<NotiData> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        notis_recycler.bind(notis, R.layout.item_notification){ noti ->
            this.noti_title.text = noti.title
            this.noti_body.text = noti.body

            this.setOnClickListener {
                when(noti.notiType) {
                    "invitation" ->{
                        val intent = Intent(context, JoinGroupActivity::class.java)
                        intent.putExtra("id", noti.dataId)
                        startActivity(intent)
                    }
                }
            }
        }

        notis_recycler.setOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(notis_recycler.canScrollVertically(-1))
                    supportActionBar?.elevation = 12f
                else
                    supportActionBar?.elevation = 0f
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getNotifications()
    }

    private fun getNotifications(){
        disposables.add(RetrofitService.instance(this).create(UserApiService::class.java)
            .getNotifications()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it.status == "success"){
                    notis = it.notis
                    notis_recycler.update(notis)
                    if(notis.isEmpty())
                        no_result_layout.visibility = View.VISIBLE
                    else
                        no_result_layout.visibility = View.GONE
                } else{
                    toast(it.msg)
                }
            }, { err ->
                err.printStackTrace()
                toast("Error")
            }))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
