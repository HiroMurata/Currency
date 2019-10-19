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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import kotlinx.android.synthetic.main.activity_main.*
import com.github.mikephil.charting.data.RadarData
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.content.res.XmlResourceParser
import android.os.AsyncTask
import com.github.mikephil.charting.formatter.IValueFormatter
import org.xmlpull.v1.XmlPullParser
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap as HashMap1


class MainActivity : AppCompatActivity() {

    // Get rate
    var latestMap: Map<String, Any> = HashMap()
    var periodMap: Map<String, Any> = HashMap()
    var selectedCurrencyList: List<String> = ArrayList()

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.navigation_base -> {
                Log.d("MainActivity: onCreate", "Button Home Clicked!")
                val intent = Intent(this, BaseActivity::class.java)
                startActivity(intent)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_target -> {
                Log.d("MainActivity: onCreate", "Button Target Clicked!")
                val intent = Intent(this, TargetActivity::class.java)
                startActivity(intent)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_term -> {
                Log.d("BaseActivity: onCreate", "Button Term Clicked")
                val intent = Intent(this, TermActivity::class.java)
                startActivity(intent)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_graph -> {
//                Log.d("MainActivity: onCreate", "Button Graph Clicked!")
//                val intent = Intent(this, GraphActivity::class.java)
//                startActivity(intent)
//
//                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.codeTextView)
        textView.text = getBaseCurrency()

        // BottomNaviView setting
        val navView: BottomNavigationView = findViewById(R.id.nav_view_main)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.getMenu().findItem(R.id.navigation_radar).isChecked = true

        selectedCurrencyList = getTargetCurrencies()

        // Generate URL
        val latestUrl: String = getLatestUrl()
        val periodUrl: String = getPeriodUrl()

        Log.d("【latestUrl】 :", latestUrl)
        Log.d("【periodUrl】 :", periodUrl)

        //Async
        AsyncTaskGetLatest().execute(latestUrl)
        AsyncTaskGetAverage().execute(periodUrl)
        AsyncTaskGetChart().execute()
    }


    /*
     * Network access should be handled outside of Main
     * otherwise android.os.NetworkOnMainThreadException occur
     */
    inner class AsyncTaskGetLatest: AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg args: String): String? {

            val (_, response, result) = args[0].httpGet().responseString()

            when (result) {
                is  Result.Success -> {
                    // Show Result
                    Log.d("Result Asynch Process:", String(response.data))
                }
                is Result.Failure -> {
                    Log.d("Result Asynch Process:", "Connection Failure")
                }
            }

            //===============================    JSON Purser    ===============================
            val json = String(response.data)
            return json
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            //Complicated Json must be used <String, Any>
            val map: Map<String, Any>
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

            val (_, response, result) = args[0].httpGet().responseString()

            when (result) {
                is  Result.Success -> {
                    // Show Result
                    Log.d("Result Asynch Process:", String(response.data))
                }
                is Result.Failure -> {
                    Log.d("Result Asynch Process:", "Connection Failure")
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
            val map: HashMap<String, Any>
            val mapper = jacksonObjectMapper()

            map = mapper.readValue(result, object : TypeReference<HashMap<String, Any>>() {
            })

            periodMap = HashMap(map)
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
            val chart = radar_chart

            //表示データ取得
            chart.data = RadarData(getRadarData())

            //グラフ上の表示
            chart.apply {

                chart.invalidate()//チャートの表示を更新したいときに呼ぶ
                chart.setDrawWeb(true)
                chart.fitsSystemWindows = true

//                chart.setBackgroundColor(Color.rgb(60, 65, 82));
                chart.webLineWidth = 1f
                chart.webLineWidthInner = 1f
                chart.webColorInner = Color.LTGRAY
//              chart.setWebColor(Color.LTGRAY)
                chart.webAlpha = 500 //Webの色の濃さ？

                chart.description.isEnabled = false // descriptionを表示する
                chart.description.text = "あいうえお"
                chart.isClickable = true
                chart.legend.isEnabled = true //凡例
                animateY(800, Easing.EasingOption.Linear)
                chart.isRotationEnabled = true //rotation of graph

                chart.xAxis.valueFormatter = IndexAxisValueFormatter(selectedCurrencyList)
//                chart.xAxis.labelRotationAngle = -30f
//                chart.xAxis.yOffset = 100.3f
//                chart.xAxis.xOffset = 100.3f


//                chart.yAxis.setValueFormatter(IndexAxisValueFormatter(yLabels))
//                chart.yAxis.labelRotationAngle = -30f
//                chart.yAxis.setTypeface(tfLight)
//                chart.yAxis.setLabelCount(5, true) ?
//                chart.yAxis.setLabelCount(5) ?
                chart.yAxis.textSize = 9f
                chart.yAxis.textColor = Color.BLUE
//                chart.yAxis.setAxisMinimum(0.95f)
//                chart.yAxis.setAxisMaximum(1.05f)
                chart.yAxis.setDrawTopYLabelEntry(true)
//                chart.yAxis.yOffset = 100.3f
//                chart.yAxis.xOffset = 100.3f


//                chart.yAxis.labelPosition.ordinal.and(0)
////                chart.yAxis.labelPosition.ordinal.and(1)
                chart.yAxis.labelPosition.ordinal.and(4)
//                chart.yAxis.labelPosition.ordinal.and(-1)

                chart.yAxis.setDrawLabels(true)//値の目盛表記
//                chart.yAxis.labelCount=10 //？？？不明

//                chart.scaleX = 0.95f //X方向の表示倍率
//                chart.scaleY = 0.95f //Y方向の表示倍率

            }
        }
    }


    /**
     * Get URL for latest API
     */
    private fun getLatestUrl(): String {

        val base = getBaseCurrency()
        var latestURL: String = getString(R.string.latest_url)
        val csvStr = Utils.createCsvStringFromArrayList(selectedCurrencyList as ArrayList<String>)

        latestURL = "$latestURL?base=$base&symbols=$csvStr"
        return latestURL
    }


    /**
     * Get URL for period API
     */
    private fun getPeriodUrl(): String {

        //get start date and end date
        val pair: Pair<String, String> = getTargetDates()
        val startDate = pair.first
        val endDate = pair.second

        val base = getBaseCurrency()

        val csvStr = Utils.createCsvStringFromArrayList(selectedCurrencyList as ArrayList<String>)

        var periodURL: String = getString(R.string.period_url)
        periodURL = "$periodURL?start_at=$startDate&end_at=$endDate&base=$base&symbols=$csvStr"

        return periodURL
    }

    /**
     * Get selected currencies from setting file
     */
    private fun getBaseCurrency(): String {

        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()
        var base : String? = sharedPref.getString(getString(R.string.base), "")

        return when (base) {
            null, "" -> {
                base = getString(R.string.init_currency)

                //for the first time before SharedPreferences have set
                editor.putString(getString(R.string.base), base)
                editor.apply()

                base //return
            }
            else ->
                base //return
        }
    }

    /*
     Get selected currencies from setting file
     */
    private fun getTargetCurrencies(): ArrayList<String> {

        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()
        val targets : String? = sharedPref.getString("targets", "")

        val items : ArrayList<String>
        items = when (targets){
            null, "" -> {
                //for the first time before SharedPreferences have set
                editor.putString("targets", getString(R.string.init_targets))
                editor.apply()

                ArrayList(getItems(getString(R.string.init_targets)))
            }
            else -> {
                ArrayList(getItems(targets))
            }
        }
        // remove base from selected just in case
        val base : String = sharedPref.getString("base","") ?: ""
        items.remove(base)

        return items
    }


    private fun getItems(str: String): List<String> {
        return str.split(",")
    }


    /*
     * Return Pair(startDate, latestDate)
     */
    private fun getTargetDates(): Pair<String, String> {

        // end date
        val date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val endDate = format.format(date)

        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()
        var period = sharedPref.getInt("period", -1)

        if (period == -1) {
            period = 7
            //for the first time before SharedPreferences have set
            editor.putInt("period", 7)
            editor.apply()
        }

        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -period)

        val startDate: Date = calendar.time

        return Pair(format.format(startDate), endDate)
    }



    private fun getLatestRate(): ArrayList<Float> {
        val latest = arrayListOf<Float>()
        val rateMap = latestMap["rates"] as HashMap<String, Double>

        selectedCurrencyList.forEach {
            val dbl : Double? = rateMap[it]
            if (dbl != null)
                latest.add(dbl.toFloat())
            println("the element at $it $dbl")
        }
        return latest
    }

    private fun getAverageRate(): ArrayList<Float> {

        // HashMap like  2019-07-26 : {EUR=0.897827258, CNY=6.8781648411, JPY=108.6909678578, GBP=0.8047495062}
        val dailyMap = periodMap.get("rates") as HashMap<*, *>
        Log.d("getAverageRate : ", "★ ★ ★ ★ ★ ★& : dailyMap : $dailyMap")
        Log.d("getAverageRate : ", "★ ★ ★ ★ ★ ★& : dailyMap size: " + dailyMap.size)

        DoubleArray(selectedCurrencyList.size)
        var rateSumMap : HashMap<String, Double> = HashMap()
        selectedCurrencyList.forEach {
            // initialize rateSumMap with selected currency and value 0.0
            rateSumMap = hashMapOf(it to 0.0)
        }

        // Get Sum amount for each currency by using HashMap
        for ((k, v) in dailyMap) {
        // dailyMap as key: currency, value: value. such like  2019-07-31 : {EUR=0.89678, CNY=6.80347, ....}, 2019-08-02 : {EUR=0.90041, CNY=6.938694, ....}
            Log.d("getAverageRate : ", "\n☆☆☆ dailyMap : $k : $v")

            @Suppress("UNCHECKED_CAST")
            val oneDayMap = v as HashMap<String, Double>
            // oneDayMap as key: currency : value: value. such like EUR=0.897827258, CNY=6.8781648411, JPY=108.6909678578, GBP=0.8047495062

            var dbl: Double
            selectedCurrencyList.forEach {
                dbl = rateSumMap[it]?: 0.0
                dbl += oneDayMap[it]?: 0.0
                rateSumMap[it] = dbl
            }
        }

        Log.d("getAverageRate : ", "\n☆☆☆ ☆☆☆☆☆☆ rateSumMap サイズ: ${rateSumMap.size}")

        val returnList: ArrayList<Float> = ArrayList()
        selectedCurrencyList.forEach {
            Log.d("getAverageRate : ", "\n☆☆☆ ☆☆☆☆☆☆ rateSumMap[it] : $rateSumMap[it]")
            returnList.add((rateSumMap[it]?.toFloat() ?: 0f) / dailyMap.size)

        }
        return returnList
    }


    private fun getRadarData(): ArrayList<IRadarDataSet> {

        // make average radar with 1.0f
        val averageEntries = ArrayList<RadarEntry>().apply {
            for (index in selectedCurrencyList.indices) {
                add(RadarEntry(1.0f, index))
            }
        }

        // Compare with latest rate and average rate, then show radar chart
        val latestRate= getLatestRate()
        val averageRate= getAverageRate()

        val latestEntries = ArrayList<RadarEntry>().apply {
            for (index in selectedCurrencyList.indices) {
                Log.d("getRadarData : ", "★  ★  ★ 【averageRate[$index]】" + averageRate[index])
                Log.d("getRadarData : ", "★  ★  ★ 【latestRate[$index]】" + latestRate[index])

                val value = latestRate[index]/averageRate[index]
                add(RadarEntry(value, index))

                Log.d("getRadarData : ", "     ■ □ ■ □ グラフに埋め込む値[$index]【$value】")
            }
        }

        val dataSet = RadarDataSet(latestEntries, "Latest")
        dataSet.apply {
            // Setting of Decimal place
            valueFormatter = IValueFormatter { value, _, _, _ -> "" + "%.5f".format(value) }
            //塗りつぶし
            setDrawFilled(false)
            fillColor = Color.BLUE

            //ハイライト
            isHighlightEnabled = true
            highLightColor = Color.BLUE
            //B色をセット
//                setColors(intArrayOf(R.color.material_blue, R.color.material_green, R.color.material_yellow), this@MainActivity)
            color = Color.rgb(0,0,255)
//                setColor(R.color.material_yellow)
        }

        // Parse XML
        val termId = getTermIdFromSharedPreferences()
        val xmlElement = parseXml()
        val termPosition = getTermPosition (xmlElement, termId)

        val dataSet2 = RadarDataSet(averageEntries, "Average of past ${termPosition.second}").apply {
            // Setting of Decimal place
            valueFormatter = IValueFormatter { value, _, _, _ -> "" + "%.3f".format(value) }

            //塗りつぶし
            setDrawFilled(false)
            fillColor = Color.RED

            //ハイライトさせない
            isHighlightEnabled = true
            //色をセット
//                setColors(intArrayOf(R.color.material_blue), this@MainActivity)
//                setColor(R.color.material_blue)
//                color = R.color.material_blue
            highLightColor = Color.RED
            color = Color.rgb(255,0,255)

        }

        val radarDataSets = ArrayList<IRadarDataSet>()
        radarDataSets.add(dataSet)
        radarDataSets.add(dataSet2)

        return radarDataSets
    }

    /**
     *  Parse XML and Retrieve xml elements into ArrayList<Triple<term_id, term, days>> then return it.
     */
    private fun parseXml() : ArrayList<Triple<String, String, Int>> {

        val xmlElement: ArrayList<Triple<String, String, Int>> = ArrayList()
        val xpp : XmlResourceParser = resources.getXml(R.xml.terms)
        var eventType = xpp.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType){
                XmlPullParser.START_DOCUMENT -> {
                    Log.d("getXml", "Start document")
                }
                XmlPullParser.START_TAG -> {
                    Log.d("getXml", "Start tag =${xpp.name}")

                    if (xpp.name == getString(R.string.item)) {
                        val tri: Triple<String, String, Int> = Triple(
                                xpp.getAttributeValue(null, getString(R.string.term_id)),
                                xpp.getAttributeValue(null, getString(R.string.item_term)),
                                (xpp.getAttributeValue(null, getString(R.string.days))).toInt())

                        xmlElement.add(tri)
                    }
                }
                XmlPullParser.END_TAG -> {
                }
                XmlPullParser.TEXT -> {
                    Log.d("getXml", "### Text=${xpp.text}")
                }
            }
            eventType = xpp.next()
        }
        // indicate app done reading the resource.
        xpp.close()

        return  xmlElement
    }

    private fun getTermIdFromSharedPreferences() : String {
        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getString(getString(R.string.term_id), null)  ?: getString(R.string.init_term_id)
    }

    private fun getTermPosition(xml: ArrayList<Triple<String, String, Int>>, position: String) : Triple<String, String, Int> {
        xml.forEach {
            if (it.first == position)
                return it
        }
        return Triple("0", "1 Week", 7)
    }


}
