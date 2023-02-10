package com.piscoya.appchat

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.piscoya.appchat.databinding.ActivityOtpBinding
import `in`.aabhasjindal.otptextview.OTPListener
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private lateinit var dialog: ProgressDialog
    private var mAuth = FirebaseAuth.getInstance()
    private var verificacionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this)
        dialog.setMessage("Enviando código OTP...")
        dialog.setCancelable(false)
        dialog.show()
        binding.txtNumero.text =
            intent.getStringExtra("codPais") + " " + intent.getStringExtra("userNumero")

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(intent.getStringExtra("codPais") + intent.getStringExtra("userNumero"))
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    TODO("Not yet implemented")
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(this@OtpActivity, "FALLOOO", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verifyId: String,
                    resendToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    dialog.dismiss()
                    verificacionId = verifyId
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

                    super.onCodeSent(verifyId, resendToken)
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        binding.codeOtp.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                val credencial = PhoneAuthProvider.getCredential(verificacionId!!, otp)
                mAuth.signInWithCredential(credencial)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            binding.codeOtp.showSuccess()
                            val intent = Intent(this@OtpActivity, UserSetupActivity::class.java)
                            startActivity(intent)
                        } else {
                            binding.codeOtp.showError()
                        }
                    }
            }
        }
        binding.btnConfirmar.setOnClickListener {
            if (binding.codeOtp.otp!!.isEmpty() || binding.codeOtp.otp!!.length < 7 || !binding.codeOtp.otp.equals(verificacionId))
                binding.codeOtp.showError()
        }
    }
}