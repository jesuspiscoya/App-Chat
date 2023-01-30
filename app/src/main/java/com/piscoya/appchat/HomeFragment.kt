package com.piscoya.appchat

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.piscoya.appchat.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var dialog: ProgressDialog
    private val mauth = FirebaseAuth.getInstance()
    private val mdatabase = FirebaseDatabase.getInstance()
    private val mstorage = FirebaseStorage.getInstance()
    private val referenceDatabase = mdatabase.reference
    private val referenceStorage = mstorage.reference
    private val userList = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        //Prueba RecyclerView
        val lista = ArrayList<User>()
        for (i in 1..18) {
            val useri = User(i.toString(), "Nombre$i", "+51999999999", "Sin foto$i")
            lista.add(useri)
        }

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvListUsers.layoutManager = linearLayoutManager
        userAdapter = UserAdapter(requireContext(), lista)
        binding.rvListUsers.adapter = userAdapter

        referenceDatabase.child("Users").child(mauth.uid!!).get().addOnSuccessListener {
            val dataUser = it.getValue(User::class.java)
            userList.add(dataUser!!)
            val user = userList[0]
            val link = user.imgPerfil

            Glide.with(this).load(link)
                .centerCrop()
                .placeholder(R.drawable.ic_add_profile)
                .into(binding.imgPerfil)
        }

        return binding.root
    }
}