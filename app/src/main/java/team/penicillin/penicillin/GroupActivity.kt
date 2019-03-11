package team.penicillin.penicillin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.item_group_member.view.*
import org.jetbrains.anko.toast
import team.penicillin.penicillin.network.RetrofitService
import team.penicillin.penicillin.network.TransactionApiService
import team.penicillin.penicillin.network.models.Group
import team.penicillin.penicillin.network.models.User
import team.penicillin.penicillin.utils.GlideApp
import team.penicillin.penicillin.utils.bind
import team.penicillin.penicillin.utils.getPrettyDate
import team.penicillin.penicillin.utils.setBackgroundTint

class GroupActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()
    private var id = 0
    private lateinit var group: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        id = intent.getIntExtra("id", 0)
        if(id == 0) finish()
    }

    override fun onResume() {
        super.onResume()
        getGroupInfo()
    }

    private fun getGroupInfo(){
        disposables.add(RetrofitService.instance(this).create(TransactionApiService::class.java)
            .getGroupDetails(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                if(res.status == "success"){
                    group = res.group
                    showData()
                } else {
                    toast(res.msg)
                }
            }, { err ->
                err.printStackTrace()
                toast("Error")
            }))
    }

    private fun showData(){
        val tempUsers = mutableListOf<User>()
        if(group.status == "active") {
            val pot = (group.amount * group.users.size).toDouble()
            group.users.forEachIndexed { i, user ->
                when (i) {
                    0 -> user.dueAmount = pot - (pot * 0.06)
                    1 -> user.dueAmount = pot - (pot * 0.05)
                    2 -> user.dueAmount = pot - (pot * 0.03)
                    3 -> user.dueAmount = pot - (pot * 0.02)
                    group.users.size - 3 -> user.dueAmount = pot + (pot * 0.02)
                    group.users.size - 2 -> user.dueAmount = pot + (pot * 0.04)
                    group.users.size - 1 -> user.dueAmount = pot + (pot * 0.05)
                }
                tempUsers.add(user)
            }
            group.users = tempUsers.toList()
        }

        group_name.text = group.name
        group_type.text = group.type
        amount.text = "${group.amount} Kyats"
        start_date.text = "Starts in ${group.startDate.getPrettyDate()}"

        group_status.text = group.status

        when(group.status){
            "active" -> group_status.setBackgroundTint(R.color.colorStatusActive, this)
            "pending" -> group_status.setBackgroundTint(R.color.colorStatusPending, this)
            "completed" -> group_status.setBackgroundTint(R.color.colorStatusCompleted, this)
            "cancelled" -> group_status.setBackgroundTint(R.color.colorStatusCancelled, this)
        }

        group_members_recycler.bind(group.users, R.layout.item_group_member){ user ->
            this.member_name.text = user.name
            this.member_due_date.text = "March 20, 2019"
//            this.member_due_amount.text = "50000 Kyats"
            if(group.status == "active"){
                this.member_due_amount.text = "${user.dueAmount} Kyats"
            }
            GlideApp.with(this).load(user.avatar).error(R.drawable.user_mark).into(this.member_image)
        }

        if(group.status != "pending")
            invite_btn.visibility = View.GONE

        invite_btn.setOnClickListener {
            val intent = Intent(this, NewInviteActivity::class.java)
            intent.putExtra("id", group.id)
            startActivity(intent)
        }
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
