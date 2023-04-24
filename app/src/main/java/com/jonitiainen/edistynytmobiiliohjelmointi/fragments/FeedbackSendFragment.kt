package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.jonitiainen.edistynytmobiiliohjelmointi.BuildConfig
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentFeedbackSendBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.datatypes.feedback.Feedback
import java.io.UnsupportedEncodingException

class FeedbackSendFragment : Fragment() {
    private var _binding: FragmentFeedbackSendBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedbackSendBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonSendFeedback.setOnClickListener {
            val name = binding.editTextFeedbackName.text.toString()
            val location = binding.editTextFeedbackLocation.text.toString()
            val value = binding.editTextFeedbackValue.text.toString()

            Log.d("TESTI", "Name: $name")
            Log.d("TESTI", "Location: $location")
            Log.d("TESTI", "Value: $value")

            sendFeedback(name, location, value)
        }

        return root
    }

    // apufunktio jolla lähetetään uusi Feedback Directusiin
    // Käytännössä tämän sisälle tulee Volley-koodi, mutta POST-versio
    private fun sendFeedback(name : String, location : String, value : String) {
        // this is the url where we want to get our data
        // Note: if using a local server, use http://10.0.2.2 for localhost. this is a virtual address for Android emulators, since
        // localhost refers to the Android device instead of your computer
        val API_KEY = BuildConfig.DIRECTUS_API_KEY
        val BASE_URL = BuildConfig.DIRECTUS_ADDRESS
        val JSON_URL = "${BASE_URL}access_token=${API_KEY}"

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, JSON_URL,
            Response.Listener { response ->
                Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                clearText()

                // Liikutaan FeedbackRead fragmentiin
                val action = FeedbackSendFragmentDirections.actionFeedbackSendFragmentToFeedbackReadFragment()
                findNavController().navigate(action)
            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.d("TESTI", it.toString())
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                // we have to specify a proper header, otherwise Apigility will block our queries!
                // define we are after JSON data!
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }

            // let's build the new data here
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                // this function is only needed when sending data
                var body = ByteArray(0)
                try { // check the example "Converting a Kotlin object to JSON"
                    // on how to create this newData -variable
                    // create a new TodoItem object here, and convert it to string format (GSON)
                    // Dataluokasta Feedback
                    var f : Feedback = Feedback()
                    f.name = name
                    f.location = location
                    f.value = value

                    // muutetaan Feedback-olio -> JSONiksi
                    var gson = GsonBuilder().create()
                    var newData = gson.toJson(f)
                    // JSON to bytes
                    body = newData.toByteArray(Charsets.UTF_8)
                } catch (e: UnsupportedEncodingException) {
                    // problems with converting our data into UTF-8 bytes
                }
                return body
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    private fun clearText() {
        binding.editTextFeedbackName.text = null
        binding.editTextFeedbackLocation.text = null
        binding.editTextFeedbackValue.text = null
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}