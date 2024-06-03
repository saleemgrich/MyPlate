package com.example.buggy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.buggy.R
import com.example.buggy.data.model.FoodItem

class CustomAdapter(context: Context, private val items: List<FoodItem>) :
    ArrayAdapter<FoodItem>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_food, parent, false)
        }

        val currentItem = items[position]

        val imageView: ImageView = itemView!!.findViewById(R.id.imageView)
        imageView.setImageResource(currentItem.imageResId)

        val textView: TextView = itemView.findViewById(R.id.textView)
        textView.text = currentItem.name

        return itemView
    }
}