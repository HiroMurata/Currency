package com.hiro.currencyradar

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.fasterxml.jackson.core.type.TypeReference
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import kotlinx.android.synthetic.main.activity_main.*
import com.github.mikephil.charting.data.RadarData
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.os.AsyncTask
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        when (item.itemId) {
            R.id.navigation_home -> {
                textMessage.setText(R.string.usd)

                var editor = sharedPref.edit()
                editor.putString("base", "USD")
                editor.commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                textMessage.setText(R.string.eur)

                var editor = sharedPref.edit()
                editor.putString("base", "EUR")
                editor.commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                textMessage.setText(R.string.jpy)

                var editor = sharedPref.edit()
                editor.putString("base", "JPY")
                editor.commit()

                return@OnNavigationItemSelectedListener true
            }

            R.id.eur -> {
                textMessage.setText(R.string.eur)

                var editor = sharedPref.edit()
                editor.putString("base", "EUR")
                editor.commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.jpy -> {
                textMessage.setText(R.string.jpy)

                val editor = sharedPref.edit()
                editor.putString("base", "JPY")
                editor.commit()

                return@OnNavigationItemSelectedListener true
            }

//ボタンナビゲーションに幅的に５個までしか表示できないのであとで対応
//            R.id.usd -> {
//                textMessage.setText(R.string.usd)
//                return@OnNavigationItemSelectedListener true
//            }

        }
        false
    }

    // Get rate
    var latestMap: Map<String, Any> = HashMap<String, Any>()
    var periodMap: Map<String, Any> = HashMap<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val navViewTerm: BottomNavigationView = findViewById(R.id.nav_view_term)

        button.setOnClickListener {
            Log.d("MainActivity: onCreate", "Button Clicked!")
            val intent = Intent(this, MainSetting::class.java)
            startActivity(intent)
        }

        textMessage = findViewById(R.id.message)
        navViewTerm.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Generate URL
        val latestUrl: String = getLatestUrl()
        val periodUrl: String = getPeriodUrl()

        println("★ ★ ★ ★ ★  非同期の前: ")

        //Async
        AsyncTaskGetLatest().execute(latestUrl)
        AsyncTaskGetAverage().execute(periodUrl)
        AsyncTaskGetChart().execute()

        println("★ ★ ★ ★ ★  非同期のあと １: ")


        // TODO Latestのデータを解析？
        //Get target currencies and set to Label of xAxis


        val current = doubleArrayOf(1.02, 0.96, 0.93, 1.05, 0.95, 0.96)


        // TODO 平均のデータを解析？
        println("★ ★ ★ ★ ★  非同期のあと ２: ")



    }


    /*
     * Network access should be handled outside of Main
     * otherwise android.os.NetworkOnMainThreadException occur
     */
    inner class AsyncTaskGetLatest: AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg args: String): String? {

            val (request, response, result) = args[0].httpGet().responseString()

            when (result) {
                is  Result.Success -> {
                    // Show Result
                    println("Result of Asynchronous Process : " + String(response.data))
                }
                is Result.Failure -> {
                    println("Connection Failure")
                }
            }

            //===============================    JSON Purser    ===============================
            val json = String(response.data)
            return json
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            //Complicated Json must be used <String, Any>
            var map: Map<String, Any> = HashMap<String, Any>()
            val mapper = jacksonObjectMapper()

            map = mapper.readValue(result, object : TypeReference<HashMap<String, Any>>() {

            })

            latestMap = HashMap(map)
        }
    }

    /*
     * Network access should be handled outside of Main
     * otherwise android.os.NetworkOnMainThreadException occur
     */
    inner class AsyncTaskGetAverage: AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg args: String): String? {

            val (request, response, result) = args[0].httpGet().responseString()

            when (result) {
                is  Result.Success -> {
                    // Show Result
                    println("Result of Asynchronous Process : " + String(response.data))
                }
                is Result.Failure -> {
                    println("Connection Failure")
                }
            }

            //===============================    JSON Purser    ===============================
            val json = String(response.data)
            return json
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            //===============================    JSON Purser    ===============================
            //Complicated Json must be used <String, Any>
            var map: Map<String, Any> = HashMap<String, Any>()
            val mapper = jacksonObjectMapper()

            map = mapper.readValue(result, object : TypeReference<HashMap<String, Any>>() {

            })

            periodMap = HashMap(map)

            println("【onPostExecute】 latestMap=" + latestMap)
            println("【onPostExecute】 periodMap=" + periodMap)
            println("####    ####    ####    ####    fun onPostExecute : ")


        }
    }

    /*
     * Network access should be handled outside of Main
     * otherwise android.os.NetworkOnMainThreadException occur
     */
    inner class AsyncTaskGetChart: AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg args: String): String? {
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            //===============================    FOR GRAPH    ===============================
            val selected:  ArrayList<String>

            val chart = radar_chart

            var xLabels:  List<String> = getSelectedCurrency()

            val yLabels = ArrayList<String>()
            yLabels.add("")
            yLabels.add("90%")
            yLabels.add("95%")
            yLabels.add("100%")
            yLabels.add("105%")
            yLabels.add("110%")
            yLabels.add("115%")




            //表示データ取得
            chart.data = RadarData(getRadarData())


            //グラフ上の表示
            chart.apply {

                chart.invalidate()//チャートの表示を更新したいときに呼ぶ
                chart.setDrawWeb(true)
                chart.webLineWidth = 3f
                chart.description.isEnabled = true
                chart.description.text = "こういうこと"
                chart.isClickable = true
                chart.legend.isEnabled = true //凡例
                animateY(1800, Easing.EasingOption.Linear)
                chart.isRotationEnabled = false//ドラックすると回転するので制御する

                chart.xAxis.setValueFormatter(IndexAxisValueFormatter(xLabels))
//            chart.yAxis.setValueFormatter(IndexAxisValueFormatter(yLabels))




                chart.yAxis.labelPosition.ordinal.and(0)
                chart.yAxis.labelPosition.ordinal.and(1)
                chart.yAxis.labelPosition.ordinal.and(3)
                chart.yAxis.labelPosition.ordinal.and(2)

                chart.yAxis.setDrawLabels(true)//値の目盛表記
                chart.yAxis.labelCount=6

//            chart.scaleX = 1f //X方向の表示倍率
//            chart.scaleY = 1f //Y方向の表示倍率

            }



        }
    }




    private fun getLatestUrl(): String {

        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()

        //===============================    HTTP Process    ===============================
        var latestURL: String = "https://api.exchangeratesapi.io/latest"

        val base = sharedPref.getString("base","")
        val selected = sharedPref.getString("selected","")

        if (base.isNullOrEmpty()) {
            //for the first time before SharedPreferences have set
            editor.putString("base", "USD")
            editor.putString("selected", "EUR,GBP,JPY,CNY")
            editor.apply()

            latestURL = latestURL + "?base=" + "USD" + "&symbols=EUR,GBP,JPY,CNY"
        } else {
            latestURL = latestURL + "?base=" + base + "&symbols=" + selected
        }
        return latestURL
    }


    /*
     Get URL for period API
     */
    private fun getPeriodUrl(): String {

        //get start date and end date
        val pair: Pair<String, String> = getTargetDates()
        val startDate = pair.first
        val endDate = pair.second

        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        var editor = sharedPref.edit()
        val base = sharedPref.getString("base","USD")
        val selected = sharedPref.getString("selected","EUR,GBP,JPY,CNY")

        var periodURL: String = "https://api.exchangeratesapi.io/history"
        periodURL = periodURL + "?start_at=" + startDate + "&end_at=" + endDate
        periodURL = periodURL + "&base=" + base
        periodURL = periodURL + "&symbols=" + selected

        return periodURL
    }


    /*
     Get selected currencies from setting file
     */
    private fun getSelectedCurrency(): List<String> {

        val defaultStr: String = "EUR,GBP,JPY,CNY"
        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()
        val selected = sharedPref.getString("selected","")

        val items: List<String>
        if (selected.isNullOrEmpty()) {
            //for the first time before SharedPreferences have set
            editor.putString("selected", defaultStr)
            editor.apply()

            items = getItems(defaultStr)
        } else {
            items = getItems(selected)

        }
        return items
    }


    private fun getItems(str: String): List<String> {
//        val items = str.split(",")
        return str.split(",")
    }


    /*
     * Return Pair(startDate, latestDate)
     */
    private fun getTargetDates(): Pair<String, String> {

        // end date
        val date: Date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val endDate = format.format(date)

        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        var period = sharedPref.getInt("period", -1)

        if (period == -1) {
            period = 7
            //for the first time before SharedPreferences have set
            editor.putInt("period", 7)
            editor.commit()
        }

        var calendar: Calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -period)

        var startDate: Date = calendar.time


        // todo 設定から対象の期間をもってくる
        // コードがそのまま期間（日）
