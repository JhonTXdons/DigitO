package com.example.digitov2


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), MyNotifyAdapter.onItemClickListener {

    private var myRecycler: RecyclerView? = null
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: MyNotifyAdapter
    private lateinit var database: DatabaseReference
    private var auth = FirebaseAuth.getInstance()
    private var uid = auth.currentUser?.uid



    companion object {
        private const val TAG = "KotlinActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        // [START initialize_database_ref]
        database = Firebase.database.reference
        // [END initialize_database_ref]

        // Recupero e inserisco a video il Nome e Cognome dell'utente attualmente loggato
        val userName: TextView = findViewById(R.id.currentUserName)
        val userSurname: TextView = findViewById(R.id.currentUserSurname)
        val checkBoxPriorityE: CheckBox = findViewById(R.id.checkBoxE)
        val checkBoxPriorityA: CheckBox = findViewById(R.id.checkBoxA)
        val checkBoxPriorityS: CheckBox = findViewById(R.id.checkBoxS)
        updateUserTextViewName(userName)
        updateUserTextViewSurname(userSurname)


        val query: Query = FirebaseDatabase.getInstance()
                .reference
                .child("Users/$uid/Notify")
                .orderByChild("token")
                .limitToFirst(5)

        val persons: FirebaseRecyclerOptions<Notify> = FirebaseRecyclerOptions.Builder<Notify>()
                .setQuery(query, Notify::class.java)
                .build()

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyNotifyAdapter(persons, this)

        myRecycler = findViewById<RecyclerView>(R.id.myRecycler).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

        //Notifies are filtered by priority to display
        checkBoxPriorityE.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                filterByPriority()
            } else {
                revertFilter()
            }
        }

        checkBoxPriorityA.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                filterByPriorityA()
            } else {
                revertFilter()
            }
        }

        checkBoxPriorityS.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                filterByPriorityS()
            } else {
                revertFilter()
            }
        }

    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "Notifica $position selezionata", Toast.LENGTH_SHORT).show()
        val n = viewAdapter.getItem(position)

        val intent = Intent(this, NotifyDisplay::class.java).apply {
            putExtra("extraId", n.id);
            putExtra("extraTitle", n.title);
            putExtra("extraDescription", n.description);
            putExtra("extraTime", n.time);
            putExtra("extraDate", n.date);
            putExtra("extraPriority", n.priority);
            putExtra("extraPosition", position);
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle item selection
        return when (item.itemId) {
            R.id.action_profile -> {
                Toast.makeText(this, "Vado al Profilo", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                auth.signOut()
                Toast.makeText(this, "Faccio il logout", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun updateUserTextViewName(TextViewUserName: TextView) {
        val dUserNome: DatabaseReference = database.child("Users").child("$uid").child("Nome")

        dUserNome.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nome = dataSnapshot.getValue(String::class.java).toString()
                TextViewUserName.text = nome
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(MainActivity.TAG, "onCancelled", databaseError.toException())
            }
        })


    }

    private fun updateUserTextViewSurname(TextViewUserSurname: TextView) {
        val dUserCognome: DatabaseReference = database.child("Users").child("$uid").child("Cognome")

        dUserCognome.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var cognome = dataSnapshot.getValue(String::class.java).toString()
                TextViewUserSurname.text = cognome

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(MainActivity.TAG, "onCancelled", databaseError.toException())
            }
        })

    }

    private fun filterByPriority(){
        val query: Query = FirebaseDatabase.getInstance()
                .reference
                .child("Users/$uid/Notify")
                .orderByChild("priority")
                .equalTo("E")
                .limitToFirst(10)

        val NewOptions: FirebaseRecyclerOptions<Notify> = FirebaseRecyclerOptions.Builder<Notify>()
                .setQuery(query, Notify::class.java)
                .build()
        viewAdapter.updateOptions(NewOptions)

    }

    private fun filterByPriorityA(){
        val query: Query = FirebaseDatabase.getInstance()
                .reference
                .child("Users/$uid/Notify")
                .orderByChild("priority")
                .equalTo("A")
                .limitToFirst(10)

        val NewOptions: FirebaseRecyclerOptions<Notify> = FirebaseRecyclerOptions.Builder<Notify>()
                .setQuery(query, Notify::class.java)
                .build()
        viewAdapter.updateOptions(NewOptions)

    }

    private fun filterByPriorityS(){
        val query: Query = FirebaseDatabase.getInstance()
                .reference
                .child("Users/$uid/Notify")
                .orderByChild("priority")
                .equalTo("S")
                .limitToFirst(10)

        val NewOptions: FirebaseRecyclerOptions<Notify> = FirebaseRecyclerOptions.Builder<Notify>()
                .setQuery(query, Notify::class.java)
                .build()
        viewAdapter.updateOptions(NewOptions)

    }

    private fun revertFilter(){
        val query: Query = FirebaseDatabase.getInstance()
                .reference
                .child("Users/$uid/Notify")
                .orderByChild("token")
                .limitToFirst(10)

        val NewOptions: FirebaseRecyclerOptions<Notify> = FirebaseRecyclerOptions.Builder<Notify>()
                .setQuery(query, Notify::class.java)
                .build()
        viewAdapter.updateOptions(NewOptions)

    }

    override fun onKeyDown(key_code: Int, key_event: KeyEvent?): Boolean {
        if (key_code == KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(key_code, key_event)
            return true
        }
        return false
    }


    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }



    protected override fun onStart() {
        super.onStart()
        viewAdapter.startListening()
    }

    protected override fun onStop() {
        super.onStop()
        viewAdapter.stopListening()
    }
}