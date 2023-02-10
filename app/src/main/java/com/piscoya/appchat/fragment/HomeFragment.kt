package com.piscoya.appchat.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.piscoya.appchat.R
import com.piscoya.appchat.adapter.UserAdapter
import com.piscoya.appchat.databinding.FragmentHomeBinding
import com.piscoya.appchat.model.User
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.collections.Map as Map

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var dialog: ProgressDialog
    private val mAuth = FirebaseAuth.getInstance()
    private val mDatabase = FirebaseDatabase.getInstance()
    private val mStorage = FirebaseStorage.getInstance()
    private val referenceDatabase = mDatabase.reference
    private val referenceStorage = mStorage.reference
    private val userList = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        referenceDatabase.child("Users").child(mAuth.uid!!).child("imgPerfil").get()
            .addOnSuccessListener {
                val img = it.value
                Glide.with(this).load(img)
                    .centerCrop()
                    .placeholder(R.drawable.ic_add_profile)
                    .into(binding.imgPerfil)
            }

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvListUsers.layoutManager = linearLayoutManager
        userAdapter = UserAdapter(requireContext(), this, userList)
        binding.rvListUsers.adapter = userAdapter

        referenceDatabase.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)
                    if (!user!!.id.equals(mAuth.uid))
                        userList.add(user)
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return binding.root
    }
}