package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jonitiainen.edistynytmobiiliohjelmointi.BuildConfig
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentBasicAuthBinding

class BasicAuthFragment : Fragment() {
    private var _binding: FragmentBasicAuthBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBasicAuthBinding.inflate(inflater, container, false)
        val root: View = binding.root

        basicAuthGet()

        return root
    }

    private fun basicAuthGet(){
        // this is the url where we want to get our data
        // Note: if using a local server, use http://10.0.2.2 for localhost. this is a virtual address for Android emulators, since
        // localhost refers to the Android device instead of your computer
        val JSON_URL = "https://apingweb.com/api/auth/users"



        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, JSON_URL,
            Response.Listener { response ->
                Log.d("TESTI", response)
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


                // username and pwd
                val username = BuildConfig.APINGWEB_USERNAME
                val password = BuildConfig.APINGWEB_PASSWORD
                // replace with your own API's login info
                val authorizationString = "Basic " + Base64.encodeToString(
                    ("$username:$password").toByteArray(), Base64.DEFAULT
                )

                headers.put("Authorization", authorizationString);

                return headers
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}