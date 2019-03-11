package team.penicillin.penicillin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_join_group.*
import kotlinx.android.synthetic.main.item_group_member.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import team.penicillin.penicillin.network.RetrofitService
import team.penicillin.penicillin.network.TransactionApiService
import team.penicillin.penicillin.network.models.GroupInvitation
import team.penicillin.penicillin.utils.GlideApp
import team.penicillin.penicillin.utils.bind
import team.penicillin.penicillin.utils.getPrettyDate

class JoinGroupActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()
    private var id = 0
    private lateinit var invitation: GroupInvitation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_group)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        id = intent.getIntExtra("id", 0)
        if(id == 0) finish()
    }

    override fun onResume() {
        super.onResume()
        getInvitationInfo()
    }

    private fun getInvitationInfo(){
        disposables.add(RetrofitService.instance(this).create(TransactionApiService::class.java)
            .getInvitationDetails(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                if(res.status == "success"){
                    invitation = res.invitation
                    showInvitation()
                } else {
                    toast(res.msg)
                }
            }, { err ->
                err.printStackTrace()
                toast("Error")
            }))
    }

    private fun showInvitation(){
        group_name.text = invitation.group.name
        group_type.text = invitation.group.type
        amount.text = "${invitation.group.amount} Kyats"
        start_date.text = "Starts in ${invitation.group.startDate.getPrettyDate()}"
        group_members_recycler.bind(invitation.group.users, R.layout.item_group_member){ user ->
            this.member_name.text = user.name
            this.member_due_amount.visibility = View.GONE
            this.member_due_date.visibility = View.GONE
//            this.member_due_date.text = "March 20, 2019"
//            this.member_due_amount.text = "50000 Kyats"
            GlideApp.with(this).load(user.avatar).error(R.drawable.user_mark).into(this.member_image)
        }

        join_btn.setOnClickListener {
            alert("Do you want to join ${invitation.group.name}?", "Join Group"){
                also{
                    ctx.setTheme(R.style.CustomAlertDialog)
                }
                positiveButton("Join"){
                    disposables.add(RetrofitService.instance(this@JoinGroupActivity).create(TransactionApiService::class.java)
                        .acceptInvitation(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ res ->
                            if(res.status == "success"){
                                toast("Joined group")
                                val intent = Intent(this@JoinGroupActivity, GroupActivity::class.java)
                                intent.putExtra("id", res.group.id)
                                startActivity(intent)
                                finish()
                            } else {
                                toast(res.msg)
                            }
                        }, { err ->
                            err.printStackTrace()
                        }))
                }
                negativeButton("Cancel"){
                    it.dismiss()
                }
            }.show()
        }
    }
}
