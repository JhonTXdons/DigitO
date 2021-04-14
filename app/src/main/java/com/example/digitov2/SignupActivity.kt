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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignupActivity : AppCompatActivity() {

    var database = FirebaseDatabase.getInstance().getReference().child("Users")
    var myref = database
    private lateinit var auth: FirebaseAuth

    lateinit var etFirstName: TextInputEditText
    lateinit var etLastName: TextInputEditText
    lateinit var etEmail: TextInputEditText
    lateinit var etPassword: TextInputEditText
    lateinit var etRepeatPassword: TextInputEditText

    lateinit var  etFirstNameLayout: TextInputLayout
    lateinit var  etLastNameLayout: TextInputLayout
    lateinit var etEmailLayout: TextInputLayout
    lateinit var etPasswordLayout: TextInputLayout
    lateinit var etPasswordRepeatLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_new)

        viewInitializations()

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        //Validation
        firstNameValidation()
        lastNameValidation()
        emailValidation()
        passwordValidation()

    }

    private fun isValid(string: String): Boolean{
        val pattern = Regex("[A-Z][a-zàèìòù]+")
        return pattern.matches(string)
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun comparePasswords(p1: String, p2: String){
        if (!p1.equals(p2)) {
            etPasswordLayout.error = "Password non compatibili"
            etPasswordRepeatLayout.error = "Password non compatibili"
        }else{
            etPasswordLayout.error = null
            etPasswordRepeatLayout.error = null
        }
    }

    fun viewInitializations() {
        etFirstName = findViewById(R.id.nome)
        etLastName = findViewById(R.id.cognome)
        etEmail = findViewById(R.id.email)
        etPassword = findViewById(R.id.password1)
        etRepeatPassword = findViewById(R.id.password2)

        etFirstNameLayout = findViewById(R.id.textInputLayout)
        etLastNameLayout = findViewById(R.id.textInputLayout2)
        etEmailLayout = findViewById(R.id.textInputLayout3)
        etPasswordLayout = findViewById(R.id.textInputLayout4)
        etPasswordRepeatLayout = findViewById(R.id.textInputLayout5)


        // To show back button in actionbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun firstNameValidation(): Boolean {

        etFirstName.doBeforeTextChanged { text, start, count, after ->
            etFirstNameLayout.error = null
        }

        etFirstName.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()) {
                etFirstNameLayout.error = "Inserisci un valore!"
            }else{
                if (!isValid(text.toString())) {
                    etFirstNameLayout.error = "Errore: inserimento non valido!"
                } else {
                    etFirstNameLayout.error = null
                }
            }
        }
        if (etFirstNameLayout.error != null) return false
        return true
    }

    fun lastNameValidation(): Boolean{

        etLastName.doBeforeTextChanged { text, start, count, after ->
            etLastNameLayout.error = null
        }

        etLastName.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()) {
                etLastNameLayout.error = "Inserisci un valore!"
            }else{
                if (!isValid(text.toString())) {
                    etLastNameLayout.error = "Errore: inserimento non valido!"
                } else {
                    etLastNameLayout.error = null
                }
            }

        }
        if (etLastNameLayout.error != null) return false
        return true
    }

    fun emailValidation(): Boolean{
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

    fun passwordValidation(): Boolean{
        etRepeatPassword.doOnTextChanged { text, start, before, count ->
            comparePasswords(etPassword.text.toString(), etRepeatPassword.text.toString())
        }

        if (etPasswordRepeatLayout.error != null) return false
        return true
    }

    fun validateInputs(): Boolean{
        if(etFirstName.text!!.isEmpty() || !firstNameValidation()){
            return false
        }
        if (etLastName.text!!.isEmpty() || !lastNameValidation()){
            return false
        }
        if (etEmail.text!!.isEmpty() || !emailValidation()){
            return false
        }
        if (etPassword.text!!.isEmpty() || etRepeatPassword.text!!.isEmpty() || !passwordValidation()){
            return false
        }
        return true
    }

    fun performSignUp(view: View) {

        if (validateInputs()) {

            // Input is valid, here send data to your server

            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val repeatPassword = etRepeatPassword.text.toString()

            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
            // Here you can call you API
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "createUserWithEmail:success")


                            val newuser = auth.currentUser?.uid.toString()
                            val user_data: HashMap<String, Any> = HashMap()
                            user_data["Nome"] = firstName
                            user_data["Cognome"] = lastName
                            user_data["email"] = email

                            myref.child(newuser).setValue(user_data)
                            val firstNotify = "0"


                            val user_placeholder_notify: HashMap<String, Any> = HashMap()
                            user_placeholder_notify["title"] = "La mia prima Notifica"
                            user_placeholder_notify["description"] = "Puoi autorizzare tutte le notifiche con il simbolo GIALLO, " +
                                    "le notifiche scadute avranno un simbolo ROSSO mentre quelle approvate un simbolo VERDE. " +
                                    "Tocca la notifica per inconminciare! Poi premi sull'impronta per autorizzare la notifica"
                            user_placeholder_notify["priority"] = "E"
                            user_placeholder_notify["token"] = "0x0"

                            val notify_ref: DatabaseReference = myref.child(newuser).child("Notify")
                            notify_ref.child(firstNotify).setValue(user_placeholder_notify)

                            val user = auth.currentUser
                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }

        }else{
            Toast.makeText(baseContext, "Registrazione fallita, alcuni campi sono vuoti!.",
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("extraAuth", user)
        }
        startActivity(intent)
    }
}