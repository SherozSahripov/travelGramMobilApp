package com.example.travelgram.Home

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.example.travelgram.Login.LoginActivity
import com.example.travelgram.R
import com.example.travelgram.utils.BottomNavigationViewHelper
import com.example.travelgram.utils.EventBusDataEvents
import com.example.travelgram.utils.HomePagerAdapter
import com.example.travelgram.utils.UniversalImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import kotlin.math.sign

class MainActivity : AppCompatActivity() {

    private val ACTIVITY_NO =0;
    private val TAG="HomeActivity"

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()

        initImageLoader()
        setupHomeViewPager()

      //  FirebaseAuth.getInstance().signOut()


    }

    override fun onResume() {
        super.onResume()
    }



    private fun setupHomeViewPager()
    {
        var homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        homePagerAdapter.addFragment(CameraFragment()) //0
        homePagerAdapter.addFragment(HomeFragmant()) //1
        homePagerAdapter.addFragment(MesaggesFragment()) //2

        //Activity main de bulunan view pagera olusturdugumuz adaptoru atadik
        homeViewPager.adapter=homePagerAdapter

        //view pagerin homeFragment ile baglanmasini sagladik
        homeViewPager.setCurrentItem(1)

        homePagerAdapter.secilenFragmentiVievPagerDenSilme(homeViewPager,0)
        homePagerAdapter.secilenFragmentiVievPagerDenSilme(homeViewPager,2)

        homeViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {


            }


            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {


            }

            override fun onPageSelected(position: Int) {

                if (position == 0)
                {
                    this@MainActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    this@MainActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

                    storageAndCameraPermision()
                    homePagerAdapter.secilenFragmentiVievPagerDenSilme(homeViewPager,1)
                    homePagerAdapter.secilenFragmentiVievPagerDenSilme(homeViewPager,2)
                    homePagerAdapter.secilenFragmentiViewpPafereEkleme(homeViewPager,0)



                }
                if(position == 1)
                {
                    this@MainActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@MainActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

                    homePagerAdapter.secilenFragmentiVievPagerDenSilme(homeViewPager,0)
                    homePagerAdapter.secilenFragmentiVievPagerDenSilme(homeViewPager,2)
                    homePagerAdapter.secilenFragmentiViewpPafereEkleme(homeViewPager,1)
                }

                if (position == 2)
                {
                    this@MainActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@MainActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

                    homePagerAdapter.secilenFragmentiVievPagerDenSilme(homeViewPager,0)
                    homePagerAdapter.secilenFragmentiVievPagerDenSilme(homeViewPager,1)
                    homePagerAdapter.secilenFragmentiViewpPafereEkleme(homeViewPager,2)
                }

            }


        })
    }

    private fun storageAndCameraPermision() {

        Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    if (report!!.areAllPermissionsGranted()){
                        Log.e("Hata"," Izinler hepsi verilmistir ")
                        EventBus.getDefault().postSticky(EventBusDataEvents.KameraIzinBilgisiGonder(true))
                    }
                    //Bir daha sorma dedigi onay napcagini burada soleriz
                    else if (report!!.isAnyPermissionPermanentlyDenied){

                        Log.e("Hata"," Izinlerin birisi verilmemistir ")



                        var builder = AlertDialog.Builder(this@MainActivity)
                        builder.setTitle("Izin Gerekli")
                        builder.setMessage("Ayarlardan uygulamaya izin vermeniz gerekiyor")
                        builder.setPositiveButton("Ayarlara git ",object  : DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()
                                var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                var uri= Uri.fromParts("package",packageName,null)
                                intent.setData(uri)
                                startActivity(intent)
                                homeViewPager.setCurrentItem(1)
                            }
                        })
                        builder.setNegativeButton("Iptal Et ",object  : DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()
                                homeViewPager.setCurrentItem(1)
                                finish()
                            }
                        })
                        builder.show()

                    }
                    else
                    {
                        finish()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    Log.e("Hata"," Izinlerden herhangi biri reddedilmistir ikna etmen lazim ")
                    var builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Izin Gerekli")
                    builder.setMessage("Uygulama izin verilmemişsiniz. İzin vermek istiyor musunuz ?")
                    builder.setPositiveButton("Onay Ver ",object  : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.cancel()
                            token!!.continuePermissionRequest()
                        }
                    })
                    builder.setNegativeButton("Iptal Et ",object  : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.cancel()
                            token!!.cancelPermissionRequest()
                            homeViewPager.setCurrentItem(1)
                            finish()
                        }
                    })
                    builder.show()



                }
            })
            .withErrorListener(object : PermissionRequestErrorListener {
                override fun onError(error: DexterError?) {
                    Log.e("HATA",error!!.toString())
                }
            }).check()



    }


    override fun onBackPressed() {

        if (homeViewPager.currentItem == 1)
        {
            homeViewPager.visibility = View.VISIBLE
            homeFragmentConteiner.visibility=View.GONE

        }
        else
        {
            homeViewPager.visibility = View.VISIBLE
            homeFragmentConteiner.visibility=View.GONE
            homeViewPager.setCurrentItem(1)
        }

    }

    private fun initImageLoader()
    {
        var universalImageLoader= UniversalImageLoader(this)
        ImageLoader.getInstance().init(universalImageLoader.config)
    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var user = FirebaseAuth.getInstance().currentUser
                if (user == null)
                {

                    //Burada koşullar eğer sağlanırsa Bize direkt giriş yapmamızı sağlıyacaktır.
                    var intent = Intent(this@MainActivity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK  or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                else{

                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener (mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }

}
