package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.jonitiainen.edistynytmobiiliohjelmointi.BuildConfig
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentCityWeatherBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.datatypes.cityweather.CityWeather

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CityWeatherFragment : Fragment() {
    private var _binding: FragmentCityWeatherBinding? = null
    // get fragment parameters from previous fragment
    val args: CityWeatherFragmentArgs by navArgs()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCityWeatherBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Log.d("TESTI", "CityWeatherFragment START")
        Log.d("TESTI", args.lat.toString())
        Log.d("TESTI", args.lon.toString())

        // kutsutaan apufunktiota
        getWeatherData()

        return root
    }

    // apufunktio, johon voidaan eriyttää säädatan hakeminen ja käsittely
    private fun getWeatherData(){
        // this is the url where we want to get our data from

        // openweathermap osoite on tätä muotoa:
        // https://api.openweathermap.org/data/2.5/weather?lat=65.85006771594745&lon=24.144143249627728&units=metric&appid={API%20key}
        val lat = args.lat
        val lon = args.lon
        // haetaan local.properties -tiedostosta OpenWeatherMap API key
        // huom: local.properties muuttujat päivittyvät vasta kun projektia käytetään
        // emulaattorissa päällä!
        val API_KEY = BuildConfig.OPENWEATHER_API_KEY

        val JSON_URL = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&units=metric&appid=${API_KEY}"

        val gson = GsonBuilder().setPrettyPrinting().create()

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, JSON_URL,
            Response.Listener { response ->

                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                Log.d("TESTI", response)

                // datassa tulee vain yhden kaupungin JSON, muutetaan GSON:illa käyttökelpoiseen muotoon
                // tämä vaatii CityWeather-luokan, joka tehdään esimerkkidatan pohjalta
                // json2kt.com -palvelussa
                var item : CityWeather = gson.fromJson(response, CityWeather::class.java)

                // haetaan tarvittavat datat fragmentia varten
                // ? tarkoitttaa, että jos main-oli
                // on arvoltaan null, koodi ei yritä väkisin ajaa
                // koodia läpi koska se tilttaisi
                // tämä liittyy ns. null-safety -ominaisuuteen ohjelmointikielessä
                val temperature = item.main?.temp
                val humidity = item.main?.humidity

                // nämä jmuuttujat voi halutessaan laittaa ulkoasuun:
                // esim. jos CityWeatherin xml-ulkoasussa on TextView, jonka id on textView_city_temperature
                // voitaisiin laittaa se näkyviin näin:
                // binding.textViewCityTemperature.text = "${temperature}C"

                Log.d("TESTI", "Lämpötila: ${temperature}, kosteusprosentti: ${humidity}")

            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.d("TESTI", it.toString())
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                // basic headers for the data
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        // if using this in an activity, use "this" instead of "context"
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}