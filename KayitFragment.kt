package com.example.travelgram.Login


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.example.travelgram.Model.Users
import com.example.travelgram.Model.UsersDetails

import com.example.travelgram.R
import com.example.travelgram.utils.EventBusDataEvents
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_kayit.*
import kotlinx.android.synthetic.main.fragment_kayit.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class KayitFragment : Fragment() {

    var telNo = ""
    var verificationID = ""
    var gelenKod = ""
    var gelenEmail = ""
    var emailKayitIslemi = true
    lateinit var mAuth: FirebaseAuth
    lateinit var mRef: DatabaseReference
    lateinit var progressBar : ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_kayit, container, false)

        progressBar = view.progressBarKullaniciKayit
        mAuth = FirebaseAuth.getInstance()

        view.tvGirisYap.setOnClickListener {

            var intent = Intent(activity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
//Hocam şu giriş yap düğmesine basarken beni   başka syfaya yönlendiriyor
        //hangisi anlamadım
        //Bi saniye bi çıkış yapim
        mRef = FirebaseDatabase.getInstance().reference

        view.etAdSoyad.addTextChangedListener(watcher)
        view.etSifre.addTextChangedListener(watcher)
        view.etKullaniciAdi.addTextChangedListener(watcher)

        view.btnGirisYap.setOnClickListener {

            progressBar.visibility =View.VISIBLE

            // Email ile giris yapmak isteyen kullanicilar
            if (emailKayitIslemi) {
                var sifre = view.etSifre.text.toString()
                var adSoyad = view.etAdSoyad.text.toString()
                var userName = view.etKullaniciAdi.text.toString()
                //var credential = view.etSifre.text.toString()

                mAuth.createUserWithEmailAndPassword(gelenEmail, sifre)
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {

                            if (p0!!.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    "Oturum email ile acildi" + mAuth.currentUser!!.uid,
                                    Toast.LENGTH_SHORT
                                ).show()
                                var userID = mAuth.currentUser!!.uid.toString()
                                //Save the user information on database

                                var kaydedilecekKullaniciDtaylari = UsersDetails("0","0","0","","","")
                                var kaydedilecekKullanici = Users(gelenEmail, sifre, userName, adSoyad,"","", userID,kaydedilecekKullaniciDtaylari)
                                // mRef.child("users").child(userID).setValue(kaydedilecekKullanici)
                                mRef.child("users").child(userID).setValue(kaydedilecekKullanici)
                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                        override fun onComplete(p0: Task<Void>) {
                                            //Eger kullanici kayitli ise
                                            if (p0.isSuccessful) {
                                                Toast.makeText(activity, "User is Saved", Toast.LENGTH_SHORT).show()
                                                progressBar.visibility=View.INVISIBLE
                                            } else {
                                                progressBar.visibility=View.INVISIBLE
                                                mAuth.currentUser!!.delete()
                                                    .addOnCompleteListener(object : OnCompleteListener<Void>{
                                                        override fun onComplete(p0: Task<Void>) {
                                                            if (p0!!.isSuccessful)
                                                            {
                                                                Toast.makeText(activity, "User is not Saved try again", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }

                                                    })
                                            }
                                        }

                                    })
                            } else {
                                progressBar.visibility=View.INVISIBLE
                                Toast.makeText(activity, "Oturum Acilamadi" + p0.exception, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }


            //Tel No ile firis yapmak istersek kullanilacak olan kisim
            else {

                var sifre = view.etSifre.text.toString()
                var adSoyad = view.etAdSoyad.text.toString()
                var userName = view.etKullaniciAdi.toString()
                var sahteEmail = telNo + "@sheroz.com"    //+905397349752@sheroz.com demek

                mAuth.createUserWithEmailAndPassword(sahteEmail, sifre)
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {

                            if (p0!!.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    "Oturum Tel No ile acildi" + mAuth.currentUser!!.uid,
                                    Toast.LENGTH_SHORT
                                ).show()

                                //Telefon Ile Oturum acildigi zaman


                                var userID = mAuth.currentUser!!.uid.toString()
                                //Save the user information on database

                                var kaydedilecekKullaniciDtaylari = UsersDetails("0","0","0","","","")
                                var kaydedilecekKullanici = Users("",sifre, userName, adSoyad, telNo, sahteEmail, userID,kaydedilecekKullaniciDtaylari)
                                // mRef.child("users").child(userID).setValue(kaydedilecekKullanici)
                                mRef.child("users").child(userID).setValue(kaydedilecekKullanici)
                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                        override fun onComplete(p0: Task<Void>) {
                                            //Eger kullanici kayitli ise
                                            if (p0.isSuccessful) {
                                                progressBar.visibility=View.INVISIBLE
                                                Toast.makeText(activity, "User is Saved", Toast.LENGTH_SHORT).show()
                                            } else {
                                                progressBar.visibility=View.INVISIBLE
                                                mAuth.currentUser!!.delete()
                                                    .addOnCompleteListener(object : OnCompleteListener<Void>{
                                                        override fun onComplete(p0: Task<Void>) {
                                                            if (p0!!.isSuccessful)
                                                            {
                                                                Toast.makeText(activity, "User is not Saved try again", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }

                                                    })


                                            }
                                        }

                                    })


                            } else {
                                Toast.makeText(activity, "Oturum Acilamadi" + p0.exception, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })

            }

        }
        return view
    }

    var watcher : TextWatcher = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length > 5)
            {
                if(etAdSoyad.text.toString().length > 5 &&etKullaniciAdi.text.toString().length > 5 && etSifre.text.toString().length > 5)
                {
                    btnGirisYap.isEnabled = true
                    btnGirisYap.setTextColor(ContextCompat.getColor(activity!!,R.color.beyaz))
                    btnGirisYap.setBackgroundResource(R.drawable.register_button_active)
                }
                else{
                    btnGirisYap.isEnabled = false
                    btnGirisYap.setTextColor(ContextCompat.getColor(activity!!,R.color.sonokmavi))
                    btnGirisYap.setBackgroundResource(R.drawable.register_button)
                }
            }
            else{
                btnGirisYap.isEnabled=false
                btnGirisYap.setTextColor(ContextCompat.getColor(activity!!, R.color.sonokmavi))
                btnGirisYap.setBackgroundResource(R.drawable.register_button)
            }
        }

    }
    //EventBusSinfimiz buradan baslar
    @Subscribe(sticky = true)
    internal fun onKayitEvent(kayitBilgileri : EventBusDataEvents.KayitBilgileriGonder){

        if (kayitBilgileri.emailKayit == true)
        {
            emailKayitIslemi=true
            gelenEmail = kayitBilgileri.email !!
            Toast.makeText(activity, "Gelen Email " +gelenEmail+ "VerificationID" + verificationID,Toast.LENGTH_SHORT).show()

            Log.e("Sheroz","Gelen Email:  "+ gelenEmail)
        }
        else
        {
            emailKayitIslemi=false
            telNo = kayitBilgileri.telNo!!
            verificationID = kayitBilgileri.verificationID!!
            gelenKod = kayitBilgileri.code!!

            Toast.makeText(activity, "Gelen Kod: " +gelenKod+ "VerificationID" + verificationID,Toast.LENGTH_SHORT).show()
        }


    }
//Hocam kusura bakma ya :) kalbe basınca login acılıo onu mu diosn
    //Emaıl ile giriş yapıyorum ondan sonra isim sifre kullanıcı adı veriyorum her şey ok ondan sonr
    // eklediğim kullanıcı ile giriş yapınca olmuyor orda beni başka bir activitiye yönlendiriyor
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }
//açılır mı yatsıya kadar:)
    //Normalde çok hızlı açılıyor:)
    // Activity giris yap kısmındaki button varya hocam o basınca baska activitiye yönlendiriyor
    //göster bakam o butonun yaptogono
}
