package com.hiro.currencyradar

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.XmlResourceParser
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.widget.ListView
import org.xmlpull.v1.XmlPullParser

class SelectTargetActivity : AppCompatActivity() {

    private lateinit var listView: ListView

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
                val intent = Intent(this, SelectBaseActivity::class.java)
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
        setContentView(R.layout.activity_select_target)

        // BottomNaviView setting
        val navView: BottomNavigationView = findViewById(R.id.nav_view_main)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.getMenu().findItem(R.id.navigation_target).setChecked(true)


        // Parse XML and Retreive xml elements into ArrayList<Triple<png, code. country>>
        val xpp : XmlResourceParser = getResources().getXml(R.xml.currencies)
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




        // setting file
        val targets = getTargetFromSharedPreferences()

        val basePosition = getBasePosition(xmlElement, targets)


        // Create ListView of Layout for this screen
        listView = findViewById(R.id.listItems)
        val adapter = TargetItemAdapter(this,  xmlElement, basePosition)
        listView.adapter = adapter



        listView.setOnItemClickListener { _, view, position, _ ->

/*
チェックボックスのON/Offの切り替え必要？
            editor.putString(getString(R.string.base), selected)
*/


            // 1
            val selectedCurrency = xmlElement[position]


            // save selected currency into shared file
            val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sharedPref.edit()
            val selected = sharedPref.getString(getString(R.string.targets), getString(R.string.init_targets))

            //for the first time before SharedPreferences have set

/*
設定ファイルの変更はあとで
            editor.putString(getString(R.string.base), selected)
*/
            editor.apply()





            System.out.println("★★ position=$position")
//            System.out.println("★★ id=$id")

            System.out.println("★★ Selected=$selected")

            // 再帰的に呼び出す
            val adapter = TargetItemAdapter(this, xmlElement, basePosition)
            listView.adapter = adapter


        }

    }



    private fun getTargetFromSharedPreferences(): List<String> {
        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val targets = sharedPref.getString(getString(R.string.targets), getString(R.string.init_targets))

        val items = targets.split(",")

        return targets.split(",")
    }


    /*
     *  Targetsとして選択された通貨の位置を取得する
     */
    private fun getBasePosition(list: ArrayList<Triple<String, String, String>>, targets: List<String>) : ArrayList<Int> {

        var positionList : ArrayList<Int> = ArrayList()

        list.forEachIndexed { index, value ->

            targets.forEach { target ->
                if (value.second == target) {
                    positionList.add(index)
                }
            }
        }
        return positionList
    }


}
