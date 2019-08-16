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
        } else {
            // 5
            view = convertView
            holder = convertView.tag as ViewHolder
        }

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
                System.out.println("□ ■ □ ■ □ ■ □ ■ ポジション一致")
//                checkedTextView.setChecked(true)
                holder.checkedTextView.setChecked(true)
                checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_box_orange_24dp)

            }
            false -> {
                System.out.println("▲ △ ▲ △ ▲ △ ▲ △ ポジション不致")
//                checkedTextView.setChecked(false)
                holder.checkedTextView.setChecked(false)
                checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_box_outline_blank_black_24dp)
            }
        }

        val png = item.first
        System.out.println("file:///android_asset/$png")
        Picasso.get().load("file:///android_asset/$png").into(thumbnailImageView)

        return view

    }

    private class ViewHolder {
        lateinit var thumbnailImageView: ImageView
        lateinit var codeTextView: TextView
        lateinit var countryTextView: TextView
        lateinit var checkedTextView: CheckedTextView
    }
}
