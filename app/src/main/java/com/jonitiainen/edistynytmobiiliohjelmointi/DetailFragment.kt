package com.jonitiainen.edistynytmobiiliohjelmointi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentDataBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    // get fragment parameters from previous fragment
    val args: DetailFragmentArgs by navArgs()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // print out the given parameter into logs
        Log.d("ADVTECH", "" + args.id.toString())
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}