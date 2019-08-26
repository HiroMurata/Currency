package com.hiro.currencyradar

import android.content.res.XmlResourceParser
import android.provider.Settings.System.getString
import android.util.Log
import org.xmlpull.v1.XmlPullParser


object Utils {

    @JvmStatic
    /*
     *  Targetsとして選択されている通貨の位置(複数)を取得する
     */
    fun getTargetPositions(list: ArrayList<Triple<String, String, String>>, targets: List<String>) : ArrayList<Int> {

        val positionList : ArrayList<Int> = ArrayList()

        list.forEachIndexed { index, value ->

            targets.forEach { target ->
                if (value.second == target) {
                    positionList.add(index)
                }
            }
        }
        return positionList
    }

    /*
     *  Targetsとして選択された通貨コードを位置(複数)から取得する
     */
    @JvmStatic
    fun getTargetCodesFromPositions(xml: ArrayList<Triple<String, String, String>>, targets: ArrayList<Int>) : ArrayList<String> {

        val codeList : ArrayList<String> = ArrayList()

        targets.forEach { index ->
            codeList.add(xml.get(index).second)
        }

        return codeList
    }

    @JvmStatic
    fun createCsvStringFromArrayList(list: ArrayList<String>): String {
        var str = String()

        list.forEachIndexed { index, s ->
            str = when (index == list.size - 1) {
                true -> {
                    str.plus(s)
                }
                false -> {
                    str.plus("$s,")
                }
            }
        }
        return str
    }






}