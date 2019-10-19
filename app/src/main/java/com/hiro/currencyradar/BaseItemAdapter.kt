package com.hiro.currencyradar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import android.util.Log


/*
 * This Adapter is for getting Base Currency
 */
class BaseItemAdapter(
    context: Context,
    private val dataSource: ArrayList<Triple<String, String, String>>,
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

    /*
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
            view = inflater.inflate(R.layout.list_currency, parent, false)

            // 3
            holder = ViewHolder()
            holder.thumbnailImageView = view.findViewById(R.id.imageView) as ImageView
            holder.codeTextView = view.findViewById(R.id.codeTextView) as TextView
            holder.countryTextView = view.findViewById(R.id.countryTextView) as TextView
            holder.checkedTextView = view.findViewById(R.id.checkedTextView) as CheckedTextView

            // 4
            view.tag = holder

            // 6
            val codeTextView = holder.codeTextView
            val countryTextView = holder.countryTextView
            val checkedTextView = holder.checkedTextView
            val thumbnailImageView = holder.thumbnailImageView


            // Get xml one row as Triple<png, code, country>
            val item = getItem(position) as Triple<String, String, String>

            codeTextView.text = item.second
            countryTextView.text = item.third

            when (initialPosition == position) {
                true -> {
                    view.isSelected = true
                    checkedTextView.isChecked = true

                    // 初期とエルスの動きを確認するためにはここの色を変えると理解しやすい
//                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_orange_24dp)
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_green_24dp)

                }
                false -> {
                    view.isSelected = false
                    checkedTextView.isChecked = false
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_unchecked_gray_24dp)
                }
            }

            val png = item.first
            Picasso.get().load("file:///android_asset/$png").into(thumbnailImageView)

        } else {
            Log.d("Adapter: getView", "エルスの方")
            // Except first call such like OnItemClickListener is supposed to through here

            view = convertView
            holder = convertView.tag as ViewHolder

            // 6
            val codeTextView = holder.codeTextView
            val countryTextView = holder.countryTextView
            val checkedTextView = holder.checkedTextView
            val thumbnailImageView = holder.thumbnailImageView

            // Get xml one row as Triple<png, code, country>
            val item = getItem(position) as Triple<String, String, String>

            codeTextView.text = item.second
            countryTextView.text = item.third

            when (initialPosition == position) {
                true -> {
                    view.isSelected = true
                    checkedTextView.isChecked = true

                    // 初期とエルスの動きを確認するためにはここの色を変えると理解しやすい
//                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_orange_24dp)
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

            val png = item.first
            Picasso.get().load("file:///android_asset/$png").into(thumbnailImageView)
        }

        return view
    }

    private class ViewHolder {
        lateinit var thumbnailImageView: ImageView
        lateinit var codeTextView: TextView
        lateinit var countryTextView: TextView
        lateinit var checkedTextView: CheckedTextView
    }
}
