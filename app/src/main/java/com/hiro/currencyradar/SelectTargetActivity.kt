package com.hiro.currencyradar

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.XmlResourceParser
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import kotlinx.android.synthetic.main.list_item.view.*
import org.xmlpull.v1.XmlPullParser

class SelectTargetActivity : AppCompatActivity() {

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

        var listView: ListView


        // BottomNaviView setting
        val navView: BottomNavigationView = findViewById(R.id.nav_view_main)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.getMenu().findItem(R.id.navigation_target).setChecked(true)


        // Parse XML and retrieve xml elements into ArrayList<Triple<png, code. country>>
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
        var targetCodes = getTargetCodesFromSharedPreferences()

        var targetPositions = Utils.getTargetPositions(xmlElement, targetCodes)


        // Create ListView of Layout for this screen
        listView = findViewById(R.id.listItems)

        xmlElement.forEachIndexed { index, triple ->
            listView.setItemChecked(index, false)

        }

        targetPositions.forEach{index ->
            listView.setItemChecked(index, true)
        }


        var adapter = TargetItemAdapter(this,  xmlElement, targetPositions)
        listView.adapter = adapter


        listView.setOnItemClickListener { _, view, position, _ ->


                // AdapterView is the parent class of ListView
                if(view.isSelected){
                    Toast.makeText(getBaseContext(), "Checked? ${view.isSelected}", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(), "Checked? ${view.isSelected}", Toast.LENGTH_SHORT).show();
                }


            // 1
            val clickedCurrency = xmlElement[position]
            System.out.println("△ △ △ △ △ △ △選択通貨：$clickedCurrency")
            System.out.println("△ △ △ △ △ △ △行：      $position")
            System.out.println("△ △ △ △ △ △ △Checked： ${listView.getChildAt(position).checkedTextView.isSelected}")


            // チェックボックスのON/Offの切り替えのために設定ファイルの内容を修正する?? やる必要ない？
            // Adapterの方で設定ファイルの内容で表示処理を行う
//            when (listView.isItemChecked(position)) {
            when (listView.getChildAt(position).checkedTextView.isSelected) {

                    true -> {
                    listView.setItemChecked(position, true)
                    listView.getChildAt(position).checkedTextView.isSelected = true


                    // remove code
                    targetCodes.forEach {code ->
                        if (code == clickedCurrency.second) {
                            targetCodes.remove(code)
                        }
                    }
                }
                false -> {
                    listView.setItemChecked(position, false)
                    listView.getChildAt(position).checkedTextView.isSelected = false

                    if (!targetCodes.contains(clickedCurrency.second)) {
                        // add code
                        targetCodes.add(clickedCurrency.second)
                    }
                }
            }

            val targets = Utils.createCsvStringFromArrayList(targetCodes)
            val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sharedPref.edit()
            editor.putString(getString(R.string.targets), targets)
            editor.apply()

            System.out.println("★★ position=$position")
            System.out.println("★★ 設定ファイルの保存する文字列：=$targets")


            // setting file
            targetCodes = getTargetCodesFromSharedPreferences()
            targetPositions = Utils.getTargetPositions(xmlElement, targetCodes)


            // 再帰的に呼び出す
            adapter.notifyDataSetChanged()
//            listView.adapter = adapter

/*
            // 再帰的に呼び出す
            val adapter = TargetItemAdapter(this, xmlElement, targetPositions)
            listView.adapter = adapter
*/


        }


    }



    private fun getTargetCodesFromSharedPreferences(): ArrayList<String> {
        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val targets = sharedPref.getString(getString(R.string.targets), getString(R.string.init_targets))

        val items = targets.split(",")

        return targets.split(",") as ArrayList
    }


}
