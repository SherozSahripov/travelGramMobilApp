package com.example.travelgram.Profile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.travelgram.R
import com.example.travelgram.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_profile_setting.*


class ProfileSettingActivity : AppCompatActivity() {

    private val ACTIVITY_NO =4;
    private val TAG="ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)
        setupNavigationView()
        setupToolBar()
        fatgmentNavıgatıon()
    }

    private fun fatgmentNavıgatıon() {
        tvProfileDuzenleHesap.setOnClickListener {
            profileSettingsRoot.visibility = View.GONE
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileSettingContainer,ProfileEditFragment())
            transaction.addToBackStack("editProfileFragMentEklendi")
            transaction.commit()

        }
        tvCikiYap.setOnClickListener {
           var dialog = SignOutFragment()
            dialog.show(supportFragmentManager,"cikisYapDialogGoster")
        }
    }

    private fun setupToolBar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        profileSettingsRoot.visibility =View.VISIBLE
        super.onBackPressed()
    }

    fun setupNavigationView()
    {
        BottomNavigationViewHelper.setupNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this,bottomNavigationView)
        var menu =bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)

    }
}
