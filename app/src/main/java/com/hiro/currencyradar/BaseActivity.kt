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
import android.widget.AbsListView
import org.xmlpull.v1.XmlPullParser


class BaseActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    /*
     * Setting of BottomNavigationView
     */
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_radar -> {
                Log.d("BaseActivity: onCreate", "Button Term Clicked")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_target -> {
                Log.d("BaseActivity: onCreate", "Button Target Clicked")
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

                Log.d("MainActivity: onCreate", "Button Graph Clicked!")
//                val intent = Intent(this, GraphActivity::class.java)
//                startActivity(intent)

//                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        // BottomNavigationView setting
        val navView: BottomNavigationView = findViewById(R.id.nav_view_main)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.menu.findItem(R.id.navigation_base).isChecked = true

        // Parse XML
        val xmlElement = parseXml()

        // setting file
        val base = getBaseFromSharedPreferences()
        val basePosition = getBasePosition (xmlElement, base)

        // Create ListView of Layout for this screen
        listView = findViewById(R.id.listItems)
        val adapter = BaseItemAdapter(this, xmlElement, basePosition)
        listView.adapter = adapter

        listView.setOnScrollListener(
            object: AbsListView.OnScrollListener{
                override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
                    Log.d( "tag ###", "### p1=$p1" )
                }
            }
        )

        listView.setOnItemClickListener { _, _, position, _ ->

            val clickedCurrency = xmlElement[position]

            // save selected currency into shared file
            setBaseCodeToSharedPreferences(clickedCurrency.second)

            Log.d("BaseActivity", "### position=$position")
            Log.d("BaseActivity", "### Selected=${clickedCurrency.second}")

            // pass selected position to adapter
            adapter.initialPosition = position

            // reflect changes recursively
            adapter.notifyDataSetChanged()
        }
    }

    private fun getBaseFromSharedPreferences() : String {
        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getString(getString(R.string.base), null)  ?: getString(R.string.usd)
    }

    /*
     * Save selected base currency into shared file
     */
    private fun setBaseCodeToSharedPreferences(code: String) {
        // save selected currency into shared file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()

        //for the first time before SharedPreferences have set
        editor.putString(getString(R.string.base), code)
        editor.apply()
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

    /*
     *  Parse XML and Retrieve xml elements into ArrayList<Triple<png, code. country>> then return it.
     */
    private fun parseXml() : ArrayList<Triple<String, String, String>> {

        val xmlElement: ArrayList<Triple<String, String, String>> = ArrayList()
        val xpp :XmlResourceParser = resources.getXml(R.xml.currencies)
        var eventType = xpp.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType){
                XmlPullParser.START_DOCUMENT -> {
                    Log.d("getXml", "Start document")
                }
                XmlPullParser.START_TAG -> {
                    Log.d("getXml", "Start tag =${xpp.name}")

                    if (xpp.name == getString(R.string.item)) {
                        val tri: Triple<String, String, String> = Triple(
                            xpp.getAttributeValue(null, getString(R.string.png)),
                            xpp.getAttributeValue(null, getString(R.string.code)),
                            xpp.getAttributeValue(null, getString(R.string.country)))

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



}
