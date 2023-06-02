package com.example.hardbrain

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ShareAdapter(var collections: MutableList<Collection>) : RecyclerView.Adapter<ShareAdapter.ShareViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.share_card, parent, false)
        return ShareViewHolder(view)
    }

    override fun getItemCount(): Int = collections.size

    override fun onBindViewHolder(holder: ShareViewHolder, position: Int) {
        val collection = collections[position]

        // Настройка обработчика нажатия на CollectionView
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

        holder.addIcon.setOnClickListener {
            val addedCollection = Collection(collection.id, collection.name, collection.pressed, collection.color, collection.cards)
            holder.firebaseHelper.createCollection(addedCollection) { success ->
                if (success) {
                    Log.d("add collection", "success added")
                } else {
                    Log.d("add collection", "error")
                }
            }
        }

        val hexColor = "#" + Integer.toHexString(collection.color)
        holder.collectionView.setCardBackgroundColor(Color.parseColor(hexColor))
        holder.tvCollectionName.setBackgroundColor(Color.parseColor(hexColor))
    }

    inner class ShareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCollectionName: TextView = itemView.findViewById(R.id.collection_name)
        val addIcon: ImageView = itemView.findViewById(R.id.add_collection)
        var firebaseHelper = FirebaseHelper()
        val collectionView = itemView.findViewById<CardView>(R.id.share_card_view)

        fun bind(collection: Collection) {
            tvCollectionName.text = collection.name
        }

        init {

        }
    }



    fun getPosition(collection: Collection): Int {
        for (i in collections.indices) {
            if (collections[i] == collection) {
                return i
            }
        }
        return -1
    }

    companion object {
        const val EDIT_REQUEST_CODE = 123
    }

    fun updateCollections(newCollections: List<Collection>) {
        collections = newCollections as MutableList<Collection>
        notifyItemRangeChanged(0, collections.size)
    }

}
