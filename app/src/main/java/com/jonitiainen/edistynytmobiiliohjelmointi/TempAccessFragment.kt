package com.jonitiainen.edistynytmobiiliohjelmointi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentDataBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentTempAccessBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.fragments.DataFragmentDirections
import org.json.JSONObject
import java.io.UnsupportedEncodingException

class TempAccessFragment : Fragment() {
    private var _binding: FragmentTempAccessBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // HUOM! Tämä esimerkki on tehty hyvin pitkälle tyyliin "siitä mistä aita on matalin".
    // Jos haluat optimoida tätä rakennetta, ks. alla olevat kommentit

    // tällä hetkellä koodin logiikka on tämä:

    // jos datan hakemisessa tulee Auth-error -> kirjaudutaan kokonaan uudestaan rajapintaan.
    // tämäkin ratkaisu toimii (varsinkin pienillä käyttäjämäärillä), mutta tämän johdosta
    // Android-sovellus tekee paljon turhia kyselyjä Directusiin, koska kirjautuminen tehdään
    // aina virheen sattuessa tai fragmentin latautuessa uudelleen

    // tämä voi muodostua ongelmaksi, mikäli sovelluksella on tuhansia aktiivisia käyttäjiä.
    // tällaisessa tilanteessa jokainen säästetty ja optimoitu kysely Directusin rajapintaan
    // säästää Androidin käyttämää suoritusaikaa, akkua sekä myös Directusista tulevaa käyttölaskua.
    // Mitä vähemmän Android-sovellus "rassaa" Directusin rajapintaa, sen halvempi ja energiatehokkaampi
    // oma Android-sovellus on.

    // Parannusehdotus 1:

    // hyödynnetään refresh tokenia access tokenin uusimisessa (kevyempi operaatio kuin uudestaan kirjautuminen)
    // refresh token tulee samassa datassa kuin access token myös. Access token on 15min voimassa, ja refresh
    // token on 7 päivää voimassa. Refresh tokenin avulla voi pyytää uuden access tokenin, mikäli refresh token
    // on vielä voimassa. Jos myös refresh token vanhenee -> kirjaudutaan uudestaan. (tämä logiikka pitää koodata itse)

    // Parannusehdotus 2:

    // Directusin kirjautumisdatassa tulee mukana myös tieto siitä, kuinka kauan access token on voimassa kirjautumishetkestä
    // alkaen, oletuksena 900000 millisekuntia -> 900 sekuntia -> 15min
    // Voidaan koodata Android-sovellus siten, että niin kauan kuin aikaa on jäljellä, Directusiin ei lähetetä
    // yhtään kirjautumispyyntöä. Tällä tavalla Android-sovellus ei turhaan ole yhteydessä Directusiin,
    // koska äppi pitää itse kirjaa siitä milloin pitää kirjautua uusiksi.

    // Parannusehdotus 3:

    // kaikki kirjautumiseen liittyvä Volley-logiikka on hyvä keskittää yhteen paikkaan, joko kustomoituun
    // Application -luokkaan, tai tehdä (suositellumpi tapa) Volley-kutsuille oma Singleton-luokka.
    // ks. Google ja Volleyn dokumentaatio esimerkistä miten tämä tehdään.

    // seuraavat laitettu local.propertiesiin
    // muista käyttää ohjelmaa päällä jotta BuildConfig päivittyy

    // DIRECTUS_HOST=http://10.0.2.2:8055
    // DIRECTUS_EMAIL=joku@jossain.com
    // DIRECTUS_PASSWORD=Password123!

    // VARIABLES USED BY THE SESSION MANAGEMENT
    val LOGIN_URL = BuildConfig.DIRECTUS_HOST + "/auth/login"

    // these should be placed in the local properties file and used by BuildConfig
    // JSON_URL should be WITHOUT a trailing slash (/)!
    val JSON_URL = BuildConfig.DIRECTUS_HOST

    // if using username + password in the service (e.g. Directus), use these
    val username = BuildConfig.DIRECTUS_TEMP_USERNAME
    val password = BuildConfig.DIRECTUS_TEMP_PASSWORD

    // request queues for requests
    var requestQueue: RequestQueue? = null
    var refreshRequestQueue: RequestQueue? = null

