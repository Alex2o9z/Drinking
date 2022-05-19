package com.example.drinking

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.drinking.databinding.ActivityLoginBinding
import com.example.drinking.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class MainActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityMainBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //Firebase RealtimeDatabase
    private var databaseReference: DatabaseReference?= null
    private var database: FirebaseDatabase?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure actionbar
        actionBar = supportActionBar!!
        actionBar.title = "Main"

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        //init firebaseDatabase
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Profile")

        checkUser()

        //handle logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }
    }

    private fun checkUser() {
        //check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //user not null, logged in
            val email = firebaseUser.email
            val userReference = databaseReference?.child(firebaseUser?.uid!!)
            userReference?.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firstname = snapshot.child("firstname").value.toString().trim()
                    val lastname = snapshot.child("lastname").value.toString().trim()
                    binding.nameText.text = firstname+" "+lastname
                }
                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@MainActivity, "DatabaseError", Toast.LENGTH_SHORT).show()
                    val firstname = "DatabaseError"
                    val lastname = "DatabaseError"
                    binding.nameText.text = firstname+" "+lastname
                }
            })

            //set to text view
            binding.emailText.text = email
        }
        else {
            //user is null, not logged in, goto login activity
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
}