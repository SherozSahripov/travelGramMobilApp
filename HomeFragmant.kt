package com.example.travelgram.Home

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.travelgram.Login.LoginActivity
import com.example.travelgram.Model.Posts
import com.example.travelgram.Model.UserPosts
import com.example.travelgram.Model.Users
import com.example.travelgram.R
import com.example.travelgram.utils.BottomNavigationViewHelper
import com.example.travelgram.utils.HomeFragmentRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragmant: Fragment() {


    private val ACTIVITY_NO =0;
    lateinit var fragmentView: View


    lateinit var tumGonderiler:ArrayList<UserPosts>
    lateinit var tumTakipEttiklerim:ArrayList<String>

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView=inflater?.inflate(R.layout.fragment_home,container,false)


        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        tumGonderiler = ArrayList<UserPosts>()
        tumTakipEttiklerim=ArrayList<String>()


        tumTakipEttiklerimiziGetir()

        kullaniciPostleriniGetir()


        fragmentView.imgTabCamera.setOnClickListener {

            (activity as MainActivity).homeViewPager.setCurrentItem(0)
        }
        fragmentView.imgDirectMesaj.setOnClickListener {

            (activity as MainActivity).homeViewPager.setCurrentItem(2)
        }

        return fragmentView
    }

    private fun tumTakipEttiklerimiziGetir() {
        //Bu method firebase ile baglanip oradaki diger kullanicilarin id'sine erisip onlarin payl
        //paylasimlarini getirecek

        tumTakipEttiklerim.add(mUser.uid)

        mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(
            object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    if (p0!!.getValue()!=null)  {

                        for (ds in p0!!.children){
                            tumTakipEttiklerim.add(ds.key!!)
                        }
                        Log.e("Hata","Tumtakipettiklerim: " +tumTakipEttiklerim.toString())
                        kullaniciPostleriniGetir()
                    }
                    else{


                    }

                }


            }
        )


    }

    //Method icindeki paramete takip ettigimiz kisilerindir ileride kullanilacaktir
    private fun kullaniciPostleriniGetir() {

        mRef = FirebaseDatabase.getInstance().reference

        for (i in 0..tumTakipEttiklerim.size-1){

            var kullaniciID = tumTakipEttiklerim.get(i)

            mRef.child("users").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    var userID= kullaniciID
                    var kullaniciadi= p0!!.getValue(Users::class.java)!!.user_name
                    var kullaniciPhotoURL = p0!!.getValue(Users::class.java)!!.usersDetails!!.profile_picture



                    mRef.child("posts").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0!!.hasChildren()){

                                Log.e("HATA",kullaniciID   +"idli kisini fotograflari var ")

                                for (ds  in p0!!.children)
                                {
                                    var eklenecekkUserPosts = UserPosts()
                                    eklenecekkUserPosts.userID= userID
                                    eklenecekkUserPosts.userName = kullaniciadi
                                    eklenecekkUserPosts.userPhotoURL = kullaniciPhotoURL
                                    eklenecekkUserPosts.postID = ds.getValue(Posts::class.java)!!.post_id
                                    eklenecekkUserPosts.postURL = ds.getValue(Posts::class.java)!!.file_url
                                    eklenecekkUserPosts.postAciklama = ds.getValue(Posts::class.java)!!.aciklama
                                    eklenecekkUserPosts.postYuklemeTarih = ds.getValue(Posts::class.java)!!.yukleme_tarihi

                                    tumGonderiler.add(eklenecekkUserPosts)

                                }
                            }else  {

                                Log.e("HATA",kullaniciID   +"idli kisini fotograflari yok ")
                            }
                            Log.e("HATA",kullaniciID   +"idli kisini fotograflari var sayisi " + tumGonderiler.size)
                            if (i >= tumTakipEttiklerim.size-1)
                            setupRecyclerView()

                        }
                    })

                }

            })

            //////
        }


    }

    private fun setupRecyclerView() {

        var recyclerView=fragmentView.recyclerview
        var recycleradapter = HomeFragmentRecyclerAdapter(this.activity!!,tumGonderiler)

        recyclerView.adapter=recycleradapter
        recyclerView.layoutManager=LinearLayoutManager(this.activity!!,LinearLayoutManager.VERTICAL,false)

    }


    fun setupNavigationView()
    {
        var fragmentBottomNavView = fragmentView.bottomNavigationView
        BottomNavigationViewHelper.setupNavigationView(fragmentBottomNavView)
        BottomNavigationViewHelper.setupNavigation(activity!!,fragmentBottomNavView)
        var menu =fragmentBottomNavView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)

    }
    private fun setupAuthListener() {



        mAuthListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var user = FirebaseAuth.getInstance().currentUser
                if (user == null)
                {

                    //Burada koşullar eğer sağlanırsa Bize direkt giriş yapmamızı sağlıyacaktır.
                    var intent = Intent(activity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK  or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    activity!!.finish()
                }
                else{

                }
            }

        }
    }

    override fun onResume() {
        setupNavigationView()
        super.onResume()
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