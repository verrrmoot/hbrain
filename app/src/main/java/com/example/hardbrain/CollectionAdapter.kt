package com.example.hardbrain

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates


class CollectionAdapter(var collections: MutableList<Collection>) : RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_card, parent, false)
        return CollectionViewHolder(view)
    }

    override fun getItemCount(): Int = collections.size

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = collections[position]
        holder.bind(collection)

        // Настройка обработчика нажатия на CardView
        holder.itemView.setOnClickListener {
            // Создание Intent для перехода на CardActivity
            val intent = Intent(holder.itemView.context, CardActivity::class.java)
            // Передача данных о выбранной коллекции в CardActivity
            intent.putExtra("collectionId", collection.id)
            collection.id?.let { it1 -> Log.d("collectionIDDD", it1) }
            intent.putExtra("collectionName", collection.name)

            // Запуск CardActivity
            holder.itemView.context.startActivity(intent)
        }
    }

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCollectionName: TextView = itemView.findViewById(R.id.collection_name)

        fun bind(collection: Collection) {
            tvCollectionName.text = collection.name

        }
    }

    fun updateCollections(newCollections: List<Collection>) {
        collections = newCollections as MutableList<Collection>
        notifyItemRangeChanged(0, collections.size)
    }

}
