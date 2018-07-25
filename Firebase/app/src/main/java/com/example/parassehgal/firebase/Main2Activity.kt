package com.example.parassehgal.firebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main2.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser





class Main2Activity : AppCompatActivity() {

    val PDF=0
    val DOCX=1
    val AUDIO=2
    val VIDEO=3
    lateinit var uri:Uri
    lateinit var mStorage:StorageReference

    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        mStorage=FirebaseStorage.getInstance().reference

        mAuth = FirebaseAuth.getInstance();

        pdfButton.setOnClickListener {
            val intent=Intent()
            intent.setType("pdf/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Select PDF"),PDF)
        }

        docxButton.setOnClickListener {
            val intent=Intent()
            intent.setType("docx/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Select DOCX"),DOCX)
        }
        musicButton.setOnClickListener {
            val intent=Intent()
            intent.setType("audio/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Select Audio"),AUDIO)
        }
        videoButton.setOnClickListener{
            val intent=Intent()
            intent.setType("video/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Select PDF"),VIDEO)
        }
        
        logoutButton.setOnClickListener {
            //mAuth.signOut()
            LoginManager.getInstance().logOut()
            updateUI()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.getCurrentUser()
        if(currentUser==null){
            updateUI()
        }

    }

    private fun updateUI() {
    startActivity(Intent(this,MainActivity::class.java))
    }

    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){
      if(resultCode== Activity.RESULT_OK){
          if(requestCode==PDF){
              uri=data!!.data
              upload()
          }
          else if(requestCode==DOCX){
              uri=data!!.data
              upload()
          }
          else if(requestCode==AUDIO){
              uri=data!!.data
              upload()
          }
          else if(requestCode==VIDEO){
              uri=data!!.data
              upload()
          }
      }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun upload(){
        var mReference=mStorage.child(uri.lastPathSegment)
        try {
            mReference.putFile(uri).addOnSuccessListener {
                taskSnapshot: UploadTask.TaskSnapshot? -> var url= taskSnapshot!!.downloadUrl
                Toast.makeText(this,"Succesfully Uploaded",Toast.LENGTH_LONG).show()
            }
        }
        catch (e: Exception){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
        }

    }
}
