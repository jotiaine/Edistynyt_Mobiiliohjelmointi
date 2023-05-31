package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.jonitiainen.edistynytmobiiliohjelmointi.BuildConfig
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentCustomViewTesterBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.datatypes.weatherstation.WeatherStation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class CustomViewTesterFragment : Fragment() {
    private var _binding: FragmentCustomViewTesterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // client-olio, jolla voidaan yhdistää MQTT-brokeriin koodin avulla
    private lateinit var client: Mqtt3AsyncClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomViewTesterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.speedView.speedTo(35f)

        binding.customTemperatureViewTest.changeTemperature(14)

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
        client!!.connectWith()
            .simpleAuth()
            .username(BuildConfig.MQTT_USERNAME)
            .password(BuildConfig.MQTT_PASSWORD.toByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { _: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable != null) {
                    Log.d("ADVTECH", "Connection failure.")
                } else {
                    // Setup subscribes or start publishing
//                    subscribeToTopic()
                }
            }

        // lisätty nappi, jolla voidaan asettaa satunnainen lämpötila
        binding.buttonSetRandomTemperature.setOnClickListener {
            val randomTemperature: Int = (-40..40).random()
            binding.customTemperatureViewTest.changeTemperature(randomTemperature)
            Log.d("ADVTECH", randomTemperature.toString())
        }

        // testi-Button, jolla voidaan lisätä satunnainen viesti
        binding.buttonAddTestData.setOnClickListener {
            subscribeToTopic()
        }

        return root
    }

    // apufunktio/metodi jolla yhdistetään sääaseman topiciin
    // JOS yhteys onnistui aiemmin
    private fun subscribeToTopic() {
        // alustetaan GSON
        val gson = GsonBuilder().setPrettyPrinting().create()


        client!!.subscribeWith()
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
                    var item: WeatherStation = gson.fromJson(result, WeatherStation::class.java)
                    Log.d("ADVTECH", item.d.get1().v.toString() + "C")

                    // asetetaan teksimuuttuja käyttöliittymään, jossa on säätietoja
                    val temperature = item.d.get1().v
                    var humidity = item.d.get3().v
                    var text = "Temperature: ${temperature}\u2103"
                    text += "\n"
                    text += "Humidity: ${humidity}%"

                    val sdf = SimpleDateFormat("HH:mm:ss")
                    val currentDate = sdf.format(Date())

                    var dataText: String =
                        "$currentDate - Temperature: ${temperature}℃ - Humidity: ${humidity}%"

                    // koska MQTT-plugin ajaa koodia ja käsittelee dataa
                    // tausta-ajalla omassa säikeessään eli threadissa
                    // joudumme laittamaan ulkoasuun liittyvän koodin runOnUiThread-blokin-
                    // sisälle. Muutoin tulee virhe että koodit toimivat eri säikeissä
                    activity?.runOnUiThread {
                        binding.latestDataViewTest.addData(dataText)
                        binding.customTemperatureViewTest.changeTemperature(temperature.toInt())
                        binding.speedView.speedTo(temperature.toFloat())
                    }
                } catch (e: Exception) {
                    Log.d("ADVTECH", e.message.toString())
                    Log.d("ADVTECH", "Saattaa olla diagnostiikkadataa.")
                }

            }
            .send()
            .whenComplete { _, throwable ->
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

        client!!.disconnect()
    }

}