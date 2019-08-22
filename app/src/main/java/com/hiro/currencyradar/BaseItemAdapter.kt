package com.hiro.currencyradar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso

/*
 * This Adapter is for getting Base Currency
 */
class BaseItemAdapter(private val context: Context,
                      private val dataSource: ArrayList<Triple<String, String, String>>,
                      private val selectedPosition: Int) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View
        val holder: ViewHolder

        // 1
        if (convertView == null) {
            // First call is supposed to through here

            // 2
            view = inflater.inflate(R.layout.list_item, parent, false)

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

            when (selectedPosition == position) {
                true -> {
                    checkedTextView.isChecked = true
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_orange_24dp)
                }
                false -> {
                    checkedTextView.isChecked = false
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_unchecked_gray_24dp)
                }
            }

            val png = item.first
            Picasso.get().load("file:///android_asset/$png").into(thumbnailImageView)


        } else {
            // Call upon OnItemClickListener is supposed to through here

            view = convertView
            holder = convertView.tag as ViewHolder

            // "view.isSelected" is the row which is selected on ListView
            when (view.isSelected) {
                true -> {
                    holder.checkedTextView.isChecked = true     // either is okay
                    holder.checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_orange_24dp)
                }
                false -> {
                    holder.checkedTextView.isChecked = false    // either is okay
                    holder.checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_unchecked_gray_24dp)
                }
            }
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
