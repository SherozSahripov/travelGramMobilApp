package com.example.travelgram.Profile

import android.content.Intent
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.travelgram.Login.LoginActivity
import com.example.travelgram.Model.Posts
import com.example.travelgram.Model.UserPosts
import com.example.travelgram.Model.Users
import com.example.travelgram.R
import com.example.travelgram.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.greenrobot.eventbus.EventBus



class ProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO =4
    private val TAG="ProfileActivity"
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser:FirebaseUser
    lateinit var mRef:DatabaseReference
    lateinit var tumGonderuler: ArrayList<UserPosts>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference

        tumGonderuler = ArrayList<UserPosts>()

        setupToolBar()

        setupNavigationView()

        KullaniciBilgileriniGetir()

        kullaniciPostlariniGetir(mUser.uid)


        imgGrid.setOnClickListener {

            setupRecycleView(1)
        }

        imgList.setOnClickListener {


            setupRecycleView(2)
        }

    }
  //burada databse te oluturdugum kullanici detaillerini getirmekteyim
    //Bu method ileride enim isime co yarayacaktir.
    private fun KullaniciBilgileriniGetir() {


      tvProfileDuzenleButton.isEnabled =false
      imgProfileSetting.isEnabled =true

        mRef.child("users").child(mUser!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0!!.getValue() != null){

                    var okunanKullaniciBilgileri = p0!!.getValue(Users::class.java)


                    //EventBus sinifini realtime verilerin degismesi
                    //EventBuste olusturdugum sinifin bir nesnesini olusturlamim

                    EventBus.getDefault().postSticky(EventBusDataEvents.KullaniciBilgileriGonder(okunanKullaniciBilgileri))
                    tvProfileDuzenleButton.isEnabled =true
                    imgProfileSetting.isEnabled =true


                    tvProfileNameToolBar.setText(okunanKullaniciBilgileri!!.user_name)
                    kullanici_ad_soyad.setText(okunanKullaniciBilgileri!!.ad_soyad)
                    tvFollowerSayisi.setText(okunanKullaniciBilgileri!!.usersDetails!!.follower)
                    tvfollowingSayisi.setText(okunanKullaniciBilgileri!!.usersDetails!!.following)
                    tvGonderiSaysi.setText(okunanKullaniciBilgileri!!.usersDetails!!.post)


                    //Profile Resmi buradan alinacaktir
                    var imgUrl: String  =  okunanKullaniciBilgileri!!.usersDetails!!.profile_picture!!
                    UniversalImageLoader.setImage(imgUrl,circleProfileImage,progressBar1,"")


                    if (!okunanKullaniciBilgileri!!.usersDetails!!.biography!!.isNullOrEmpty()){
                        tv_Biyografi.visibility = View.VISIBLE
                        tv_Biyografi.setText(okunanKullaniciBilgileri!!.usersDetails!!.biography!!)
                    }
                    else
                    {
                        tv_Biyografi.visibility = View.GONE
                    }
                    if (!okunanKullaniciBilgileri!!.usersDetails!!.web_site.isNullOrEmpty()){
                        tvWebSitesi.visibility = View.VISIBLE
                        tvWebSitesi.setText(okunanKullaniciBilgileri.usersDetails!!.web_site)
                    }
                    else
                    {
                        tvWebSitesi.visibility = View.GONE
                    }
                }


            }


        })

    }

    private fun setupToolBar() {
        imgProfileSetting.setOnClickListener {
            var intent = Intent(this,ProfileSettingActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)

        }

        tvProfileDuzenleButton.setOnClickListener {

            tumLayout.visibility = View.INVISIBLE
            profileContainer.visibility = View.VISIBLE
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileContainer,ProfileEditFragment())
            transaction.addToBackStack( "signOutFragMentEklendi")
            transaction.commit()

        }
    }

    override fun onResume() {
        setupNavigationView()
        super.onResume()
    }

    fun setupNavigationView()
    {
        BottomNavigationViewHelper.setupNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this,bottomNavigationView)
        var menu =bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)

    }

    private fun kullaniciPostlariniGetir(kullaniciID: String) {

        mRef.child("users").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var userID = kullaniciID
                var kullaniciAdi = p0!!.getValue(Users::class.java)!!.user_name
                var kullaniciFotoURL = p0!!.getValue(Users::class.java)!!.usersDetails!!.profile_picture


                mRef.child("posts").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0!!.hasChildren()) {
                            //Log.e("HATA","COCUK VAR")
                            for (ds in p0!!.children) {

                                var eklenecekUserPosts = UserPosts()

                                eklenecekUserPosts.userID = userID
                                eklenecekUserPosts.userName = kullaniciAdi
                                eklenecekUserPosts.userPhotoURL = kullaniciFotoURL
                                eklenecekUserPosts.postID = ds.getValue(Posts::class.java)!!.post_id
                                eklenecekUserPosts.postURL = ds.getValue(Posts::class.java)!!.file_url
                                eklenecekUserPosts.postAciklama = ds.getValue(Posts::class.java)!!.aciklama
                                eklenecekUserPosts.postYuklemeTarih = ds.getValue(Posts::class.java)!!.yukleme_tarihi

                                tumGonderuler.add(eklenecekUserPosts)

                            }


                        }

                        setupRecycleView(1)

                    }
                })
            }

        })
    }
   //1 Ise grid 2 ise list verileri gosterilir
    private fun setupRecycleView(layoutCesidi: Int) {

        if (layoutCesidi == 1)
        {

            imgGrid.setColorFilter(ContextCompat.getColor(this,R.color.mavi),PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this,R.color.siyah),PorterDuff.Mode.SRC_IN)

            var kullaniciPosteListe = profileRecyclerView
            kullaniciPosteListe.adapter = ProfilePostRecyclerAdaptor(tumGonderuler,this)

            kullaniciPosteListe.layoutManager = GridLayoutManager(this,3)

        }
       else if(layoutCesidi == 2)
        {
            imgGrid.setColorFilter(ContextCompat.getColor(this,R.color.siyah),PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this,R.color.mavi),PorterDuff.Mode.SRC_IN)
            var kullaniciPosteListe = profileRecyclerView
            kullaniciPosteListe.adapter = ProfilePostListRecyclerAdapter(this,tumGonderuler)
            kullaniciPosteListe.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        }

    }

    override fun onBackPressed() {
        tumLayout.visibility = View.VISIBLE
        profileContainer.visibility= View.INVISIBLE
        super.onBackPressed()
    }
    private fun setupAuthListener() {



        mAuthListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var user = FirebaseAuth.getInstance().currentUser
                if (user == null)
                {

                    //Burada koşullar eğer sağlanırsa Bize direkt giriş yapmamızı sağlıyacaktır.
                    var intent = Intent(this@ProfileActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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
