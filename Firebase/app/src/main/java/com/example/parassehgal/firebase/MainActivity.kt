package com.example.parassehgal.firebase

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
//import android.support.test.orchestrator.junit.BundleJUnitUtils.getResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import android.support.design.widget.Snackbar
import android.support.annotation.NonNull
import android.view.View
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit private var mAuth: FirebaseAuth
    lateinit var firebaseDatabase:FirebaseDatabase
    lateinit var databaseReference:DatabaseReference

    lateinit var userid:String
    var firebaseuser:FirebaseUser?=null
    lateinit var email:String
    lateinit var password:String

    val RC_SIGN_IN:Int=1
    lateinit var mGoogleSignInClient:GoogleSignInClient

    private lateinit var mCallbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()



        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth= FirebaseAuth.getInstance()
        firebaseDatabase= FirebaseDatabase.getInstance()
        databaseReference=firebaseDatabase.getReference("userdetails")

        firebaseuser=mAuth.currentUser
        userid= firebaseuser!!.uid



        registerButton.setOnClickListener{

            email=emailEditText.text.toString()
            password=passwordEditText.text.toString()

            var name=nameEditText.text.toString()

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            val user = mAuth.getCurrentUser()
                            var id:String= user!!.uid

                            databaseReference.child(id).child("name").setValue(name)
                            databaseReference.child(id).child("email").setValue(email)
                            databaseReference.child(id).child("password").setValue(password)
                            // Sign in success, update UI with the signed-in user's information
                           // Log.d(FragmentActivity.TAG, "createUserWithEmail:success")
                            Toast.makeText(this,"Registered succesfully",Toast.LENGTH_LONG).show()

                            //updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("main activity", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(this@MainActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                           // updateUI(null)
                        }

                        // ...
                    }

        }

        loginButton.setOnClickListener {

            email=emailEditText.text.toString()
            password=passwordEditText.text.toString()

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            // Read from the database
                            databaseReference.child(userid).child("name").addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.

                                    var value= dataSnapshot.getValue(String::class.java)
                                    Log.d("main activity", "Value is: " + value!!)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Failed to read value
                                    Log.w("main activity", "Failed to read value.", error.toException())
                                }
                            })

                            Toast.makeText(this,"Login succesfull",Toast.LENGTH_LONG).show()
                            startActivity(Intent(this,Main2Activity::class.java))
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(FragmentActivity.TAG, "signInWithEmail:success")
                           // val user = mAuth.currentUser
                          //  updateUI(user)

                            val editor=getSharedPreferences("userdata",Context.MODE_PRIVATE).edit()
                            editor.putBoolean("accountCreated",true)
                            editor.apply()



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("main activity", "signInWithEmail:failure", task.exception)
                            Toast.makeText(this@MainActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                           // updateUI(null)
                        }

                        // ...
                    }
        }

        signinButton.setOnClickListener {
            signIn()
        }

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        login_button.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("email", "public_profile"))
            LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {

                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("MainActivity", "Facebook token: " + loginResult.accessToken.token)

                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d("MainActivity", "Facebook onCancel.")

                }

                override fun onError(error: FacebookException) {
                    Log.d("MainActivity", "Facebook onError.")

                }
            });
        }



    }

    private fun handleFacebookAccessToken( token: AccessToken) {
    //Log.d(TAG, "handleFacebookAccessToken:" + token);

     var credential:AuthCredential = FacebookAuthProvider.getCredential(token.getToken());
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, object: OnCompleteListener<AuthResult> {
                @Override
                public override fun onComplete(@NonNull task:Task<AuthResult>) {
                    if (task.isSuccessful()) {
                        startActivity(Intent(this@MainActivity, Main2Activity::class.java))
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(TAG, "signInWithCredential:success");
                       // FirebaseUser user = mAuth.getCurrentUser();
                        //updateUI(firebaseuser);
                    } else {
                        // If sign in fails, display a message to the user.
                        //Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this@MainActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }

                    // ...
                }
            });
}
    


    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
                updateUI(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                //Log.w(FragmentActivity.TAG, "Google sign in failed", e)
                // ...
            }

        }
        else{
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        //Log.d(FragmentActivity.TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(FragmentActivity.TAG, "signInWithCredential:success")
                        val user = mAuth.currentUser
                        Toast.makeText(this,"Login succesfull",Toast.LENGTH_LONG).show()
                        startActivity(Intent(this,Main2Activity::class.java))

                        val editor=getSharedPreferences("userdata",Context.MODE_PRIVATE).edit()
                        editor.putBoolean("accountCreated",true)
                        editor.apply()
                    } else {
                        // If sign in fails, display a message to the user.
                       // Log.w(FragmentActivity.TAG, "signInWithCredential:failure", task.exception)
                        //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        //updateUI(null)
                    }

                    // ...
                }
    }

    private fun updateUI(account: GoogleSignInAccount){
        displayText.text=account.displayName
        signoutButton.visibility=View.VISIBLE
        signoutButton.setOnClickListener { 
            view: View? -> mGoogleSignInClient.signOut().addOnCompleteListener {
            task: Task<Void> -> displayText.text=" "
            signoutButton.visibility=View.INVISIBLE
        }
        }
    }


}
