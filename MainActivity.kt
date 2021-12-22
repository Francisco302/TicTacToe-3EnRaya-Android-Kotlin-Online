package com.fdsanchez.ticonline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {


    private var mAuth:FirebaseAuth?=null

    private var database= FirebaseDatabase.getInstance()
    private var myRef=database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnLocal = findViewById(R.id.btnLocal) as Button;
        btnLocal.setOnClickListener{
            val intent = Intent(this,GameActivity::class.java)
            intent.putExtra("mode", "local")
            startActivity(intent)
        }

        var btnOnline = findViewById(R.id.btnOnline) as Button;
        btnOnline.setOnClickListener{
            buLoginEvent(btnOnline)


        }

        mAuth= FirebaseAuth.getInstance()
    }

    fun buLoginEvent(view: View){

        LoginToFireBase(findViewById<EditText>(R.id.etEmail).text.toString(),findViewById<EditText>(R.id.etPassword).text.toString())
    }


    fun LoginToFireBase(email:String,password:String){

        mAuth!!.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){ task ->

                if (task.isSuccessful){
                    Toast.makeText(applicationContext,"Successful login",Toast.LENGTH_LONG).show()

                    var currentUser =mAuth!!.currentUser
                    //save in database
                    if(currentUser!=null) {
                        myRef.child("Users").child(SplitString(currentUser.email.toString())).child("Request").setValue(currentUser.uid)
                    }

                    LoadMain()

                }else
                {
                    Toast.makeText(applicationContext,"fail login",Toast.LENGTH_LONG).show()
                }

            }

    }

    override fun onStart() {
        super.onStart()
        LoadMain()
    }


    fun  LoadMain(){
        var currentUser =mAuth!!.currentUser

        if(currentUser!=null) {


            val intent = Intent(this,GameActivity::class.java)
            intent.putExtra("mode", "online")


            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)

            startActivity(intent)
            finish()
        }
    }

    fun  SplitString(str:String):String{
        var split=str.split("@")
        return split[0]
    }


}