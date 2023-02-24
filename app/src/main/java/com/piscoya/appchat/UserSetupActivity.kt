package com.piscoya.appchat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.piscoya.appchat.databinding.ActivityUserSetupBinding
import com.piscoya.appchat.model.User
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.*

class UserSetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserSetupBinding
    private var fotoUrl: Uri? = null
    private var mAuth = FirebaseAuth.getInstance()
    private var mDatabase = FirebaseDatabase.getInstance()
    private var mStorage = FirebaseStorage.getInstance()
    private val referenceDatabase = mDatabase.reference
    private val referenceStorage = mStorage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgPerfil.setOnClickListener { buscarFoto() }
        binding.btnRegistrar.setOnClickListener {
            if (binding.txtNombrePerfil.text!!.isEmpty())
                binding.filledTextField.error = "Ingrese su nombre."
            else {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
                binding.progressCircular.visibility = View.VISIBLE
                binding.btnRegistrar.visibility = View.GONE
                binding.filledTextField.error = null
                if (fotoUrl != null) {
                    subirUsuario()
                }
            }
        }
    }

    private fun buscarFoto() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        startActivityForResult(intent, 300)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK)
            if (requestCode == 300) {
                fotoUrl = data!!.data
                binding.imgPerfil.setImageURI(fotoUrl)
                showToast("Se cargó la imágen correctamente.", FancyToast.SUCCESS)
            }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun subirUsuario() {
        val urlStorageFoto = "${Date().time}_user_${mAuth.uid}"
        val folder = referenceStorage.child("UsersPhotos")
        val file = folder.child(urlStorageFoto)
        file.putFile(fotoUrl!!).addOnSuccessListener {
            val uriTask = it.storage.downloadUrl
            while (!uriTask.isSuccessful);
            if (uriTask.isSuccessful) {
                uriTask.addOnSuccessListener {
                    val uid = mAuth.uid
                    val nombre = binding.txtNombrePerfil.text.toString()
                    val numero = mAuth.currentUser!!.phoneNumber
                    val imageUrl = it.toString()
                    val user = User(uid!!, nombre, numero!!, imageUrl)

                    referenceDatabase
                        .child("Users")
                        .child(uid)
                        .setValue(user)
                        .addOnSuccessListener {
                            binding.progressCircular.visibility = View.GONE
                            binding.btnRegistrar.visibility = View.VISIBLE
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                }
            } else {
                val uid = mAuth.uid
                val nombre = binding.txtNombrePerfil.text.toString()
                val numero = mAuth.currentUser!!.phoneNumber
                val user = User(uid!!, nombre, numero!!, "Sin Foto")

                referenceDatabase
                    .child("Users")
                    .child(uid!!)
                    .setValue(user)
                    .addOnSuccessListener {
                        binding.progressCircular.visibility = View.GONE
                        binding.btnRegistrar.visibility = View.VISIBLE
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            }
        }.addOnFailureListener {
            binding.progressCircular.visibility = View.GONE
            binding.btnRegistrar.visibility = View.VISIBLE
            showToast("Error al cargar foto.", FancyToast.ERROR)
        }
    }

    private fun showToast(msg: String, type: Int) {
        val toast = FancyToast.makeText(this, msg, FancyToast.LENGTH_SHORT, type, false);
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_VERTICAL, 0, 90);
        toast.show();
    }
}