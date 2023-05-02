package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentOpenStreetMapBinding
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.config.Configuration.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class OpenStreetMapFragment : Fragment() {
    private var _binding: FragmentOpenStreetMapBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOpenStreetMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // muutetaan activity Context-tyyppiseksi lennosta, jotta se kelpaa uudelle PreferenceManagerille
        // tietotyypin muuntamienn luokasta toiseen on nimeltään typecasting
        // jotta deprecated saadaan koodista pois, muuta import androidx.preference.PreferenceManager
        // ja lisää build.gradleen
        getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity as Context))

        // asetetaan mapViewiin teitty ulkonäkö
        binding.map.setTileSource(TileSourceFactory.MAPNIK)

        // Zoomataa Tornioon
        val mapController = binding.map.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(65.85006771594745, 24.144143249627728);
        mapController.setCenter(startPoint);

        // kokeillaan lisätä Marker Torniolle
        val firstMarker = Marker(binding.map)
        firstMarker.position = startPoint
        firstMarker.title = "Tornio"
        firstMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        binding.map.overlays.add(firstMarker)
        binding.map.invalidate()

        // kun markkeria klikataan:
        firstMarker.setOnMarkerClickListener { _, _ ->
            // sijainti: esim: marker.position.latitude
            Log.d("TESTI", "Marker click!")

            return@setOnMarkerClickListener false
        }

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}