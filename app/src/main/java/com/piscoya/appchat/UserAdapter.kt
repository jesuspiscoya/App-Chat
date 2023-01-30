package com.piscoya.appchat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.piscoya.appchat.databinding.ListItemUserBinding

public class UserAdapter(var context: Context, var userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolderUser>() {

    public class ViewHolderUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListItemUserBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderUser {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_user, parent, false)
        return ViewHolderUser(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolderUser, position: Int) {
        val user = userList[position]
        holder.binding.txtNombrePerfil.text = user.nombre
        holder.binding.txtMensaje.text = position.toString()
        Glide.with(context).load(user.imgPerfil)
            .centerCrop()
            .placeholder(R.drawable.ic_add_profile)
            .into(holder.binding.imgPerfil)
    }
}