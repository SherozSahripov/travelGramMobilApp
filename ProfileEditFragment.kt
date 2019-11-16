package com.example.travelgram.Profile


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.travelgram.Model.Users
import com.example.travelgram.R
import com.example.travelgram.utils.EventBusDataEvents
import com.example.travelgram.utils.UniversalImageLoader
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ProfileEditFragment : Fragment() {
    lateinit var circleProfileImageFragment: CircleImageView
    lateinit var gelenKullaniciBilgileri: Users
    lateinit var mDatabaseRef: DatabaseReference
    lateinit var mStrorage: StorageReference

    var profilePictureUrI: Uri? = null
    val resimSech = 150


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        mDatabaseRef = FirebaseDatabase.getInstance().reference
        mStrorage = FirebaseStorage.getInstance().reference

        setupKullaniciBilgileri(view)
        view.imgClose.setOnClickListener {
            activity?.onBackPressed()

        }

        //Profule fotografi degistirme
        view.tvFotografDegistir.setOnClickListener {
            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent, resimSech)
        }

        view.imgButtonDegKaydet.setOnClickListener {

            if (profilePictureUrI != null) {

                var dialogyukleniyor = YukleniyorFragment()
                dialogyukleniyor.show(activity!!.supportFragmentManager, "yukleniyorfragmenti")
                dialogyukleniyor.isCancelable = false

                //Profile photo save part
                var ref = mStrorage.child("users").child(gelenKullaniciBilgileri!!.user_id!!)
                    .child(profilePictureUrI!!.lastPathSegment)

                val uploadtask = ref.putFile(profilePictureUrI!!)
                var urkTask = uploadtask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val downLoadUri = task.result
                        mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("usersDetails")
                            .child("profile_picture")
                            .setValue(downLoadUri.toString())


                        dialogyukleniyor.dismiss()
                        kullanicidiAdiniGuncelle(view,true)
                    }

                }
            }
            else {
                kullanicidiAdiniGuncelle(view,false)
            }
         }

        return view
    }


    //Profile Resmi Degistirmeye yearayan methoddur
    //true is basarili bir sekilde storaga yuklenmistir
    //False image yklenirken hata olnmustur
    //null: kullanici resmini degistirmek  istememistir/
    private fun kullanicidiAdiniGuncelle(view: View, profileResimDegisti: Boolean?) {




        if (!gelenKullaniciBilgileri!!.user_name!!.equals(view.etUserName.text.toString())) {

            mDatabaseRef.child("users").orderByChild("user_name")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    //Kullanici adini kontrol edecegiz daha once kullanilmis mi yoksa kullanilmamis mi diye
                    override fun onDataChange(p0: DataSnapshot ) {
                        var userNameisused = false
                        for (ds in p0!!.children) {

                            var okunanKullaniciAdi = ds!!.getValue(Users::class.java)!!.user_name
                            if (okunanKullaniciAdi!!.equals(view.etUserName.text.toString())) {
                                userNameisused = true
                                profileBilgileriGuncelle(view,profileResimDegisti,false)
                                break
                            }
                        }
                        if (userNameisused == false) {
                            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!)
                                .child("user_name")
                                .setValue(view.etUserName.text.toString())
                                profileBilgileriGuncelle(view,profileResimDegisti,true)

                        }
                    }
                })


        }
        else {

            profileBilgileriGuncelle(view,profileResimDegisti,null)
        }
    }

    private fun profileBilgileriGuncelle(view: View, profileResimDegisti: Boolean?, userNameDegisti: Boolean?) {



        var profileGuncellebdiMi:Boolean?= null

        if (!gelenKullaniciBilgileri!!.ad_soyad!!.equals(view.etProfileName.text.toString())) {

            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("ad_soyad")
                .setValue(view.etProfileName.text.toString())
            profileGuncellebdiMi = true
        }

        if (!gelenKullaniciBilgileri!!.usersDetails!!.biography!!.equals(view.etBiography.text.toString())) {
            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("usersDetails")
                .child("biography").setValue(view.etBiography.text.toString())
            profileGuncellebdiMi = true
        }

        if (!gelenKullaniciBilgileri!!.usersDetails!!.web_site!!.equals(view.etWebSite.text.toString())) {
            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("usersDetails")
                .child("web_site").setValue(view.etWebSite.text.toString())
            profileGuncellebdiMi = true
        }

        if (profileResimDegisti == null && userNameDegisti == null && profileGuncellebdiMi == null )
        {
            Toast.makeText(activity,"Herhangi bir degisikli yapilmadi",Toast.LENGTH_SHORT).show()
        }
        else if (userNameDegisti == false  && (profileGuncellebdiMi == true || profileResimDegisti == true))
        {
            Toast.makeText(activity,"Kullanici bilgileri guncellendi aa kullanici adi kullanimda",Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(activity,"Kullanici Guncellendi",Toast.LENGTH_SHORT).show()
            activity!!.onBackPressed()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == resimSech && resultCode ==  AppCompatActivity.RESULT_OK && data!!.data!=null)
        {
            profilePictureUrI = data!!.data!!

            circleProfileImageDegis.setImageURI(profilePictureUrI)
        }
    }

    //b methodun icindeki view fragment proile edit te bulunana degisikleri yapmami sagliyor
    // bubula beraber baska fragmentteki nesneleri kullanilmis oluyorum
    private fun setupKullaniciBilgileri(view: View?) {

        view!!.etProfileName.setText(gelenKullaniciBilgileri!!.ad_soyad)
        view!!.etUserName.setText(gelenKullaniciBilgileri!!.user_name)

        if (!gelenKullaniciBilgileri!!.usersDetails!!.biography!!.isNullOrEmpty()){
            view!!.etBiography.setText(gelenKullaniciBilgileri!!.usersDetails!!.biography)

            }
            if (!gelenKullaniciBilgileri!!.usersDetails!!.web_site!!.isNullOrEmpty()){
                view!!.etWebSite.setText(gelenKullaniciBilgileri!!.usersDetails!!.web_site)
            }
        var imgUrl = gelenKullaniciBilgileri!!.usersDetails!!.profile_picture
        UniversalImageLoader.setImage(imgUrl!!,view!!.circleProfileImageDegis,view!!.progressBar,"")

    }

    @Subscribe(sticky = true)
    internal fun onKullaniciBilgileriEvent( kullaniciBilgileri: EventBusDataEvents.KullaniciBilgileriGonder){

  gelenKullaniciBilgileri = kullaniciBilgileri!!.kallanici!!


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
