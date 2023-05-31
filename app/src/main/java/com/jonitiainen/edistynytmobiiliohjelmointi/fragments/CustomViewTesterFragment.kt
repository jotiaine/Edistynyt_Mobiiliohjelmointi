package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentCustomViewTesterBinding


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

        binding.customTemperatureViewTest.changeTemperature(14)

        // lisätty nappi, jolla voidaan asettaa satunnainen lämpötila
        binding.buttonSetRandomTemperature.setOnClickListener {
            val randomTemperature: Int = (-40..40).random()
            binding.customTemperatureViewTest.changeTemperature(randomTemperature)
            Log.d("ADVTECH", randomTemperature.toString())
        }

        // testi-Button, jolla voidaan lisätä satunnainen viesti
        binding.buttonAddTestData.setOnClickListener {
//            val randomValue: Int = (-40..40).random()


        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}