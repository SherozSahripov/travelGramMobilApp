package com.example.travelgram.Login

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.travelgram.Home.MainActivity
import com.example.travelgram.R
import com.example.travelgram.utils.EventBusDataEvents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener:FirebaseAuth.AuthStateListener
    lateinit var manager: FragmentManager
    //lateinit var mRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupAuthListener()

        mAuth =FirebaseAuth.getInstance()
       //mAuth.signOut()
        // mRef = FirebaseDatabase.getInstance().reference
        //Hocam şuraaı beni
        //Gördünüz mü
        manager = supportFragmentManager
        manager.addOnBackStackChangedListener(this)
        init()
        //selam hata neydi
    }

    private fun init() {


        txt_giri_yap.setOnClickListener {

            var intent =
                Intent(this@RegisterActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
      startActivity(intent)
            finish()
        }

        tvEposta.setOnClickListener {
            tlGolgelik.visibility = View.INVISIBLE
            epGolgelik.visibility = View.VISIBLE
            edtNumber.setText("")
            edtNumber.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            edtNumber.setHint("E-Posta")


            btnRegisterIlerle.isEnabled = false
            btnRegisterIlerle.setTextColor((ContextCompat.getColor(this@RegisterActivity, R.color.sonokmavi)))
            btnRegisterIlerle.setBackgroundColor(R.drawable.register_button)
        }
        txtTelefon.setOnClickListener {
            tlGolgelik.visibility = View.VISIBLE
            epGolgelik.visibility = View.INVISIBLE
            edtNumber.setText("")
            edtNumber.inputType = InputType.TYPE_CLASS_NUMBER
            edtNumber.setHint("Telefon")



            btnRegisterIlerle.isEnabled = false
            btnRegisterIlerle.setTextColor((ContextCompat.getColor(this@RegisterActivity, R.color.sonokmavi)))
            btnRegisterIlerle.setBackgroundColor(R.drawable.register_button)

        }

        edtNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (start + before + count >= 10) {
                    btnRegisterIlerle.isEnabled = true
                    btnRegisterIlerle.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.beyaz))
                    btnRegisterIlerle.setBackgroundResource(R.drawable.register_button_active)
                } else {
                    btnRegisterIlerle.isEnabled = false
                    btnRegisterIlerle.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.sonokmavi))
                    btnRegisterIlerle.setBackgroundResource(R.drawable.register_button)



                    btnRegisterIlerle.isEnabled = false
                    btnRegisterIlerle.setTextColor((ContextCompat.getColor(this@RegisterActivity, R.color.sonokmavi)))
                    btnRegisterIlerle.setBackgroundColor(R.drawable.register_button)
                }

            }
        })

        btnRegisterIlerle.setOnClickListener {
            if (edtNumber.hint.toString().equals("Telefon")) {

                var cepTellefonKullanimdaMi = false

                if (isValidTelefon(edtNumber.text.toString())) {

                    loginRoot.visibility = View.GONE
                    loginContainer.visibility = View.VISIBLE

                    var transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.loginContainer, TelefonKoduGirFragment())
                    transaction.addToBackStack("Entered tlefon Code Frajment")
                    transaction.commit()
                    EventBus.getDefault().postSticky(
                        EventBusDataEvents.KayitBilgileriGonder(
                            edtNumber.text.toString(),
                            null,
                            null,
                            null,
                            false
                        )
                    )

                } else {
                    Toast.makeText(this, "Lutfen gecerli bir telefon numarasi giriniz ", Toast.LENGTH_SHORT).show()
                }
            } else {

                if (isValidEmail(edtNumber.text.toString())) {
                    /////Buradaaaaaa ayni email ile girisini kontrol etmisimdir ileride duzelririlir--------************

                    /* var mailInUse = false

                     mRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener{
                         override fun onCancelled(p0: DatabaseError) {

                         }

                         override fun onDataChange(p0: DataSnapshot) {

                             if (p0.getValue()!=null)
                             {
                                 for (user in p0!!.children)
                                 {
                                     var okunanKullanici = user.getValue(Users::class.java)
                                     if (okunanKullanici!!.email!!.equals(edtNumber.text.toString()))
                                     {
                                         Toast.makeText(this@RegisterActivity,"Bu email ile daha once Kayit Yapildi",Toast.LENGTH_SHORT).show()
                                         mailInUse =
                                         break
                                     }

                                 }
                             }
                         }

                     })*/
                    loginRoot.visibility = View.GONE
                    loginContainer.visibility = View.VISIBLE
                    var transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.loginContainer, KayitFragment())
                    transaction.addToBackStack("Entering With Email")
                    transaction.commit()
                    EventBus.getDefault().postSticky(
                        EventBusDataEvents.KayitBilgileriGonder(
                            null,
                            edtNumber.text.toString(),
                            null,
                            null,
                            true
                        )
                    )
                } else {
                    Toast.makeText(this, "Lutfen gecerli bir Email adresi  giriniz ", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun onBackStackChanged() {

        val elemanSayi = manager.backStackEntryCount
        if (elemanSayi == 0) {
            loginRoot.visibility = View.VISIBLE

        }
    }

    fun isValidEmail(kontrolEdilecekEmail: String): Boolean {
        if (kontrolEdilecekEmail == null) {
            return false
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(kontrolEdilecekEmail).matches()
    }

    fun isValidTelefon(kontrolEdilecekTelefon: String): Boolean {
        if (kontrolEdilecekTelefon == null || kontrolEdilecekTelefon.length > 14) {
            return false
        }
        return android.util.Patterns.PHONE.matcher(kontrolEdilecekTelefon).matches()
    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var user = FirebaseAuth.getInstance().currentUser
                if (user != null)
                {

                    var intent = Intent(this@RegisterActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                }
                else{

                }
            }
            //buraya mainactivitye gitme kodunu yazman lazım, oturum açıksa hemen oraya yonlenmesi için

        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener  (mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
