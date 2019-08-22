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
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item.view.checkedTextView
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
        navView.menu.findItem(R.id.navigation_target).setChecked(true)


        // Parse XML and retrieve xml elements into ArrayList<Triple<png, code. country>>
        val xpp : XmlResourceParser = getResources().getXml(R.xml.currencies)
        val xmlElement: ArrayList<Triple<String, String, String>> = ArrayList()
        var eventType = xpp.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType){
                XmlPullParser.START_DOCUMENT -> {
                    Log.d("TargetActivity", "Start document")
                }
                XmlPullParser.START_TAG -> {
                    Log.d("TargetActivity", "Start tag =${xpp.name}")

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
                    Log.d("TargetActivity", "★ Text=${xpp.text}")
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

            view.isSelected = true

            /*
            // I have tried below for confirm whether item was checked.
            listView.getChildAt(position).checkedTextView.isSelected
            view.checkedTextView.isChecked
            checkedTextView.isChecked
            view.checkedTextView.isChecked
*/

//            Toast.makeText(getBaseContext(), "Checked? : ${view.checkedTextView.isChecked}", Toast.LENGTH_SHORT).show();
//            when(view.checkedTextView.isChecked) {
//                true -> { //下記の３行効いてない。
//                    view.isSelected = true
////                    view.checkedTextView.isChecked = false
////                    listView.getChildAt(position).checkedTextView.isSelected = false
//                }
//                false -> {
//                    view.isSelected = true
////                    view.checkedTextView.isChecked = true
////                    listView.getChildAt(position).checkedTextView.isSelected = true
//                }
//            }


            // AdapterView is the parent class of ListView
//            Toast.makeText(getBaseContext(), "Checked? : ${view.isSelected}", Toast.LENGTH_SHORT).show();
//            Toast.makeText(getBaseContext(), "Checked? : ${listView.getChildAt(position).checkedTextView.isSelected}", Toast.LENGTH_SHORT).show();
//            Toast.makeText(getBaseContext(), "Checked? : ${checkedTextView.isChecked}", Toast.LENGTH_SHORT).show();


            // 1
            val clickedCurrency = xmlElement[position]
            System.out.println("△ △ △ △ △ △ △選択通貨：$clickedCurrency")
            System.out.println("△ △ △ △ △ △ △行：      $position")
            System.out.println("△ △ △ △ △ △ △Checked： ${listView.getChildAt(position).checkedTextView.isSelected}")


            // チェックボックスのON/Offの切り替えのために設定ファイルの内容を修正する?? やる必要ない？
            // Adapterの方で設定ファイルの内容で表示処理を行う
//            when (listView.isItemChecked(position)) {
//            when (listView.getChildAt(position).checkedTextView.isSelected) {
//
//                    true -> {
//                    listView.setItemChecked(position, true)
//                    listView.getChildAt(position).checkedTextView.isSelected = true
//
//
//                    // remove code
//                    targetCodes.forEach {code ->
//                        if (code == clickedCurrency.second) {
//                            targetCodes.remove(code)
//                        }
//                    }
//                }
//                false -> {
//                    listView.setItemChecked(position, false)
//                    listView.getChildAt(position).checkedTextView.isSelected = false
//
//                    if (!targetCodes.contains(clickedCurrency.second)) {
//                        // add code
//                        targetCodes.add(clickedCurrency.second)
//                    }
//                }
//            }

            val targets = Utils.createCsvStringFromArrayList(targetCodes)
            val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sharedPref.edit()
//            editor.putString(getString(R.string.targets), targets)
//            editor.apply()

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
