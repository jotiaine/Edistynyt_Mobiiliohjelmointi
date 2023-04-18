package com.jonitiainen.edistynytmobiiliohjelmointi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentCommentDetailBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentDetailBinding

class CommentDetailFragment : Fragment() {
    private var _binding: FragmentCommentDetailBinding? = null
    // get fragment parameters from previous fragment
    val args: CommentDetailFragmentArgs by navArgs()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // print out the given parameter into logs
        Log.d("ADVTECH", "Kommentin id-parametri: " + args.id.toString())

        // tästä eteenpäin detail fragmentin voi tehdä pääpiirteittäin kolmella eri tavalla
        // detail fragmentin idea tässä tapauksessa on ottaa vastaan tai hakea yhden kommentin data
        // ja asettaa ne esim. erillisiin TextVieweihin (tee itse TextViewit tämän fragmentin ulkoa)


        // vaihtoehto 1: lähetetään Detail fragmentiin pelkkä kommentin id
        // idea on se että rakennetaan uusi URL/osoite rajapintaan, ja haetaan vain yksi kommentti

        // vaihtoehto 2: lähetetään detail fragmentin kaikki kommentin muuttujat omina argumentteinaan
        // ja tulostetaan ne sellaisenaan. hyvä puoli: helppo ratkaisu, huono puoli: jos data kerkeää muuttua
        // välissä rajapinnassa data-palvelussa, silloin mobiilisovellus näyttää vanhentunutta dataa

        // vaihtoehto 3: Lähetetään detail fragmenttiin JSON-muodossa oleva Comment-objekti, ja puretaan
        // se tässä fragmentissa GSONin avulla Comment-objektiksi

        // jos mennään vaihtoehdolla 1, saadaan aina ajantasaisin data rajapinnasta
        // tätä varten tarvitaan generoitu yhden kommentin URL/osoite, joka voidaan hakea, esim:
        val JSON_URL = "https://jsonplaceholder.typicode.com/comments/" + args.id
        Log.d("TESTI", JSON_URL)

        // tätä JSON_URLia hyödyntämällä, lataa Volleyllä raakadata, ja muuta se JSONIKSI
        // GSONilla objektimuotoon:
        // var item : TodoItem = gson.fromJSON(response, TodoItem::class.java)

        // tämän jälkeen item-muuttujan avulla, asetetaan arvot minne halutaan:
        // binding.jokutextview.text = item.email jne.

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}