    // state booleans to determine our session state
    var loggedIn: Boolean = false
    var needsRefresh: Boolean = false

    // stored tokens. refresh is used when our session token has expired
    // access token in this case is the same as session token
    var refreshToken = ""
    var accessToken = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTempAccessBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonGetRawData.setOnClickListener {
            dataAction()
        }

        return root
    }

    // fragment entry point
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);

        requestQueue = Volley.newRequestQueue(context)
        refreshRequestQueue = Volley.newRequestQueue(context)

        // start with login
        loginAction()
    }

    // button methods
    fun loginAction()
    {
        Log.d("TESTI", "login")
        Log.d("TESTI", JSON_URL + " login")
        requestQueue?.add(loginRequest)
    }

    fun refreshLogin() {
        if (needsRefresh) {
            loggedIn = false
            // use this if using refresh logic
            //refreshRequestQueue?.add(loginRefreshRequest)

            // if using refresh logic, comment this line out
            loginAction()
        }
    }

    fun dataAction() {
        if (loggedIn) {
            requestQueue?.add(dataRequest)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // REQUEST OBJECTS

    // REQUEST OBJECT 1: LOGIN
    var loginRequest: StringRequest = object : StringRequest(
        Request.Method.POST, LOGIN_URL,
        Response.Listener { response ->

            var responseJSON: JSONObject = JSONObject(response)

            // save the refresh token too if using refresh logic
            // refreshToken = responseJSON.get("refresh_token").toString()

            // this part depends completely on the service that is used
            // Directus uses the data -> access_token -approach
            // IBM Cloud handles the version in comments
            // accessToken = responseJSON.get("access_token").toString()
            accessToken = responseJSON.getJSONObject("data").get("access_token").toString()

            loggedIn = true

            // after login's done, get data from API
            dataAction()

            Log.d("TESTI", response)
        },
        Response.ErrorListener {
            // typically this is a connection error
            Log.d("TESTI", it.toString())
        }) {
        @Throws(AuthFailureError::class)
        override fun getHeaders(): Map<String, String> {
            // we have to provide the basic header info
            // + Bearer info => accessToken
            val headers = HashMap<String, String>()
            headers["Accept"] = "application/json"

            // IBM Cloud expects the Content-Type to be the following:
            // headers["Content-Type"] = "application/x-www-form-urlencoded"

            // for Directus, the typical approach works:
            headers["Content-Type"] = "application/json; charset=utf-8"

            return headers
        }

        // use this to build the needed JSON-object
        // this approach is used by Directus, IBM Cloud uses the commented version instead
        @Throws(AuthFailureError::class)
        override fun getBody(): ByteArray {
            // this function is only needed when sending data
            var body = ByteArray(0)
            try {
                // on how to create this newData -variable
                var newData = ""

                // a very quick 'n dirty approach to creating the needed JSON body for login
                newData = "{\"email\":\"${username}\", \"password\": \"${password}\"}"

                // JSON to bytes
                body = newData.toByteArray(Charsets.UTF_8)
            } catch (e: UnsupportedEncodingException) {
                // problems with converting our data into UTF-8 bytes
            }
            return body
        }

    }

    // REQUEST OBJECT 3 : ACTUAL DATA -> FEEDBACK
    var dataRequest: StringRequest = object : StringRequest(
        Request.Method.GET, JSON_URL+"/items/feedback",
        Response.Listener { response ->
            Log.d("ADVTECH", response)

            binding.textViewTempFeedbackRaw.text = response
        },
        Response.ErrorListener {
            // typically this is a connection error
            Log.d("ADVTECH", it.toString())

            if (it is AuthFailureError) {
                Log.d("ADVTECH", "EXPIRED start")

                needsRefresh = true
                loggedIn = false
                refreshLogin()

                Log.d("ADVTECH", "EXPIRED end")
            }
        }) {
        @Throws(AuthFailureError::class)
        override fun getHeaders(): Map<String, String> {
            // we have to provide the basic header info
            // + Bearer info => accessToken
            val headers = HashMap<String, String>()
            // headers["Accept"] = "application/json"
            // headers["Content-Type"] = "application/json; charset=utf-8"
            headers["Authorization"] = "Bearer " + accessToken
            return headers
        }

    }
}