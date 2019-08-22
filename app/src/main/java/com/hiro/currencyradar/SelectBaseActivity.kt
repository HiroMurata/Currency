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


class SelectBaseActivity : AppCompatActivity() {

    private lateinit var listView: ListView

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
        navView.menu.findItem(R.id.navigation_base).isChecked = true


        // Parse XML and Retrieve xml elements into ArrayList<Triple<png, code. country>>
        val xpp :XmlResourceParser = resources.getXml(R.xml.currencies)
        val xmlElement: ArrayList<Triple<String, String, String>> = ArrayList()
        var eventType = xpp.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType){
                XmlPullParser.START_DOCUMENT -> {
                    Log.d("BaseActivity", "Start document")
                }
                XmlPullParser.START_TAG -> {
                    Log.d("BaseActivity", "Start tag =${xpp.name}")

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
                    Log.d("BaseActivity", "★ Text=${xpp.text}")
                }
            }
            eventType = xpp.next()
        }
        // indicate app done reading the resource.
        xpp.close()

        // setting file
        val base = getBaseFromSharedPreferences()
        val basePosition = getBasePosition (xmlElement, base)


        // Create ListView of Layout for this screen
        listView = findViewById(R.id.listItems)
        val adapter = BaseItemAdapter(this,  xmlElement, basePosition)
        listView.adapter = adapter


        listView.setOnItemClickListener { _, view, position, _ ->

            val clickedCurrency = xmlElement[position]

            // save selected currency into shared file
            val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sharedPref.edit()

            //for the first time before SharedPreferences have set
            editor.putString(getString(R.string.base), clickedCurrency.second)
            editor.apply()

            view.isSelected = true


//            listView.getChildAt(position).checkedTextView.isSelected = true
//            view.checkedTextView.isChecked = true
//            checkedTextView.isChecked = true
//            view.checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_box_orange_24dp)
//            checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_box_orange_24dp)
//            listView.getChildAt(position).checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_box_orange_24dp)

            Log.d("BaseActivity", "★ position=$position")
            Log.d("BaseActivity", "★ Selected=${clickedCurrency.second}")

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
