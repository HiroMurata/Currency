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
import com.squareup.moshi.Moshi
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class MainActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                textMessage.setText(R.string.usd)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                textMessage.setText(R.string.eur)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                textMessage.setText(R.string.jpy)
                return@OnNavigationItemSelectedListener true
            }

            R.id.eur -> {
                textMessage.setText(R.string.eur)
                return@OnNavigationItemSelectedListener true
            }
            R.id.jpy -> {
                textMessage.setText(R.string.jpy)
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




        //===============================    HTTP Process    ===============================
//        val res  = Rates("USD", "", rates = Rate("", 1.24))
//        val res  : Rates?
//        res  = Rates("USD", "", rates = Rate("", 1.24))



//        //        // Synchronous Process
//        val triple = "https://api.exchangeratesapi.io/latest".httpGet().response()
//        // Show Result
//        println("Result of Synchronous Process : " + String(triple.second.data))

        // Asynchronous Process
        "https://api.exchangeratesapi.io/latest".httpGet().response { request, response, result ->
            when (result) {
                is Result.Success -> {
                    // Show Result
                    println("Result of Asynchronous Process : " + String(response.data))
                }
                is Result.Failure -> {
                    println("Connection Failure")
                }
            }


            //===============================    JSON Purser    ===============================
            val json = String(response.data)
            val mapper = jacksonObjectMapper()
            val rates = mapper.readValue<Rates>(json)

            println("######################## : ")


//            val moshi = Moshi.Builder()
//                .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
//
//            val adapter = moshi.adapter(Rates::class.java)
//            val res = adapter.fromJson(response.data.toString())
//


        }    // Asynchronous Processの対の閉じ


        //===============================    FOR GRAPH    ===============================
        val chart = radar_chart

        val xLabels = ArrayList<String>()
        xLabels.add("USD")
        xLabels.add("CNY")
        xLabels.add("MYR")
        xLabels.add("JPY")
        xLabels.add("SPD")
        xLabels.add("GBP")

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
            chart.isRotationEnabled = true//ドラックすると回転するので制御する

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

    private fun getRadarData(): ArrayList<IRadarDataSet> {
        //表示させるデータ

        val current = floatArrayOf(1.02f, 0.96f, 0.93f, 1.05f, 0.95f, 0.96f)
        val entries = ArrayList<RadarEntry>().apply {
            add(RadarEntry(current[0], 0))
            add(RadarEntry(current[1], 1))
            add(RadarEntry(current[2], 2))
            add(RadarEntry(current[3], 3))
            add(RadarEntry(current[4], 4))
            add(RadarEntry(current[5], 5))

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

