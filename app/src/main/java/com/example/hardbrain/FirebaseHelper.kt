package com.example.hardbrain

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.Serializable

data class Card(
    var id: String? = null,
    var front: String = "",
    var back: String = "",
    var color: Int = R.color.white // цвет по умолчанию
): Serializable

class FirebaseHelper {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userId: String? = auth.currentUser?.uid
    private val cardsRef: DatabaseReference = database.getReference("users/$userId/cards")

    fun addCard(card: Card, callback: (Boolean) -> Unit) {
        cardsRef.push().setValue(card)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error adding card", exception)
                callback(false) }
    }

    fun updateCard(card: Card, callback: (Boolean) -> Unit) {
        card.id?.let {
            cardsRef.child(it).setValue(card)
                .addOnSuccessListener { callback(true) }
                .addOnFailureListener { callback(false) }
        }
    }

    fun deleteCard(cardId: String, callback: (Boolean) -> Unit) {
        cardsRef.child(cardId).removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getAllCards(callback: (List<Card>) -> Unit) {
        cardsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cards = mutableListOf<Card>()
                snapshot.children.forEach { child ->
                    val card = child.getValue(Card::class.java)
                    card?.id = child.key
                    card?.let { cards.add(it) }
                }
                callback(cards)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
