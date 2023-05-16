package com.example.hardbrain

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
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


class CardAdapter(var cards: MutableList<Card>, private val collectionId: String?) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    val selectedCards = mutableListOf<Card>()
    private var longClickListener: ((Card, CardViewHolder) -> Unit)? = null

    fun setOnLongClickListener(listener: (Card, CardViewHolder) -> Unit) {
        longClickListener = listener
    }

       override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        val actiview = LayoutInflater.from(parent.context).inflate(R.layout.activity_card, parent, false)



        return CardViewHolder(view,actiview)
    }

    override fun getItemCount(): Int = cards.size
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.tvFront.text = card.front
        holder.tvBack.text = card.back
        // Изначально обратная сторона скрыта
        holder.tvBack.visibility = View.GONE
        holder.divider.visibility = View.GONE

        // Обработчик нажатия на карточку
        holder.cardView.setOnClickListener {
            // Проверяем, открыта ли уже карточка
            val isOpen = holder.isCardOpen

            // Изменяем состояние карточки (открыта/закрыта)
            holder.isCardOpen = !isOpen

            if (isOpen) {
                // Показываем обратную сторону
                holder.viewFlipper.showNext()
                holder.cardView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                // Скрываем обратную сторону
                holder.viewFlipper.showPrevious()
                holder.cardView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }

            holder.cardView.requestLayout() // Обновляем макет, чтобы изменения высоты применились

            // Обновляем видимость обратной стороны карточки
            holder.tvBack.visibility = if (!isOpen) {
                View.VISIBLE // Открытое состояние: показываем обратную сторону
            } else {
                View.GONE // Закрытое состояние: скрываем обратную сторону
            }
            holder.divider.visibility = if (!isOpen) {
                View.VISIBLE // Открытое состояние: показываем обратную сторону
            } else {
                View.GONE // Закрытое состояние: скрываем обратную сторону
            }
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val cardbox = cards[position]
            if (isChecked) {
                selectedCards.add(cardbox)
            } else {
                selectedCards.remove(cardbox)
            }
        }

        holder.editIcon.setOnClickListener {
            val intent = Intent(holder.itemView.context.applicationContext, EditCardActivity::class.java)
            intent.putExtra("card_id", cards[position].id)
            intent.putExtra("isNewCard", false) // передача флага
            intent.putExtra("position", position)
            intent.putExtra("collectionId", collectionId)
            (holder.itemView.context as Activity).startActivityForResult(intent, EDIT_REQUEST_CODE)
        }

        val hexColor = "#" + Integer.toHexString(card.color)
        holder.cardView.setCardBackgroundColor(Color.parseColor(hexColor))
        holder.tvBack.setBackgroundColor(Color.parseColor(hexColor))
        holder.tvFront.setBackgroundColor(Color.parseColor(hexColor))
    }



    inner class CardViewHolder(itemView: View, actiview: View) : RecyclerView.ViewHolder(itemView) {
        val tvFront: TextView = itemView.findViewById(R.id.tv_front)
        val tvBack: TextView = itemView.findViewById(R.id.tv_back)
        val cardView = itemView.findViewById<CardView>(R.id.my_card_view)
        val viewFlipper: ViewFlipper = itemView.findViewById(R.id.view_flipper)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        val editIcon: ImageView = itemView.findViewById(R.id.edit_icon)
        var isCardOpen: Boolean = false
        val divider: View = itemView.findViewById(R.id.divider)

        init {
            itemView.setOnLongClickListener {
                longClickListener?.invoke(cards[adapterPosition], this)
                true
            }
        }
    }


    fun showCheckBoxes(recyclerView: RecyclerView) {
        for (i in cards.indices) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as? CardViewHolder
            viewHolder?.checkBox?.visibility = View.VISIBLE
        }
    }

    fun hideCheckBoxes(recyclerView: RecyclerView) {
        for (i in cards.indices) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as? CardViewHolder
            viewHolder?.checkBox?.visibility = View.INVISIBLE
        }
    }

    fun getPosition(card: Card): Int {
        for (i in cards.indices) {
            if (cards[i] == card) {
                return i
            }
        }
        return -1
    }


    companion object {
        const val EDIT_REQUEST_CODE = 123
    }




}
