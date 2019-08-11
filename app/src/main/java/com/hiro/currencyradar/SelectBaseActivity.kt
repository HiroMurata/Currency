package com.hiro.currencyradar

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_select_base.button
import kotlinx.android.synthetic.main.activity_select_target.*
import android.graphics.BitmapFactory
import android.widget.ImageView


//import kotlinx.android.synthetic.main.activity_select_term.*

class SelectBaseActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
//    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//        when (item.itemId) {
//            R.id.navigation_base -> {
//                textMessage.setText(R.string.usd)
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.navigation_term -> {
//                textMessage.setText(R.string.eur)
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.navigation_target -> {
//                textMessage.setText(R.string.jpy)
//                return@OnNavigationItemSelectedListener true
//            }
//
//
////ボタンナビゲーションに幅的に５個までしか表示できないのであとで対応
////            R.id.usd -> {
////                textMessage.setText(R.string.usd)
////                return@OnNavigationItemSelectedListener true
////            }
//
//        }
//        false
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_base)

        val imageView = findViewById<ImageView>(R.id.image_view)
        val assets = resources.assets

        // try-with-resources
        try {
            assets.open("eu.png").use { istream ->
                val bitmap = BitmapFactory.decodeStream(istream)
                imageView.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }


    }
}
