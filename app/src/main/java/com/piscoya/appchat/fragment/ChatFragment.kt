package com.piscoya.appchat.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.piscoya.appchat.R
import com.piscoya.appchat.adapter.ChatAdapter
import com.piscoya.appchat.databinding.FragmentChatBinding
import com.piscoya.appchat.model.Mensaje
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private val args: ChatFragmentArgs by navArgs()
    private val mAuth = FirebaseAuth.getInstance()
    private val mDatabase = FirebaseDatabase.getInstance()
    private val mStorage = FirebaseStorage.getInstance()
    private val referenceDatabase = mDatabase.reference
    private val referenceStorage = mStorage.reference
    private val mensajeList = ArrayList<Mensaje>()

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        senderRoom = mAuth.uid + args.id
        receiverRoom = args.id + mAuth.uid
        binding.btnAtras.setOnClickListener {
            findNavController().navigate(R.id.chatFragment_to_homeFragment)
        }

        Glide.with(this).load(args.imgPerfil).centerCrop().placeholder(R.drawable.ic_add_profile)
            .into(binding.imgPerfil)

        binding.txtNombrePerfil.text = args.nombre

        referenceDatabase.child("Conexión").child(args.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val conexion = snapshot.getValue() as HashMap<String, String>

                        if (conexion.get("conexion").equals("null")) {
                            val fecha = LocalDate.parse(conexion.get("fecha"))
                            val hora = LocalTime.parse(conexion.get("hora"))
                                .format(DateTimeFormatter.ofPattern("hh:mm a"))

                            if (fecha.equals(LocalDate.now()))
                                binding.txtConexion.text = "ult. vez hoy a las $hora"
                            else if (ChronoUnit.DAYS.between(fecha, LocalDate.now()) == 1L)
                                binding.txtConexion.text = "ult. vez ayer a las $hora"
                            else {
                                val fechaFormato =
                                    fecha.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                                binding.txtConexion.text = "ult. vez $fechaFormato"
                            }
                        } else
                            binding.txtConexion.text = conexion.get("conexion")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvChat.layoutManager = linearLayoutManager
        chatAdapter = ChatAdapter(requireContext(), mensajeList, senderRoom, receiverRoom)
        binding.rvChat.adapter = chatAdapter

        referenceDatabase.child("Chats").child(senderRoom).child("Mensajes")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mensajeList.clear()
                    for (data in snapshot.children) {
                        val mensaje = data.getValue(Mensaje::class.java)
                        mensaje!!.id = data.key
                        mensajeList.add(mensaje)
                    }
                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        binding.btnEnviar.setOnClickListener {
            val mensajeSend = binding.txtMensaje.text.toString()
            val tiempo = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))
            val mensaje = Mensaje(mensajeSend, mAuth.uid, tiempo)
            binding.txtMensaje.setText("")
            val randomKey = referenceDatabase.push().key
            val lastMsgMap = HashMap<String, Any>()
            lastMsgMap.put("ultimoMensaje", mensaje.mensaje!!)
            lastMsgMap.put("ultimoTiempo", mensaje.tiempo!!)

            referenceDatabase.child("Chats").child(senderRoom).updateChildren(lastMsgMap)
            referenceDatabase.child("Chats").child(receiverRoom).updateChildren(lastMsgMap)
            referenceDatabase.child("Chats").child(senderRoom).child("Mensajes").child(randomKey!!)
                .setValue(mensaje).addOnSuccessListener {
                    referenceDatabase.child("Chats").child(receiverRoom).child("Mensajes")
                        .child(randomKey)
                        .setValue(mensaje)
                }
        }

        return binding.root
    }
}