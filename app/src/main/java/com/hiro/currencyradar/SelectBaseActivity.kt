package com.hiro.currencyradar

import android.content.Intent
import android.content.res.XmlResourceParser
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.support.design.widget.BottomNavigationView
import android.util.Log
import org.xmlpull.v1.XmlPullParser




//import kotlinx.android.synthetic.main.activity_select_term.*

class SelectBaseActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_radar -> {
                Log.d("BaseActivity: onCreate", "Button Term Clicked!")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_target -> {
                Log.d("BaseActivity: onCreate", "Button Target Clicked!")
                val intent = Intent(this, SelectTargetActivity::class.java)
                startActivity(intent)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_term -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_base)

        // BottomNaviView setting
        val navView: BottomNavigationView = findViewById(R.id.nav_view_main)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.getMenu().findItem(R.id.navigation_base).setChecked(true)


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

        // setting file
        val base = getBaseFromSharedPreferences()

        val basePosition = getBasePosition (xmlElement, base)


        // Create ListView of Layout for this screen
        listView = findViewById(R.id.listItems)
        val adapter = BaseItemAdapter(this,  xmlElement, basePosition)
        listView.adapter = adapter



        listView.setOnItemClickListener { _, view, position, _ ->

            // 1
            val selectedCurrency = xmlElement[position]


            // save selected currency into shared file
            val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sharedPref.edit()
            val selected = sharedPref.getString(getString(R.string.base), selectedCurrency.second)?: getString(R.string.usd)

            //for the first time before SharedPreferences have set
            editor.putString(getString(R.string.base), selected)
            editor.apply()





            System.out.println("★★ position=$position")
//            System.out.println("★★ id=$id")

            System.out.println("★★ Selected=$selected")

            // 再帰的に呼び出す
            val adapter = BaseItemAdapter(this, xmlElement, position)
            listView.adapter = adapter


        }


    }

    private fun getBaseFromSharedPreferences() :String {
        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val base = sharedPref.getString("base","USD")
        return base ?: "USD"

    }


    /*
     *  Baseとして選択された通貨の位置を取得する
     */
    private fun getBasePosition(list: ArrayList<Triple<String, String, String>>, base: String) :Int {

        list.forEachIndexed { index, value ->
            if (base == value.second) {
                return  index
            }
        }
        return  0
    }






}
