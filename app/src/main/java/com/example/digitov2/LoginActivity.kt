package com.example.digitov2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser



class LoginActivity: AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    lateinit var etEmail: TextInputEditText
    lateinit var etPassword: TextInputEditText

    lateinit var etEmailLayout: TextInputLayout
    lateinit var etPasswordLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_new)

        //Initializzation
        viewInitializations()

        //Validation
        emailValidation()
        passValidation()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    fun viewInitializations() {
        etEmail = findViewById(R.id.login_email)
        etPassword = findViewById(R.id.login_pass)

        etEmailLayout = findViewById(R.id.textInputLayoutEmail)
        etPasswordLayout = findViewById(R.id.textInputLayoutPassword)

        // To show back button in actionbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Checking if the input in form is valid
    fun validateInput(): Boolean {
        if (!emailValidation() || etEmail.text!!.isEmpty()){
            return false
        }
        // checking minimum password Length
        if (!passValidation() || etPassword.text!!.isEmpty()) {
            return false
        }
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun emailValidation(): Boolean{
        etEmail.doBeforeTextChanged { text, start, count, after ->
            etEmailLayout.error = null
        }
        etEmail.doOnTextChanged { text, start, before, count ->
            if (!isEmailValid(text.toString())) {
                etEmailLayout.error = "Carattere non valido!"
            }else{
                etEmailLayout.error = null
            }
        }
        if (etEmailLayout.error != null) return false
        return true
    }

    fun passValidation(): Boolean{
        etPassword.doBeforeTextChanged { text, start, count, after ->
            etPasswordLayout.error = null
        }
        etPassword.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 20) {
                etPasswordLayout.error = "Lunghezza password non valida!"
            }else{
                etPasswordLayout.error = null
            }
        }
        if ( etPasswordLayout.error != null) return false
        return true
    }


    // Hook Click Event
    fun performLogin(v: View) {
        if (validateInput()) {

            // Input is valid, here send data to your server
            val email = etEmail!!.text.toString()
            val password = etPassword!!.text.toString()
            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()

            // [START sign_in_with_email]
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
            // [END sign_in_with_email]

        }else{
            etEmailLayout.error = "Vuoto o non corretto!"
            etPasswordLayout.error ="Vuoto o non corretto!"
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("extraAuth", user)
        }
        startActivity(intent)
    }

    private fun reload() {

    }

    fun goToSignup(v: View) {
        // Open your SignUp Activity if the user wants to signup
        // Visit this article to get SignupActivity code https://handyopinion.com/signup-activity-in-android-studio-kotlin-java/
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}
