package com.hiro.currencyradar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class ItemAdapter(private val context: Context,
                  private val dataSource: ArrayList<Triple<String, String, String>>) : BaseAdapter() {

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
        // Get view for row item
        val rowView = inflater.inflate(R.layout.table, parent, false)

        // for thumbnail element
        val thumbnailImageView = rowView.findViewById(R.id.image_view) as ImageView

        // for code element
        val codeTextView = rowView.findViewById(R.id.text_view_code) as TextView

        // for country name element
        val countryNameTextView = rowView.findViewById(R.id.text_view_name) as TextView



        /*
        """
        https://www.raywenderlich.com/155-android-listview-tutorial-with-kotlin
        Implement a ViewHolder Pattern のところ
        """
        */



        // Get xml one row as Triple<png, code, country>
        val item = getItem(position) as Triple<String, String, String>

        codeTextView.text = item.second
        countryNameTextView.text = item.third

        val png = item.first
        System.out.println("file:///android_asset/$png")
        Picasso.get().load("file:///android_asset/$png").into(thumbnailImageView)




        return rowView
    }

    private class ViewHolder {
        lateinit var thumbnailImageView: ImageView
        lateinit var codeTextView: TextView
        lateinit var countryTextView: TextView
        lateinit var checkBox: CheckBox
    }
}
