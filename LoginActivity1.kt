package com.example.travelgram.Login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.travelgram.Model.Users
import com.example.travelgram.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login1.*

class LoginActivity1 : AppCompatActivity() {

    lateinit var  mAuth: FirebaseAuth
    lateinit var mRef : DatabaseReference
    val user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login1)

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference

        init()

    }

    private fun init() {

        btnGirisYap1.setOnClickListener {





        }
    }
    private fun OturumumAcacakKullaniciDenet(emailPhoneNumberOrUsername: String, sifre: String) {
        mRef.child("users").orderByChild("email").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {

                for (ds in p0!!.children){
                    var okunanKullanici = ds.getValue(Users::class.java)

                    if (okunanKullanici!!.email!!.toString().equals(emailPhoneNumberOrUsername))
                    {
                        oturumAc(okunanKullanici,sifre,false)
                        break
                    }
                    else if (okunanKullanici!!.user_name!!.toString().equals(emailPhoneNumberOrUsername))
                    {
                        oturumAc(okunanKullanici,sifre,false)
                        break
                    }
                    else if (okunanKullanici!!.phone_number!!.toString().equals(emailPhoneNumberOrUsername))
                    {
                        oturumAc(okunanKullanici,sifre,true)
                        break
                    }

                }

            }


        })

    }

    private fun oturumAc(okunanKullanici: Users, sifre: String, telefonIleGiris: Boolean) {

        var girisYapacakEmail = ""

        if (telefonIleGiris == true)
        {
            girisYapacakEmail = okunanKullanici.email_phone_number.toString()

        }
        else
        {
            girisYapacakEmail = okunanKullanici.email.toString()
        }

        mAuth.signInWithEmailAndPassword(girisYapacakEmail, sifre)
            .addOnCompleteListener(object : OnCompleteListener <AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0!!.isSuccessful)
                    {
                        Toast.makeText(this@LoginActivity1,"Oturum Acildi : " + mAuth.currentUser!!.uid,Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this@LoginActivity1,"Şifre veya Kullanıcı adı hatalı",Toast.LENGTH_SHORT).show()
                    }
                }


            })

    }

}













