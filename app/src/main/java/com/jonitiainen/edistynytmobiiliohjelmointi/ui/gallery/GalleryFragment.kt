package com.jonitiainen.edistynytmobiiliohjelmointi.ui.gallery

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jonitiainen.edistynytmobiiliohjelmointi.R
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // käynnistetään robotin animaatio XML:n pohjalta
        binding.imageViewRobot.setBackgroundResource(R.drawable.robo_animation)
        val frameAnimation = binding.imageViewRobot.background as AnimationDrawable
        frameAnimation.start()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}