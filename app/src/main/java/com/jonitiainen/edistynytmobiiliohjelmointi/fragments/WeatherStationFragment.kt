package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.jonitiainen.edistynytmobiiliohjelmointi.BuildConfig
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentWeatherStationBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.datatypes.weatherstation.WeatherStation
import java.util.*


class WeatherStationFragment : Fragment() {
    private var _binding: FragmentWeatherStationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.

    // client-olio, jolla voidaan yhdistää MQTT-brokeriin koodin avulla
    private lateinit var client: Mqtt3AsyncClient

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherStationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // version 3, IBM Cloud, weather station
        // Huomaa identifier eli Client ID => vain alkuosa toiseen kaksoispisteeseen
        // laitetaan local.propertiesiin, ja satunnainen tekstihäntä liitetään
        // perään UUID-kirjaston avulla
        client = MqttClient.builder()
            .useMqttVersion3()
            .sslWithDefaultConfig()
            .identifier(BuildConfig.MQTT_CLIENT_ID + UUID.randomUUID().toString())
            .serverHost(BuildConfig.MQTT_BROKER)
            .serverPort(8883)
            .buildAsync()

        // yhdistetään käyttäjätiedoilla (username/password)
        client.connectWith()
            .simpleAuth()
            .username(BuildConfig.MQTT_USERNAME)
            .password(BuildConfig.MQTT_PASSWORD.toByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { connAck: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable != null) {
                    Log.d("ADVTECH", "Connection failure.")
                } else {
                    // Setup subscribes or start publishing
                    subscribeToTopic()
                }
            }

        return root
    }

    // apufunktio/metodi jolla yhdistetään sääaseman topiciin
    // JOS yhteys onnistui aiemmin
    fun subscribeToTopic()
    {
        // alustetaan GSON
        val gson = GsonBuilder().setPrettyPrinting().create()

        client.subscribeWith()
            .topicFilter(BuildConfig.MQTT_TOPIC)
            .callback { publish ->

                // this callback runs everytime your code receives new data payload
                // muutetaan raakadata tekstiksi (tässä tapauksessa JSONia)
                var result = String(publish.getPayloadAsBytes())
                // Log.d("ADVTECH", result)

                // try/catch => koodi joka saattaa tiltata laitetaan tryn sisälle:
                // catch hoitaa virhetilanteet
                // nyt MQTT:stä tulee välillä diagnostiikkadataa, mikä rikkoo GSON-koodin
                // try/catch estää ohjelman tilttaamisen
                try {
                    // muutetaan vastaanotettu data JSONista -> WeatherStation -luokan olioksi
                    var item : WeatherStation = gson.fromJson(result, WeatherStation::class.java)
                    Log.d("ADVTECH", item.d.get1().v.toString() + "C")
                }
                catch(e : Exception) {
                    Log.d("ADVTECH", e.message.toString())
                    Log.d("ADVTECH", "Saattaa olla diagnostiikkadataa.")
                }

            }
            .send()
            .whenComplete { subAck, throwable ->
                if (throwable != null) {
                    // Handle failure to subscribe
                    Log.d("ADVTECH", "Subscribe failed.")
                } else {
                    // Handle successful subscription, e.g. logging or incrementing a metric
                    Log.d("ADVTECH", "Subscribed!")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        client.disconnect()
    }
}