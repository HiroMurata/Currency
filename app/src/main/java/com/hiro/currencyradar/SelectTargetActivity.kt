package com.hiro.currencyradar

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_select_base.*
import kotlinx.android.synthetic.main.activity_select_base.button
import kotlinx.android.synthetic.main.activity_select_target.*

class SelectTargetActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_base -> {
                textMessage.setText(R.string.usd)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_term -> {
                textMessage.setText(R.string.eur)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_target -> {
                textMessage.setText(R.string.jpy)
                return@OnNavigationItemSelectedListener true
            }

//            R.id.eur -> {
//                textMessage.setText(R.string.eur)
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.jpy -> {
//                textMessage.setText(R.string.jpy)
//                return@OnNavigationItemSelectedListener true
//            }

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
        setContentView(R.layout.activity_select_term)

        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

        textMessage = findViewById(R.id.message)
        navViewTerm.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

    }
}
