package com.hiro.currencyradar

import android.content.Intent
import android.content.res.XmlResourceParser
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_select_base.*
import kotlinx.android.synthetic.main.table.*
import org.xmlpull.v1.XmlPullParser




//import kotlinx.android.synthetic.main.activity_select_term.*

class SelectBaseActivity : AppCompatActivity() {

    private lateinit var listView: ListView
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


        // Parse XML and Retreive xml elements into ArrayList<Triple<png, code. country>>
        val xpp :XmlResourceParser = getResources().getXml(R.xml.currencies)
        var xmlElement: ArrayList<Triple<String, String, String>> = ArrayList()
        var eventType = xpp.getEventType()

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType){
                XmlPullParser.START_DOCUMENT -> {
                    println("Start document")
                }
                XmlPullParser.START_TAG -> {
                    System.out.println("Start tag " + xpp.getName())
                    if (xpp.getName() == "item") {
                        val tri: Triple<String, String, String> = Triple(
                            xpp.getAttributeValue(null, "png"),
                            xpp.getAttributeValue(null, "code"),
                            xpp.getAttributeValue(null, "country"))

                        xmlElement.add(tri)
                    }
                }
                XmlPullParser.END_TAG -> {
                }
                XmlPullParser.TEXT -> {
                    System.out.println("Text " + xpp.getText())
                }

            }
            eventType = xpp.next()
        }
        // indicate app done reading the resource.
        xpp.close()


//        val imageView = findViewById<ImageView>(R.id.image_view)
//        val assets = resources.assets

//        // the way to get image filer from assets folder
//        // try-with-resources
//        try {
//            resources.assets.open("images/eu.png").use { istream ->
//                val bitmap = BitmapFactory.decodeStream(istream)
//                image_view.setImageBitmap(bitmap)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }




        // Create ListView of Layout for this screen
        listView = findViewById(R.id.listItems)
        val adapter = ItemAdapter(this,  xmlElement)
        listView.adapter = adapter



//        checkbox.setOnClickListener {
////            System.out.println("★★ it=$it.id")
//        }


//        button.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//
//        }

        listView.setOnItemClickListener { _, _, position, id ->
            System.out.println("★★ position=$position")
            System.out.println("★★ id=$id")
            // 1
            val selectedCurrency = xmlElement[position]

            System.out.println("★★ Selected=$selectedCurrency.second")

        }


    }




//    private fun onCheckboxClicked (savedInstanceState: Bundle?){
//
//
//    }
}
