package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.jonitiainen.edistynytmobiiliohjelmointi.R
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentMapsBinding

// nyt MapsFragment toteuttaa olio-ohjlemoinnin interfacen
// nimeltä GoogleMap.OnMarkerClickListener =>
// MapsFragmentista pitää nyt löytyä interfacen vaatima metodi, eli OnMarkerClick()
class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    private var _binding: FragmentMapsBinding? = null
    // get fragment parameters from previous fragment
    private val binding get() = _binding!!

    // tehdään luokan tlätasolle uusi jäsenmuuttuja => gMap
    // tarkoitus on tallentaa googleMap-olio talteen, jotta sitä voi
    // käyttää muualtakin kuin callvackin sisältä
    private lateinit var gMap : GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->
        // tämä käynnistyy silloin kun kartta on valmis ja voidaan käyttää eri ominaisuuksia

        // laitetaan googleMap-olio talteen, jotta myös onCreateView
        // ja ym. metodit pääsevät myös siihen käsiksi
        gMap = googleMap

        // jos marker tallenetaan muuttujaan, siihen voidaan asettaa lisädataa tagin kautta
        // esim. kaupungin nimi jos rajapinta ei jostain syystä tukisi koordinaatteja
        val tornio = LatLng( 65.85006771594745, 24.144143249627728 )
        var m1 = googleMap.addMarker(MarkerOptions().position(tornio).title("Marker in Tornio"))
        m1?.tag = "Tornio"

        // siirretään Mapsin kamera tähän koordinaattiin, zoomataan tähän koordinaattiin
        // zoom taso pitää olla float
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tornio, 14f))

        // asetetaan, että tämä luokka (this => MapsFragment) huolehtii siitä
        // jos jotain Markeria klikataan
        googleMap.setOnMarkerClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // käytetään binding layeria myös karttafragmentissä
        // asetetaan zoom-kontrollit joko päälle tai pois CheckBoxin arvon perusteella
        binding.checkBoxZoomControls.setOnCheckedChangeListener{compoundButton, b ->
            Log.d("TESTI", "CHECKED! " + b.toString())
            gMap.uiSettings.isZoomControlsEnabled = b
        }

        // kun valitaan radiobuttonilla normaali-kartta
        binding.radioButtonMapNormal.setOnCheckedChangeListener { compoundButton, b ->
            // onchecked käynnistyy molempiin suuntiin, eli jos radioButton valitaan
            // TAI valinta poistuu => tulee paljone bugeja
            // keskenään kumpaa koodia pitäisi totella
            if(compoundButton.isChecked) {
                gMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }

        // kun valitaan radiobuttonilla hybrid-kartta
        binding.radioButtonMapHybrid.setOnCheckedChangeListener { compoundButton, b ->
            // onchecked käynnistyy molempiin suuntiin, eli jos radioButton valitaan
            // TAI valinta poistuu => tulee paljone bugeja
            // keskenään kumpaa koodia pitäisi totella
            if(compoundButton.isChecked) {
                gMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    // tämä metodi vastaa siitä, kun jotakin markeria klikataan
    override fun onMarkerClick(p0: Marker): Boolean {
        Log.d("TESTI", "Marker CLICK!")
        Log.d("TESTI", p0.position.latitude.toString())
        Log.d("TESTI", p0.position.longitude.toString())

        // jos markerilla on tagi, tällä tavalla sen saa haettua siitä
        Log.d("TESTI", p0.tag.toString())

        // tallennetaan koordinaatit apumuuttujiin selkeyden vuoksi
        val lat = p0.position.latitude.toFloat()
        val lon = p0.position.longitude.toFloat()

        // actionin avulla siirrytään CityWEatherFragmentiin ja lähetetään
        // tarvittavat koordinaatit parametreina
        val action = MapsFragmentDirections.actionMapsFragmentToCityWeatherFragment(lat, lon)
        findNavController().navigate(action)

        return false
    }
}