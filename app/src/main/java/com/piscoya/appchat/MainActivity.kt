package com.piscoya.appchat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.piscoya.appchat.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val mAuth = FirebaseAuth.getInstance()
    private val mDatabase = FirebaseDatabase.getInstance()
    private val referenceDatabase = mDatabase.reference
    private val conexion = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        //setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onResume() {
        conexion.put("conexion", "En línea")
        conexion.put("fecha", "null")
        conexion.put("hora", "null")
        referenceDatabase.child("Conexión").child(mAuth.uid!!).setValue(conexion)
        super.onResume()
    }

    @SuppressLint("NewApi")
    override fun onPause() {
        val fecha = LocalDate.now()
        val hora = LocalTime.now()
        conexion.put("conexion", "null")
        conexion.put("fecha", fecha.toString())
        conexion.put("hora", hora.toString())
        referenceDatabase.child("Conexión").child(mAuth.uid!!)
            .setValue(conexion)
        super.onPause()
    }
}