package com.hiro.currencyradar

//import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.XmlResourceParser
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AlertDialog;
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.view.Gravity
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_select_target.*
import kotlinx.android.synthetic.main.list_item.view.checkedTextView
import org.xmlpull.v1.XmlPullParser
import android.R.attr.gravity
import android.widget.LinearLayout
import android.R.drawable
import android.app.PendingIntent.getActivity
import android.widget.TextView
import android.util.SparseBooleanArray
import android.widget.Toast




class SelectTargetActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    var changedTargetPositions : ArrayList<Int> = ArrayList()


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

        button.setOnClickListener {
            Log.d("TargetActivity:onCreate", "Save Button Clicked!")

//            Toast.makeText(this, "選択行：$changedTargetPositions", Toast.LENGTH_LONG).show();

//            Log.d("TargetActivity", "★ listView.checkedItemCount. = ${listView.checkedItemCount}")
            Log.d("TargetActivity", "★ listView.checkedItemCount. = ${listView.getCheckedItemPositions().size() }")

            val checkedItemPositions = listView.checkedItemPositions
            val checkedItemCount = checkedItemPositions.size()

            Log.d("TargetActivity", "★ listView checkedItemCount = $checkedItemCount")
//            Log.d("TargetActivity", "★ adapter ★checkedCount = ${listView.adapter.checkedCount}")

            Log.d("TargetActivity", "★ カウント ： ${changedTargetPositions.size}")
            when {
                changedTargetPositions.size == 0|| changedTargetPositions.size == 1 || changedTargetPositions.size == 2 -> {
                    // build alert dialog
                    val alertDialog = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)

                    // ダイアログの設定
                    alertDialog.setIcon(R.drawable.ic_alart_orange_24dp)   //アイコン設定
                    alertDialog.setTitle(getString(R.string.dialog_title))      //タイトル設定
                    alertDialog.setMessage(getString(R.string.dialog_message_at_least))  //内容(メッセージ)設定

                    // OK(肯定的な)ボタンの設定
                    alertDialog.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        // OKボタン押下時の処理
                        Log.d("AlertDialog", "Positive which :$which")


                    })

                    // ダイアログの作成と描画
                    // alertDialog.create();
                    alertDialog.show() // .show() including .create()
                }
                changedTargetPositions.size >= 7 -> {
                    // build alert dialog
                    val alertDialog = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)

                    // ダイアログの設定
                    alertDialog.setIcon(R.drawable.ic_alart_orange_24dp)   //アイコン設定
                    alertDialog.setTitle(getString(R.string.dialog_title))      //タイトル設定
                    alertDialog.setMessage(getString(R.string.dialog_message_over))  //内容(メッセージ)設定

                    // OK(肯定的な)ボタンの設定
                    alertDialog.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        // OKボタン押下時の処理
                        Log.d("AlertDialog", "Positive which :$which")
                    })

                    // ダイアログの作成と描画
                    // alertDialog.create();
                    alertDialog.show() // .show() including .create()

                }


            }
        }

        // BottomNavigationView setting
        val navView: BottomNavigationView = findViewById(R.id.nav_view_main)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.menu.findItem(R.id.navigation_target).isChecked = true


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

        val adapter = TargetItemAdapter(this,  xmlElement, targetPositions)
        listView.adapter = adapter

        // keep targetPosition upon onCreate initially called
        changedTargetPositions = adapter.changedTargetPositions


        listView.setOnItemClickListener { _, view, position, _ ->

            // 1
            val clickedCurrency = xmlElement[position]
            Log.d("TargetActivity", "★ clickedCurrency = $clickedCurrency")
            Log.d("TargetActivity", "★ clicked row = $position")

            val targets = Utils.createCsvStringFromArrayList(targetCodes)
            val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sharedPref.edit()
//            editor.putString(getString(R.string.targets), targets)
//            editor.apply()

            // TODO Saveボタンで設定ファイルに書き込み
            System.out.println("★★ position=$position")
            System.out.println("★★ 設定ファイルの保存する文字列：=$targets")

            // setting file
            targetCodes = getTargetCodesFromSharedPreferences()
            targetPositions = Utils.getTargetPositions(xmlElement, targetCodes)

            // pass selected position to adapter
            adapter.selectedPosition = position

            // reflect changes recursively
            adapter.notifyDataSetChanged()

            // update targetPosition when an Item Clicked
            changedTargetPositions = adapter.changedTargetPositions
        }

    }



    private fun getTargetCodesFromSharedPreferences(): ArrayList<String> {
        // setting file
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val targets = sharedPref.getString(getString(R.string.targets), getString(R.string.init_targets))

//        val items = targets.split(",")

        return targets.split(",") as ArrayList
    }


}
