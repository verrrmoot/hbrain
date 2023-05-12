package com.example.hardbrain

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates


class CardAdapter(var cards: MutableList<Card>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    val selectedCards = mutableListOf<Card>()

       override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        val actiview = LayoutInflater.from(parent.context).inflate(R.layout.activity_card, parent, false)



        return CardViewHolder(view,actiview)
    }

    override fun getItemCount(): Int = cards.size
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.viewFlipper.displayedChild = 0
        holder.tvFront.text = card.front
        holder.tvBack.text = card.back

        val frontHeight = getTextHeight(holder.tvFront) * 5
        val backHeight = getTextHeight(holder.tvBack) * 5

        holder.viewFlipper.layoutParams.height = frontHeight

        holder.cardView.setOnClickListener {
            val newHeight = if (holder.viewFlipper.displayedChild == 0) {
                backHeight
            } else {
                frontHeight
            }

            animateHeight(holder.viewFlipper, newHeight)
            holder.cardView.layoutParams.height = newHeight
            holder.viewFlipper.showNext()
        }

        holder.itemView.setOnLongClickListener { view ->
            holder.checkBox.visibility = View.VISIBLE
            if (selectedCards.contains(card)) {
                selectedCards.remove(card)
                holder.checkBox.isChecked = false
            } else {
                selectedCards.add(card)
                holder.checkBox.isChecked = true
            }
            true
        }

        holder.checkBox.isChecked = selectedCards.contains(card)
        holder.checkBox.visibility = if (selectedCards.isEmpty()) View.GONE else View.VISIBLE



        val hexColor = "#" + Integer.toHexString(card.color)
        holder.cardView.setCardBackgroundColor(Color.parseColor(hexColor))
        holder.tvBack.setBackgroundColor(Color.parseColor(hexColor))
        holder.tvFront.setBackgroundColor(Color.parseColor(hexColor))
    }

    fun animateHeight(view: View, targetHeight: Int) {
        val animator = ValueAnimator.ofInt(view.height, targetHeight)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        // Запускаем анимацию
        animator.duration = 500 // длительность анимации 500 миллисекунд
        animator.start()
    }

    fun getTextHeight(textView: TextView): Int {
        val paint = Paint()
        val rect = Rect()

        paint.typeface = Typeface.SANS_SERIF // установите шрифт, который используется в TextView
        paint.textSize = textView.textSize // установите размер шрифта, который используется в TextView

        paint.getTextBounds(textView.text.toString(), 0, textView.text.length, rect)
        return (rect.height())
    }

    inner class CardViewHolder(itemView: View, actiview: View) : RecyclerView.ViewHolder(itemView) {
        val tvFront: TextView = itemView.findViewById(R.id.tv_front)
        val tvBack: TextView = itemView.findViewById(R.id.tv_back)
        val cardView = itemView.findViewById<CardView>(R.id.my_card_view)
        val viewFlipper: ViewFlipper = itemView.findViewById(R.id.view_flipper)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)



               /*
                init {
                    // Устанавливаем стандартные анимации для переворота
                    viewFlipper.setInAnimation(itemView.context, android.R.anim.fade_in)
                    viewFlipper.setOutAnimation(itemView.context, android.R.anim.fade_out)
                    viewFlipper.displayedChild = 0
                    itemView.setOnClickListener {
                        viewFlipper.showNext()
                    }
                }

                fun bind(card: Card) {
                    tvFront.text = card.front
                    tvBack.text = card.back
                    val hexColor = "#" + Integer.toHexString(card.color)
                    cardView.setCardBackgroundColor(Color.parseColor(hexColor))
                    tvBack.setBackgroundColor(Color.parseColor(hexColor))
                    tvFront.setBackgroundColor(Color.parseColor(hexColor))

                    // Устанавливаем высоту CardView на основе максимальной высоты между tvFront и tvBack
                    val textViewHeight = tvFront.height
                    val backTextViewHeight = tvBack.height
                    val maxHeight = maxOf(textViewHeight, backTextViewHeight)
                    cardView.layoutParams.height = maxHeight
                    viewFlipper.layoutParams.height = maxHeight
                }*/
    }

    private fun resizeCardView(cardView: CardView, height: Int) {
        val layoutParams = cardView.layoutParams
        layoutParams.height = height
        cardView.layoutParams = layoutParams
    }



}
