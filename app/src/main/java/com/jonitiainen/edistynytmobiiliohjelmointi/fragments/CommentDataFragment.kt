package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.jonitiainen.edistynytmobiiliohjelmointi.CommentAdapter
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentCommentDataBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.datatypes.comment.Comment

class CommentDataFragment : Fragment() {
    private var _binding: FragmentCommentDataBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // alustetaan viittaus adapteriin sekä luodaan LinearLayoutManager
    // RecyclerView tarvitsee jonkin LayoutManagerin, joista yksinkertaisin on Linear
    private lateinit var adapter: CommentAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentDataBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonGetComments.setOnClickListener {
            getComments()
        }

        // luodaan linear layout manager ja kytketään se ulkoasussa olevaan RecyclerViewiin
        // tarkista, että recyclerviewin id: täsmää binding-layerin koodin kanssa
        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewComments.layoutManager = linearLayoutManager

        return root
    }

    // apufunktio/metodi, joka hakee dataa rajapinnasta
    private fun getComments() {
        Log.d("TESTI", "getComments() kutsuttu")

        // this is the url where we want to get our data from
        val JSON_URL = "https://jsonplaceholder.typicode.com/comments"

        // haetaan GSON-objekti käytettäväksi
        val gson = GsonBuilder().setPrettyPrinting().create()

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, JSON_URL,
            Response.Listener { response ->

                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                // Log.d("TESTI", response)


                var rows : List<Comment> = gson.fromJson(response, Array<Comment>::class.java).toList()

                // luodaan adapteri omalla datalla, ja kytketään adapteri recyclerviewiin
                adapter = CommentAdapter(rows)
                binding.recyclerViewComments.adapter = adapter

                /*
               // kokeillaan käyttää rows-listaa, saadaanko dataa ulos
               Log.d("TESTI", "Kommenttien määrä: " + rows.size)

               // kokeillaan loopat läpi kaikki kommentit, tulostetaan jokainen email
               for (item : Comment in rows) {
                   Log.d("TESTI", item.email.toString())
               }
               */
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