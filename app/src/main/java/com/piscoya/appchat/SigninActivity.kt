package com.piscoya.appchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.piscoya.appchat.databinding.ActivitySigninBinding

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (mAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnContinuar.setOnClickListener {
            val intent = Intent(this, OtpActivity::class.java)
            intent.putExtra("codPais", binding.listPais.selectedCountryCode())
            intent.putExtra("userNumero", binding.txtUserNumero.text.toString())
            startActivity(intent)
        }
    }
}