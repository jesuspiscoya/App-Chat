package com.piscoya.appchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.piscoya.appchat.databinding.ActivityOtpBinding
import com.shashank.sony.fancytoastlib.FancyToast
import `in`.aabhasjindal.otptextview.OTPListener
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val codPais = intent.getStringExtra("codPais")
        val numero = intent.getStringExtra("userNumero")
        var verificationId = intent.getStringExtra("verificationId")

        cuentaRegresiva().start()

        binding.txtNumero.text = "$codPais $numero"
        binding.codeOtp.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                binding.txtErrorOtp.visibility = View.INVISIBLE
            }

            override fun onOTPComplete(otp: String) {
                val credencial = PhoneAuthProvider.getCredential(verificationId!!, otp)
                mAuth.signInWithCredential(credencial)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            binding.codeOtp.showSuccess()
                            FirebaseDatabase.getInstance().reference.child("Users")
                                .child(mAuth.uid!!).get().addOnSuccessListener {
                                    if (it.value == null) {
                                        val intent =
                                            Intent(this@OtpActivity, UserSetupActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        val intent =
                                            Intent(this@OtpActivity, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                        } else {
                            binding.codeOtp.showError()
                            binding.txtErrorOtp.visibility = View.VISIBLE
                        }
                    }
            }
        }
        binding.txtReenviarOtp.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            binding.progressCircular.visibility = View.VISIBLE
            binding.txtErrorOtp.visibility = View.GONE
            binding.lyReenviar.visibility = View.GONE
            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(codPais + numero)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {}

                    override fun onVerificationFailed(p0: FirebaseException) {
                        showToast("Ocurrió un error, intentalo de nuevo.", FancyToast.ERROR)
                    }

                    override fun onCodeSent(
                        newVerificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        showToast("Se envió el código.", FancyToast.SUCCESS)
                        verificationId = newVerificationId
                        binding.codeOtp.setOTP("")
                        binding.progressCircular.visibility = View.GONE
                        binding.lyReenviar.visibility = View.VISIBLE
                        binding.txtMensajeOtp.visibility = View.VISIBLE
                        binding.txtTiempoOtp.visibility = View.VISIBLE
                        binding.txtReenviarOtp.visibility = View.GONE
                        cuentaRegresiva().start()
                    }
                }).build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun showToast(msg: String, type: Int) {
        val toast = FancyToast.makeText(this, msg, FancyToast.LENGTH_SHORT, type, false);
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_VERTICAL, 0, 90);
        toast.show();
    }

    private fun cuentaRegresiva(): CountDownTimer {
        return object : CountDownTimer(30000, 1000) {
            override fun onTick(p0: Long) {
                binding.txtTiempoOtp.text = ((p0 / 1000) + 1).toString()
            }

            override fun onFinish() {
                binding.txtMensajeOtp.visibility = View.GONE
                binding.txtTiempoOtp.visibility = View.GONE
                binding.txtReenviarOtp.visibility = View.VISIBLE
            }
        }
    }
}