package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentCustomViewTesterBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentDataBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.fragments.DataFragmentDirections

class CustomViewTesterFragment : Fragment() {
    private var _binding: FragmentCustomViewTesterBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomViewTesterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.speedView.speedTo(35f)

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}