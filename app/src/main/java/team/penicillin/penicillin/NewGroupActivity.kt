package team.penicillin.penicillin

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_new_group.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import team.penicillin.penicillin.network.RetrofitService
import team.penicillin.penicillin.network.TransactionApiService
import team.penicillin.penicillin.utils.setBackgroundTint
import java.util.*

@SuppressLint("SetTextI18n")
class NewGroupActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var frequency = "daily"
    private var minPerson = 7
    private var maxPerson = 30
    private var minBalance = 1000
    private var maxBalance = 10000
    private var increment = 1000
    private var totalBalance = 1000
    private var startDate = ""
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        balance.text = minBalance.toString()
        updateCounters()

        freq_daily.setOnClickListener {
            changeFrequency("daily")
        }
        freq_weekly.setOnClickListener {
            changeFrequency("weekly")
        }
        freq_monthly.setOnClickListener {
            changeFrequency("monthly")
        }

        start_date.setOnClickListener {
            showDatePicker()
        }

        create_btn.setOnClickListener {
            if(validate()){
                createGroup()
            }
        }
    }

    private fun createGroup(){
        val dialog = indeterminateProgressDialog("Creating group")
        dialog.setCancelable(false)
        dialog.show()
        disposables.add(RetrofitService.instance(this).create(TransactionApiService::class.java)
            .createGroup(group_name.text.toString(),
                frequency, totalBalance, minPerson, maxPerson, startDate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                dialog.dismiss()
                if(res.status == "success"){
                    toast("Group created")
                    finish()
                } else {
                    toast("Group create failed")
                }
            }, { err ->
                dialog.dismiss()
                err.printStackTrace()
                toast("Error")
            }))
    }

    private fun validate(): Boolean{
        if(group_name.text.isNullOrEmpty()){
            toast("Please enter group name.")
            return false
        }
        if(startDate.isNullOrEmpty()){
            toast("Please select a start date")
            return false
        }
        return true
    }

    private fun showDatePicker(){
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
            this@NewGroupActivity,
            now.get(Calendar.YEAR), // Initial year selection
            now.get(Calendar.MONTH), // Initial month selection
            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        )
        dpd.show(supportFragmentManager, "Datepickerdialog")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        startDate = "$year/${monthOfYear+1}/$dayOfMonth"
        start_date.setText(startDate)
    }

    private fun changeFrequency(f: String){
        frequency = f
        when(f) {
            "daily" -> {
                freq_daily.setBackgroundTint(R.color.colorAccent, this)
                freq_weekly.setBackgroundTint(R.color.colorAccentFilter, this)
                freq_monthly.setBackgroundTint(R.color.colorAccentFilter, this)
                label_member_count.text = "7 - 30 members"
                minBalance = 1000
                maxBalance = 10000
                increment = 1000
                maxPerson = 30
                fee_label.text = "Daily fee"
            }
            "weekly" -> {
                freq_daily.setBackgroundTint(R.color.colorAccentFilter, this)
                freq_weekly.setBackgroundTint(R.color.colorAccent, this)
                freq_monthly.setBackgroundTint(R.color.colorAccentFilter, this)
                label_member_count.text = "7 - 16 members"
                minBalance = 10000
                maxBalance = 50000
                increment = 5000
                maxPerson = 16
                fee_label.text = "Weekly fee"
            }
            "monthly" -> {
                freq_daily.setBackgroundTint(R.color.colorAccentFilter, this)
                freq_weekly.setBackgroundTint(R.color.colorAccentFilter, this)
                freq_monthly.setBackgroundTint(R.color.colorAccent, this)
                label_member_count.text = "7 - 12 members"
                minBalance = 100000
                maxBalance = 500000
                increment = 50000
                maxPerson = 12
                fee_label.text = "Monthly fee"
            }
        }
        updateCounters()
    }

    private fun updateCounters(){
        totalBalance = minBalance
        Log.e(minBalance.toString(), maxBalance.toString())
        Log.e("Increment", increment.toString())
        balance.text = totalBalance.toString()
        balance_increment.setOnClickListener {
            if(totalBalance < maxBalance) {
                Log.e("INCREMENT","++")
                totalBalance += increment
            }
            else
                toast("Max amount reached.")
            balance.text = totalBalance.toString()
        }
        balance_decrement.setOnClickListener {
            if(totalBalance <= minBalance)
                totalBalance = 0
            else
                totalBalance -= increment

            balance.text = totalBalance.toString()
        }
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
