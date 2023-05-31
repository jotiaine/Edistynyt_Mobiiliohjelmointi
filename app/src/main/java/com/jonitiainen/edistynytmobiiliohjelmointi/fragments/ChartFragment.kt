package com.jonitiainen.edistynytmobiiliohjelmointi.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartZoomType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.gson.GsonBuilder
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.jonitiainen.edistynytmobiiliohjelmointi.BuildConfig
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentChartBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.databinding.FragmentDataBinding
import com.jonitiainen.edistynytmobiiliohjelmointi.datatypes.weatherstation.WeatherStation
import com.jonitiainen.edistynytmobiiliohjelmointi.fragments.DataFragmentDirections
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // lista, joka kerää lämpötilat
    private val temperatureList = mutableListOf<Double>()

    // lista, joka kerää ilmankosteudet
    private val humidityList = mutableListOf<Double>()

    // client-olio, jolla voidaan yhdistää MQTT-brokeriin koodin avulla
    private lateinit var client: Mqtt3AsyncClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // testiarvoja
//        temperatureList.add(7.5)
//        temperatureList.add(13.5)
//        temperatureList.add(16.5)


        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .title("Sääasema")
            .subtitle("Rantavitikan mittauspiste")
            .dataLabelsEnabled(true)
            .yAxisMin(-60.0)
            .yAxisMax(120.0)
            .zoomType(AAChartZoomType.Y)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Lämpötila")
                        .data(
                            temperatureList.toTypedArray()
                        ),
                    AASeriesElement()
                        .name("Kosteus")
                        .data(
                            humidityList.toTypedArray()
                        ),
                )
            )

        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        binding.aaChartView.aa_drawChartWithChartModel(aaChartModel)

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
                    subscribeToTopic()
                }
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

                //Only refresh the chart series data
                //aaChartView.aa_onlyRefreshTheChartDataWithChartModelSeries(chartModelSeriesArray)

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

                    // pidetään huoli että lämpötilalista ei kasva liian pitkäksi
                    while (temperatureList.size > 10) {
                        temperatureList.removeAt(0)
                    }

                    // pidetään huoli että kosteuslista ei kasva liian pitkäksi
                    while (humidityList.size > 10) {
                        humidityList.removeAt(0)
                    }

                    temperatureList.add(temperature)
                    humidityList.add(humidity)

                    var newArray = arrayOf(
                        AASeriesElement()
                            .name("Lämpötila")
                            .data(
                                temperatureList.toTypedArray()
                            ),
                        AASeriesElement()
                            .name("Kosteus")
                            .data(
                                humidityList.toTypedArray()
                            ),
                    )

                    // koska MQTT-plugin ajaa koodia ja käsittelee dataa
                    // tausta-ajalla omassa säikeessään eli threadissa
                    // joudumme laittamaan ulkoasuun liittyvän koodin runOnUiThread-blokin-
                    // sisälle. Muutoin tulee virhe että koodit toimivat eri säikeissä
                    activity?.runOnUiThread {
                        //aaChartView.aa_onlyRefreshTheChartDataWithChartModelSeries(chartModelSeriesArray)
                        binding.aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                            newArray,
                            false
                        )
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