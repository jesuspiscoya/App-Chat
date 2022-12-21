package com.piscoya.appchat

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.piscoya.appchat.databinding.FragmentOtpBinding

class OtpFragment : Fragment() {
    private lateinit var binding: FragmentOtpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpBinding.inflate(inflater, container, false)

        //Toast.makeText(context,"PASTE",Toast.LENGTH_SHORT).show()

        binding.btnConfirmar.setOnClickListener {
            if (binding.pinOtp.getEnteredPin().equals("123456"))
                findNavController().navigate(R.id.otpFragment_to_addProfileFragment)
            else
                binding.pinOtp.reset()
        }

        return binding.root
    }
}