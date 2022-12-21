package com.piscoya.appchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.piscoya.appchat.databinding.FragmentAddProfileBinding

class AddProfileFragment : Fragment() {
    private lateinit var binding: FragmentAddProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProfileBinding.inflate(inflater, container, false)

        binding.btnRegistrar.setOnClickListener {
            Toast.makeText(context, "PERFIL REGISTRADO", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}