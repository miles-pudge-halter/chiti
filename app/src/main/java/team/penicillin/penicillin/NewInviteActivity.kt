package team.penicillin.penicillin

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_new_invite.*
import kotlinx.android.synthetic.main.item_user.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import team.penicillin.penicillin.network.RetrofitService
import team.penicillin.penicillin.network.apis.UserApiService
import team.penicillin.penicillin.network.models.SearchUser
import team.penicillin.penicillin.utils.GlideApp
import team.penicillin.penicillin.utils.bind
import team.penicillin.penicillin.utils.update

class NewInviteActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()
    private var userList: List<SearchUser> = listOf()
    private var groupId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_invite)

        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = ""

        groupId = intent.getIntExtra("id", 0)
        if(groupId == 0) finish()

        search_recycler.bind(userList, R.layout.item_user){ user ->
            this.user_name.text = user.name
            this.user_phone.text = user.phone
            GlideApp.with(this).load(user.avatar).error(R.drawable.user_mark).into(this.user_image)

            this.setOnClickListener {
                alert("Do you want to invite ${user.name} to your group?", "Invite member"){
                    also {
                        ctx.setTheme(R.style.CustomAlertDialog)
                    }
                    positiveButton("Invite") { dialogInterface ->
                        RetrofitService.instance(context).create(UserApiService::class.java)
                            .inviteToGroup(user.phone, groupId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ res ->
                                if(res.status == "success"){
                                    toast("User invited.")
                                    finish()
                                } else
                                    toast(res.message)
                            }, { err ->
                                err.printStackTrace()
                                toast("Error")
                            })
                        dialogInterface.dismiss()
                    }
                    negativeButton("Cancel"){ dialogInterface ->
                        dialogInterface.dismiss()
                    }
                }.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getUsers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView.setIconifiedByDefault(false)
        searchView.queryHint = "Search users"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean = true
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrEmpty()){
                    searchUser(query)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun getUsers(){
        disposables.add(RetrofitService.instance(this).create(UserApiService::class.java)
            .getUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                if(res.status == "success"){
                    userList = res.users
                    search_recycler.update(userList)
                } else {
                    toast(res.msg)
                }
            }, { err ->
                err.printStackTrace()
                toast("Error")
            }))
    }

    private fun searchUser(query: String){
        val searchedList = mutableListOf<SearchUser>()
        userList.forEach{
            if(it.name.toLowerCase().contains(query) || it.phone.contains(query))
                searchedList.add(it)
        }
        search_recycler.update(searchedList)

        if(searchedList.isEmpty())
            no_result_layout.visibility = View.VISIBLE
        else
            no_result_layout.visibility = View.GONE
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
