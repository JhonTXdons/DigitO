package com.example.digitov2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class NotifyDisplay : AppCompatActivity() {

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private var notifyListener: ValueEventListener? = null
    var database = FirebaseDatabase.getInstance()
    var myref = database.reference
    var uid = FirebaseAuth.getInstance().uid

    companion object {
        private const val TAG = "KotlinActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify_display)

        val title: TextView = findViewById(R.id.textNTitle)
        val description: TextView = findViewById(R.id.textNDescription)
        val date: TextView = findViewById(R.id.textNDate)
        val time: TextView = findViewById(R.id.textNTime)
        val img: ImageView = findViewById(R.id.imageViewNFinger)
        val id: TextView = findViewById(R.id.textNId)

        val bundle: Bundle? = intent.extras

        title.text = bundle!!.getString("extraTitle")
        description.text = bundle!!.getString("extraDescription");
        time.text = bundle!!.getString("extraTime");
        date.text = bundle!!.getString("extraDate");
        id.text = bundle!!.getString("extraId");
        val priority: String? = bundle!!.getString("extraPriority");
        val position: Int = bundle!!.getInt("extraPosition");
        val key: String? = bundle!!.getString("extraId");

        when(priority){
            "E" -> img.visibility = View.VISIBLE;
            else -> img.visibility = View.INVISIBLE;
        }

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            applicationContext,
                            "Notifica $position autorizzata",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (key != null) {
                        updatePriority(key)
                    }
                    val intent = Intent(this@NotifyDisplay,MainActivity::class.java)
                    startActivity(intent)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            applicationContext,
                            "Notifica $position non autorizzata",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val intent = Intent(this@NotifyDisplay,MainActivity::class.java)
                    startActivity(intent)
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autorizzazione ${title.toString()}")
            .setSubtitle("Usa l'impronta per autorizzare")
            .setNegativeButtonText("Indietro")
            .build()

        img.setOnClickListener{
            Toast.makeText(this, "Autorizzazione in corso...", Toast.LENGTH_SHORT).show()

            biometricPrompt.authenticate(promptInfo)
        }

    }


    private fun updatePriority(id: String) {
        val newPriority= "A"
        myref.child("Users/$uid/Notify/$id")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val currentPriority = dataSnapshot.getValue<Notify>()
                    if (currentPriority?.priority == "S") {
                        Toast.makeText(this@NotifyDisplay, "Notifica non autorizzata poichè scaduta!", Toast.LENGTH_SHORT).show()
                        return
                    }
                    dataSnapshot.ref.child("priority").setValue(newPriority)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("User", databaseError.message)
                }
            })
    }


}

