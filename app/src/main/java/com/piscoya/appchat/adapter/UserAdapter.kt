package com.piscoya.appchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.piscoya.appchat.R
import com.piscoya.appchat.databinding.ItemUserBinding
import com.piscoya.appchat.fragment.HomeFragmentDirections
import com.piscoya.appchat.model.Mensaje
import com.piscoya.appchat.model.User

public class UserAdapter(
    var context: Context,
    var fragment: Fragment,
    var userList: ArrayList<User>
) :
    RecyclerView.Adapter<UserAdapter.ViewHolderUser>() {
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private val mAuth = FirebaseAuth.getInstance()
    private val mDatabase = FirebaseDatabase.getInstance()
    private val referenceDatabase = mDatabase.reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderUser {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return ViewHolderUser(view)
    }

    override fun onBindViewHolder(holder: ViewHolderUser, position: Int) {
        val user = userList[position]
        senderRoom = mAuth.uid + user.id
        receiverRoom = user.id + mAuth.uid
        val send = senderRoom
        val receive = receiverRoom
        Glide.with(context).load(user.imgPerfil)
            .centerCrop()
            .placeholder(R.drawable.ic_add_profile)
            .into(holder.binding.imgPerfil)
        holder.binding.txtNombrePerfil.text = user.nombre

        referenceDatabase.child("Chats").child(senderRoom).child("ultimoMensaje").get()
            .addOnSuccessListener {
                holder.binding.txtMensaje.text = it.getValue().toString()
            }
        referenceDatabase.child("Chats").child(senderRoom).child("ultimoTiempo").get()
            .addOnSuccessListener {
                holder.binding.txtFecha.text = it.getValue().toString()
            }

        holder.binding.lnChat.setOnClickListener {
            val action = HomeFragmentDirections.homeFragmentToChatFragment(
                user.id!!,
                user.nombre!!,
                user.imgPerfil!!
            )
            fragment.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int = userList.size

    class ViewHolderUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemUserBinding.bind(itemView)
    }
}