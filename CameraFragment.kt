package com.example.travelgram.Home
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.travelgram.R
import com.example.travelgram.utils.EventBusDataEvents
import com.otaliastudios.cameraview.*
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_camera.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.FileOutputStream


class CameraFragment: Fragment() {


    var myCamera :CameraView? = null
    var kameraIzniVerildiMi=false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view=inflater?.inflate(R.layout.fragment_camera,container,false)

        myCamera = view!!.camera_view
        myCamera!!.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        myCamera!!.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)


        myCamera!!.addCameraListener(object : CameraListener(){

            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)
                //Byte to File Stream olusturalim
//Cekilen her bir resmin bir oncekinf=den farkli bir isim alinmasini saglamak istriyoruz/
                var cekilenPhotoName = System.currentTimeMillis()
                var cekilanPhoto = File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/TestKlasor/" + cekilenPhotoName+".jpg")

                var dosyaOlustur = FileOutputStream(cekilanPhoto)
                dosyaOlustur.write(jpeg)
                dosyaOlustur.close()



                Log.e("Hata 2","Cekilen remin Buraya kaydedilecek " + cekilanPhoto.absolutePath.toString())




            }



        })



        view.imgKamraDegistirmeIconu.setOnClickListener {

            if (myCamera!!.facing==Facing.BACK){
                myCamera!!.facing=Facing.FRONT
            }
            else{
                myCamera!!.facing=Facing.BACK
            }


        }
        view.imgPhotoCek.setOnClickListener {

            if (myCamera!!.facing ==Facing.BACK)
            {
                myCamera!!.capturePicture()
            }
            else
            {
                myCamera!!.captureSnapshot()
            }


        }



        return view

    }

    override fun onResume() {
        super.onResume()
        if (kameraIzniVerildiMi == true)
            myCamera!!.start()
    }
    override fun onPause() {
        super.onPause()
        // Log.e("Hata2","Camera Fragmenti onPause'de de")
        myCamera!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Log.e("Hata2","Camera Fragmenti OnDestroy'de de")
        // cameraView!!1.destroy()
        if (myCamera!=null)
        {
            myCamera!!.destroy()
        }
    }
    @Subscribe(sticky = true)
    internal fun onCameaIzinEvent(permissionState : EventBusDataEvents.KameraIzinBilgisiGonder){

        kameraIzniVerildiMi=permissionState.kameraIzniVerildiMi!!
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