package com.example.hardbrain

import android.R.color
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView


class CardAdapter(var cards: MutableList<Card>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int = cards.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.bind(card)
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFront: TextView = itemView.findViewById(R.id.tv_front)
        private val tvBack: TextView = itemView.findViewById(R.id.tv_back)
        private val cardView = itemView.findViewById<CardView>(R.id.my_card_view)
        private val viewFlipper: ViewFlipper = itemView.findViewById(R.id.view_flipper)

        init {
            itemView.setOnClickListener {
                viewFlipper.showNext()
            }
        }

        fun bind(card: Card) {
            tvFront.text = card.front
            tvBack.text = card.back
            val hexColor = "#" + Integer.toHexString(card.color)
            cardView.setCardBackgroundColor(Color.parseColor(hexColor))
        }
    }


}
