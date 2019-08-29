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
import android.os.AsyncTask
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.XAxis
import android.R.layout
import com.github.mikephil.charting.R






class GraphActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    }


}

