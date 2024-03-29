package com.hiro.currencyradar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.util.Log

/**
 * This Adapter is for getting Base Currency
 */
class TermItemAdapter(
    context: Context,
    private val dataSource: ArrayList<Triple<String, String, Int>>,
    private val selectedPosition: Int) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var initialPosition = selectedPosition

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * this getView method  will be called repeatedly for dataSource(rows of currency.xml)
     * refer below site.
     * https://www.raywenderlich.com/155-android-listview-tutorial-with-kotlin
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View
        val holder: ViewHolder

        // 1
        if (convertView == null) {
            Log.d("Adapter: getView", "初期の方")
            Log.d("Adapter: getView", "初期の方 selectedPosition=$selectedPosition")

            // Only first call is supposed to through here

            // 2
            view = inflater.inflate(R.layout.list_term, parent, false)

            // 3
            holder = ViewHolder()
            holder.termTextView = view.findViewById(R.id.term_TextView) as TextView
            holder.termDescriptionTextView = view.findViewById(R.id.term_description_TextView) as TextView
            holder.checkedTextView = view.findViewById(R.id.checkedTextView) as CheckedTextView

            // 4
            view.tag = holder

            // 6
            val termTextView = holder.termTextView
            val termDescriptionTextView = holder.termDescriptionTextView
            val checkedTextView = holder.checkedTextView


            // Get xml one row as Triple<png, code, country>
            val item = getItem(position) as Triple<String, String, Int>

            termTextView.text = item.second
            termDescriptionTextView.text = "Last ${item.third} days"


            when (initialPosition == position) {
                true -> {
                    view.isSelected = true
                    checkedTextView.isChecked = true
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_orange_24dp)
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_green_24dp)

                }
                false -> {
                    view.isSelected = false
                    checkedTextView.isChecked = false
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_unchecked_gray_24dp)
                }
            }


        } else {
            Log.d("Adapter: getView", "エルスの方")
            // Except first call such like OnItemClickListener is supposed to through here

            view = convertView
            holder = convertView.tag as ViewHolder

            // 6
            val termDescriptionTextView = holder.termDescriptionTextView
            val termTextView = holder.termTextView
            val checkedTextView = holder.checkedTextView

            // Get xml one row as Triple<term_id, term, days>
            val item = getItem(position) as Triple<String, String, Int>

            termTextView.text = item.second
            termDescriptionTextView.text = "Last ${item.third} days"


            when (initialPosition == position) {
                true -> {
                    view.isSelected = true
                    checkedTextView.isChecked = true
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_orange_24dp)
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_green_24dp)

                }
                false -> {
                    view.isSelected = false
                    checkedTextView.isChecked = false
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_unchecked_gray_24dp)
                }
            }


            // "view.isSelected" is the row which is selected on ListView
            when (view.isSelected) {
                true -> {

                    Log.d("Adapter: getView", "エルスの方  ★★★★★★★★view.id=${view.id}")
                    checkedTextView.isChecked = true     // either is okay
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_green_24dp)
//                    view.checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_green_24dp)
                }
                false -> {
                    checkedTextView.isChecked = false    // either is okay
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_unchecked_gray_24dp)
                }
            }
            Log.d("Adapter: getView", "エルスの方 selectedPosition=$selectedPosition")

        }

        return view
    }

    private class ViewHolder {
        lateinit var termTextView: TextView
        lateinit var termDescriptionTextView: TextView
        lateinit var checkedTextView: CheckedTextView
    }
}
