package team.penicillin.penicillin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_homemenu_list_dialog.*
import org.jetbrains.anko.toast
import team.penicillin.penicillin.utils.UserPref

class HomeMenuListDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_homemenu_list_dialog, container, false)
    }

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigation_view.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.action_account -> {
                    val intent = Intent(context, RegisterActivity::class.java)
                    intent.putExtra("new", false)
                    startActivity(intent)
                }
                R.id.action_transactions -> context?.toast("Menu 1")
                R.id.action_logout -> {
                    UserPref.clear()
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }
}
