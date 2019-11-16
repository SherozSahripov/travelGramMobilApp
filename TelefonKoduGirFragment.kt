package com.example.travelgram.Login


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast

import com.example.travelgram.R
import com.example.travelgram.utils.EventBusDataEvents
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_telefon_kodu_gir.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.android.synthetic.main.fragment_telefon_kodu_gir.*





class TelefonKoduGirFragment : Fragment() {


    var gelenTelNo = ""
    lateinit var mCallbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationID =""
    var gelenKod = ""
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view  = inflater.inflate(R.layout.fragment_telefon_kodu_gir, container, false)

        view.tvKullaniciTelNo.setText(gelenTelNo)
        progressBar = view.pbTelNoOnaylar
        setupCallBack()

        view.btnTelKodIleri.setOnClickListener {

            if (gelenKod.equals(view.etOnayKodu.text.toString()))
            {
                EventBus.getDefault().postSticky(EventBusDataEvents.KayitBilgileriGonder(gelenTelNo,null,verificationID,gelenKod,false))

                var transaction = activity!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.loginContainer,KayitFragment())
                transaction.addToBackStack("KayıtFragmentı Eklendı")
                transaction.commit()
            }
            else
            {
                Toast.makeText(activity,"Kod Hatali",Toast.LENGTH_SHORT).show()
            }

        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            gelenTelNo,        // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            this.activity!!,               // Activity (for callback binding)
            mCallbacks)        // OnVerificationStateChangedCallbacks



        return view

    }

    private fun setupCallBack() {

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                if (!credential.smsCode.isNullOrEmpty()) {

                    gelenKod = credential.smsCode!!
                    progressBar.visibility = View.INVISIBLE
                    Log.e("HATA","on  verification complete SMS gelmis " + gelenKod)
                }
                else
                {
                    Log.e("HATA", "on verification complete SMS gelmeyecktir")
                }

            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.e("Hata","Hata Cikti" + e.message)
                progressBar.visibility = View.INVISIBLE

            }

            override fun onCodeSent(
                verificationId: String?,
                token: PhoneAuthProvider.ForceResendingToken?
            )
            {

                verificationID = verificationId!!
                progressBar.visibility = View.VISIBLE
                Log.e("Hata","Hata Cikti")

            }
        }
    }


    @Subscribe(sticky = true)
    internal fun onTelefonNoEvent(kayitBilgileri : EventBusDataEvents.KayitBilgileriGonder){

        gelenTelNo = kayitBilgileri.telNo !!
        Log.e("Sheroz","Gelen Tel No:  "+ gelenTelNo)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }


}
