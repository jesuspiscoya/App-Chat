package com.piscoya.appchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.piscoya.appchat.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        Toast.makeText(
            context,
            "CODE: " + binding.btnCodeCountry.selectedCountryCode() + binding.txtUserNumber.text,
            Toast.LENGTH_SHORT
        ).show()

        binding.btnContinuar.setOnClickListener {
            findNavController().navigate(R.id.registerFragment_to_otpFragment)
        }

        return binding.root
    }
}