package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.jonitiainen.edistynytmobiiliohjelmointi.BuildConfig
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentRemoteMessageBinding

class RemoteMessageFragment : Fragment() {
    private var _binding: FragmentRemoteMessageBinding? = null
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
        _binding = FragmentRemoteMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonSendRemoteMessage.setOnClickListener {
            // any version, if you want to send (publish) data:
            // remember, if you want to send JSON-data, use GSON!

            // oikea import: Kotlin Random
            // var randomNumber = Random.nextInt(0, 100)
            var message = binding.editTextMessage.text.toString()
            //var stringPayload = "Hello world! " + randomNumber.toString()

            client.publishWith()
                .topic(BuildConfig.HIVEMQ_TOPIC)
                .payload(message.toByteArray())
                .send()
        }

        // version 3, IBM Cloud, weather station
        // käytetään tällä kaertaa aina samaa client id:tä ettei ilmaiset
        // client id:t kulu ilmaisessa HiveMQ-palvelussa
        client = MqttClient.builder()
            .useMqttVersion3()
            .sslWithDefaultConfig()
            .identifier("android2023jt01test")
            .serverHost(BuildConfig.HIVEMQ_BROKER)
            .serverPort(8883)
            .buildAsync()

        // yhdistetään käyttäjätiedoilla (username/password)
        client.connectWith()
            .simpleAuth()
            .username(BuildConfig.HIVEMQ_USERNAME)
            .password(BuildConfig.HIVEMQ_PASSWORD.toByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { _: Mqtt3ConnAck?, throwable: Throwable? ->
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
    fun subscribeToTopic() {
        client.subscribeWith()
            .topicFilter(BuildConfig.HIVEMQ_TOPIC)
            .callback { publish ->

                // this callback runs everytime your code receives new data payload
                // muutetaan raakadata tekstiksi (tässä tapauksessa JSONia)
                var result = String(publish.getPayloadAsBytes())

                activity?.runOnUiThread {
                    binding.textViewRemoteMessage.text = result
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

        client.disconnect()
    }
}