//        0 //
//        7 // 1 week
//        14 // 2-week
//        30 // 1-month
//        60 // 2-month
//        90 // 3-month
//        180 // half year
//        365 // 1-year
//        730 // 2-year
//        1095 // 3-year
//        1825 // 5-year

        var pair: Pair<String, String> = Pair(format.format(startDate), endDate)
        return  pair
    }




    private fun getRadarData(): ArrayList<IRadarDataSet> {
        //表示させるデータ

        println("&%&%&%&%&%&%&%    6565&%&%&%& : latestMap.get(\"rates\") : " + latestMap.get("rates"))

        val currencyList: List<String> = getSelectedCurrency()

        val rateMap = latestMap.get("rates") as HashMap<String, Double>
        println("&%&%&%&%&%&%&%    6565&%&%&%& : rateMap.get(\"EUR\") : " + rateMap.get("EUR"))
        println("&%&%&%&%&%&%&%    6565&%&%&%& : rateMap.get(\"GBP\") : " + rateMap.get("GBP"))
        println("&%&%&%&%&%&%&%    6565&%&%&%& : rateMap.get(\"JPY\") : " + rateMap.get("JPY"))
        println("&%&%&%&%&%&%&%    6565&%&%&%& : rateMap.get(\"CNY\") : " + rateMap.get("CNY"))

        val latest: MutableList<Double> = mutableListOf()
        currencyList.forEach {
            val dbl : Double? = rateMap.get(it)
            if (dbl != null)
                latest.add(dbl)
            println("the element at $it " + dbl)
        }


        println("latest" + latest.size)
        var entries = ArrayList<RadarEntry>().apply {
            for ((index, value) in latest.withIndex()) {
                add(RadarEntry(value.toFloat(), index))
                println("the value at $index is $value")
            }
        }

        val average = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f)
        val entries2 = ArrayList<RadarEntry>().apply {
            add(RadarEntry(average[0], 0))
            add(RadarEntry(average[1], 1))
            add(RadarEntry(average[2], 2))
            add(RadarEntry(average[3], 3))
            add(RadarEntry(average[4], 4))
            add(RadarEntry(average[5], 5))
        }


        val dataSet = RadarDataSet(entries, "current")
        dataSet.apply {
            //整数で表示
            valueFormatter = IValueFormatter { value, _, _, _ -> "" + value.toInt() }
            //塗りつぶし
            setDrawFilled(true)
            fillColor = Color.BLUE

            //ハイライト
            isHighlightEnabled = true
            highLightColor = Color.BLUE
            //B色をセット
//                setColors(intArrayOf(R.color.material_blue, R.color.material_green, R.color.material_yellow), this@MainActivity)
            color = Color.BLUE
//                setColor(R.color.material_yellow)
        }

        val dataSet2 = RadarDataSet(entries2, "avarage").apply {
            //整数で表示
//                valueFormatter = IValueFormatter { value, _, _, _ -> "" + value.toInt() }
            //塗りつぶし
            setDrawFilled(true)
            fillColor = Color.RED

            //ハイライトさせない
            isHighlightEnabled = false
            //色をセット
//                setColors(intArrayOf(R.color.material_blue), this@MainActivity)
//                setColor(R.color.material_blue)
//                color = R.color.material_blue
            highLightColor = Color.RED
            color = Color.RED

        }



        val radarDataSets = ArrayList<IRadarDataSet>()
        radarDataSets.add(dataSet)
        radarDataSets.add(dataSet2)


        return radarDataSets
    }






}

