package com.hiro.currencyradar


object Utils {

    @JvmStatic
    /*
     *  Targetsとして選択されている通貨の位置(複数)を取得する
     */
    fun getTargetPositions(list: ArrayList<Triple<String, String, String>>, targets: List<String>) : ArrayList<Int> {

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