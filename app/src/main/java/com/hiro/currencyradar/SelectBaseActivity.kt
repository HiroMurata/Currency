package com.hiro.currencyradar

import android.content.Intent
import android.content.res.XmlResourceParser
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.graphics.BitmapFactory
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_select_base.*
import org.xmlpull.v1.XmlPullParser




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

        val xrp :XmlResourceParser = getResources().getXml(R.xml.currencies)

        // Xml Parser
        var xmlElement: ArrayList<Triple<String, String, String>> = ArrayList()
        var eventType = xrp.getEventType()
        while (eventType != XmlPullParser.END_DOCUMENT) {

            // instead of the following if/else if lines
            // you should custom parse your xml
            when (eventType){
                XmlPullParser.START_DOCUMENT -> {
                    println("Start document")
                }
                XmlPullParser.START_TAG -> {
                    if (xrp.getName() == "item") {
                        val tri: Triple<String, String, String> = Triple(
                            xrp.getAttributeValue(null, "png"),
                            xrp.getAttributeValue(null, "code"),
                            xrp.getAttributeValue(null, "country"))

                        System.out.println("Start tag " + xrp.getName())
                        System.out.println("id " + xrp.getAttributeValue(null, "id"))
                        System.out.println("code " + xrp.getAttributeValue(null, "code"))
                        System.out.println("country " + xrp.getAttributeValue(null, "country"))

                        xmlElement.add(tri)
                    }
                }
                XmlPullParser.END_TAG -> {

                }

            }

            if (eventType == XmlPullParser.START_DOCUMENT) {
                println("Start document")
            } else if (eventType == XmlPullParser.START_TAG) {

                if (xrp.getName() == "item") {
                    val tri: Triple<String, String, String> = Triple(
                        xrp.getAttributeValue(null, "png"),
                        xrp.getAttributeValue(null, "code"),
                        xrp.getAttributeValue(null, "country"))

                    System.out.println("Start tag " + xrp.getName())
                    System.out.println("id " + xrp.getAttributeValue(null, "id"))
                    System.out.println("code " + xrp.getAttributeValue(null, "code"))
                    System.out.println("country " + xrp.getAttributeValue(null, "country"))

                    xmlElement.add(tri)
                }

            } else if (eventType == XmlPullParser.END_TAG) {
                System.out.println("End tag " + xrp.getName())
            } else if (eventType == XmlPullParser.TEXT) {
                System.out.println("Text " + xrp.getText())
            }
            eventType = xrp.nextToken()
        }
        // indicate app done reading the resource.
        xrp.close()

        xmlElement.forEachIndexed { index, triple ->
            System.out.println("☆XML")
            System.out.println(xmlElement[index].first)
            System.out.println(xmlElement[index].second)
            System.out.println(xmlElement[index].third)
        }

        // try-with-resources
        try {
            resources.assets.open("images/eu.png").use { istream ->
                val bitmap = BitmapFactory.decodeStream(istream)
                image_view.setImageBitmap(bitmap)
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
