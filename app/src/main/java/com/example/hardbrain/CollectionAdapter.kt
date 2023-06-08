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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates


class CollectionAdapter(var collections: MutableList<Collection>) : RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    val selectedCollections = mutableListOf<Collection>()
    private var longClickListener: ((Collection, CollectionAdapter.CollectionViewHolder) -> Unit)? = null

    fun setOnLongClickListener(listener: (Collection, CollectionViewHolder) -> Unit) {
        longClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_card, parent, false)
        return CollectionViewHolder(view)
    }

    override fun getItemCount(): Int = collections.size

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = collections[position]
        var isPressed = collection.pressed
        if (isPressed){
            holder.btnPressed.setImageResource(R.drawable.ic_pressed)
        }
        else{
            holder.btnPressed.setImageResource(R.drawable.ic_unpressed)
        }
        holder.bind(collection)

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

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val collectionbox = collections[position]
            if (isChecked) {
                selectedCollections.add(collectionbox)
            } else {
                selectedCollections.remove(collectionbox)
            }
        }

        holder.editIcon.setOnClickListener {
            val intent = Intent(holder.itemView.context.applicationContext, EditCollectionActivity::class.java)
            intent.putExtra("collection_id", collections[position].id)
            intent.putExtra("isNewCollection", false) // передача флага
            intent.putExtra("position", position)
            (holder.itemView.context as Activity).startActivityForResult(intent,EDIT_REQUEST_CODE)
        }

        holder.shareIcon.setOnClickListener {
            val sharedCollection = Collection(collection.id, collection.name, isPressed, collection.color, collection.cards)
            holder.firebaseHelper.getShareCollectionsIds { sharedCollectionsIds ->
                if (!sharedCollectionsIds.contains(sharedCollection.id)) {
                    holder.firebaseHelper.shareCollection(sharedCollection) { success ->
                        if (success) {
                            Log.d("share collection", "success share")
                            Log.d("sharedCollection.id", sharedCollection.id.toString())
                        } else {
                            Log.d("share collection", "error")
                        }
                    }
                }
            }
        }

        holder.btnPressed.setOnClickListener{
            if (isPressed) {
                holder.btnPressed.setImageResource(R.drawable.ic_unpressed)
            } else {
                holder.btnPressed.setImageResource(R.drawable.ic_pressed)
            }
            isPressed = !isPressed
            val editCollection = Collection(collection.id, collection.name, isPressed, collection.color, collection.cards)
            holder.firebaseHelper.updateCollection(editCollection){success ->
                if (success) {
                    notifyItemChanged(position)
                } else {
                    Log.d("edit isPressed", "Что-то пошло не так!!!")
                }
            }
        }

        val hexColor = "#" + Integer.toHexString(collection.color)
        holder.collectionView.setCardBackgroundColor(Color.parseColor(hexColor))
        holder.tvCollectionName.setBackgroundColor(Color.parseColor(hexColor))
        holder.btnPressed.setBackgroundColor(Color.parseColor(hexColor))
    }

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCollectionName: TextView = itemView.findViewById(R.id.collection_name)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        val editIcon: ImageView = itemView.findViewById(R.id.edit_icon)
        val shareIcon: ImageView = itemView.findViewById(R.id.share_icon)
        val btnPressed: ImageButton = itemView.findViewById((R.id.show_collection_cards))
        var firebaseHelper = FirebaseHelper()
        val collectionView = itemView.findViewById<CardView>(R.id.my_card_view)

        fun bind(collection: Collection) {
            tvCollectionName.text = collection.name
        }

        init {
            itemView.setOnLongClickListener {
                longClickListener?.invoke(collections[adapterPosition], this)
                true
            }
        }
    }

    fun showCheckBoxes(recyclerView: RecyclerView) {
        for (i in collections.indices) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as? CollectionAdapter.CollectionViewHolder
            viewHolder?.checkBox?.visibility = View.VISIBLE
        }
    }

    fun hideCheckBoxes(recyclerView: RecyclerView) {
        for (i in collections.indices) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as? CollectionAdapter.CollectionViewHolder
            viewHolder?.checkBox?.visibility = View.INVISIBLE
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
