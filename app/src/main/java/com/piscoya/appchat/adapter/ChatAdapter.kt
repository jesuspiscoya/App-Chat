package com.piscoya.appchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.piscoya.appchat.R
import com.piscoya.appchat.databinding.ItemReceiveBinding
import com.piscoya.appchat.databinding.ItemSendBinding
import com.piscoya.appchat.model.Mensaje

class ChatAdapter(
    var context: Context,
    mensajesList: ArrayList<Mensaje>?,
    senderRoom: String,
    receiverRoom: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    lateinit var mensajesList: ArrayList<Mensaje>
    val ITEM_SEND = 1
    val ITEM_RECEIVE = 2
    val senderRoom: String
    val receiverRoom: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_SEND) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false)
            return ViewHolderSend(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false)
            return ViewHolderReceive(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensajes = mensajesList[position]
        if (holder.javaClass == ViewHolderSend::class.java) {
            val viewHolder = holder as ViewHolderSend

            //un if para las imagenes en chat

            viewHolder.binding.txtMensaje.text = mensajes.mensaje
            viewHolder.binding.txtTiempo.text = mensajes.tiempo

            // borrar mensajes

        } else {
            val viewHolder = holder as ViewHolderReceive
            //un if para las imagenes en chat

            viewHolder.binding.txtMensaje.text = mensajes.mensaje
            viewHolder.binding.txtTiempo.text = mensajes.tiempo

            // borrar mensajes

        }
    }

    override fun getItemCount(): Int = mensajesList.size

    override fun getItemViewType(position: Int): Int {
        val mensajes = mensajesList[position]
        if (FirebaseAuth.getInstance().uid == mensajes.senderId)
            return ITEM_SEND
        else
            return ITEM_RECEIVE
    }

    class ViewHolderSend(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSendBinding.bind(itemView)
    }

    class ViewHolderReceive(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemReceiveBinding.bind(itemView)
    }

    init {
        if (mensajesList != null)
            this.mensajesList = mensajesList
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }
}