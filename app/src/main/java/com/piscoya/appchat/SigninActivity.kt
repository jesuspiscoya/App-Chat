package com.piscoya.appchat

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.piscoya.appchat.databinding.ActivitySigninBinding
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.concurrent.TimeUnit

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
            if (binding.txtUserNumero.text!!.isEmpty() || binding.txtUserNumero.text!!.length < 9)
                binding.filledTextField.error = "Número invalido."
            else {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)

                binding.filledTextField.error = null
                binding.progressCircular.visibility = View.VISIBLE
                binding.btnContinuar.visibility = View.GONE

                val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(binding.listPais.selectedCountryCode() + binding.txtUserNumero.text.toString())
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                            binding.progressCircular.visibility = View.GONE
                            binding.btnContinuar.visibility = View.VISIBLE
                        }

                        override fun onVerificationFailed(p0: FirebaseException) {
                            binding.progressCircular.visibility = View.GONE
                            binding.btnContinuar.visibility = View.VISIBLE
                            showToast("Ocurrió un error, intentalo de nuevo.", FancyToast.ERROR)
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            binding.progressCircular.visibility = View.GONE
                            binding.btnContinuar.visibility = View.VISIBLE
                            val intent = Intent(this@SigninActivity, OtpActivity::class.java)
                            intent.putExtra("codPais", binding.listPais.selectedCountryCode())
                            intent.putExtra("userNumero", binding.txtUserNumero.text.toString())
                            intent.putExtra("verificationId", verificationId)
                            startActivity(intent)
                        }
                    }).build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
    }

    private fun showToast(msg: String, type: Int) {
        val toast = FancyToast.makeText(this, msg, FancyToast.LENGTH_SHORT, type, false);
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_VERTICAL, 0, 90);
        toast.show();
    }
}