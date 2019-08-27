package com.hiro.currencyradar

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.XmlResourceParser
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.widget.AbsListView
import android.widget.ListView
import org.xmlpull.v1.XmlPullParser

class TermActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    /*
     * Setting of BottomNavigationView
     */
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_radar -> {
                Log.d("TargetActivity:onCreate", "Button Term Clicked!")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_base -> {
                Log.d("TargetActivity:onCreate", "Button Base Clicked!")
                val intent = Intent(this, BaseActivity::class.java)
                startActivity(intent)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_target -> {
                Log.d("TermActivity: onCreate", "Button Target Clicked")
                val intent = Intent(this, TargetActivity::class.java)
                startActivity(intent)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_graph -> {

                Log.d("MainActivity: onCreate", "Button Graph Clicked!")
                val intent = Intent(this, GraphActivity::class.java)
                startActivity(intent)

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_term)

        // BottomNavigationView setting
        val navView: BottomNavigationView = findViewById(R.id.nav_view_main)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.menu.findItem(R.id.navigation_term).isChecked = true

        // Parse XML
        val xmlElement = parseXml()

        // setting file
        val termId = getTermIdFromSharedPreferences()
        val termPosition = getTermPosition (xmlElement, termId)

        // Create ListView of Layout for this screen
        listView = findViewById(R.id.listItems)
        val adapter = TermItemAdapter(this, xmlElement, termPosition)
        listView.adapter = adapter

        listView.setOnScrollListener(
                object: AbsListView.OnScrollListener{
                    override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {
//                    Log.d( "tag", "scroll" )
//                    Log.d( "p1,p2,p3", "p1=$p1, p2=$p2, p3=$p3" )
//                    if (p3 == p1 + p2) {
//                        Log.d( "tag ###", "process for Next Screen( be done automatically)" )
//                    }
                    }
                    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
                        Log.d( "tag ###", "### p1=$p1" )
                    }
                }
        )

        listView.setOnItemClickListener { _, _, position, _ ->

            val clickedTerm = xmlElement[position]

            // save selected currency into shared file
            setTermIdToSharedPreferences(clickedTerm.first, clickedTerm.third)

            Log.d("BaseActivity", "### position=$position")
            Log.d("BaseActivity", "### Selected=${clickedTerm.second}")

            // pass selected position to adapter
            adapter.initialPosition = position

            // reflect changes recursively
            adapter.notifyDataSetChanged()
        }
    }

    private fun getTermIdFromSharedPreferences() : String {
        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getString(getString(R.string.term_id), null)  ?: getString(R.string.init_term_id)
    }

    /*
     * Save selected term into shared file
     */
    private fun setTermIdToSharedPreferences(term_id: String, days: Int) {
        // save selected currency into shared file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()

        //for the first time before SharedPreferences have set
        editor.putString(getString(R.string.term_id), term_id)
        editor.putInt(getString(R.string.period), days)
        editor.apply()
    }


    /**
     *  選択された期間の位置を取得する
     */
    private fun getTermPosition(list: ArrayList<Triple<String, String, Int>>, termId: String) :Int {

        list.forEachIndexed { index, value ->
            if (termId == value.first) {
                return  index
            }
        }
        return  0
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



}
