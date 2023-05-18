package com.example.hardbrain

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.io.Serializable

data class Collection(
    var id: String? = null,
    var name: String = "",
    var cards: HashMap<String, Card> = hashMapOf()
) : Serializable {
    // Конструктор без аргументов
    constructor() : this(null, "", hashMapOf())
}

data class Card(
    var id: String? = null,
    var front: String = "",
    var back: String = "",
    var date: String? = "",
    var collectionId: String? = "",
    var interval: Int = 1, //по умолчанию 0 дней
    var factor: Double = 1.0, //изначально 1
    var color: Int = -1 // цвет по умолчанию white
): Serializable

class FirebaseHelper {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userId: String? = auth.currentUser?.uid
    private val cardsRef: DatabaseReference = database.getReference("users/$userId/cards")
    private val collectionsRef: DatabaseReference = database.getReference("users/$userId/collections")

    fun createCollection(collectionName: String, callback: (Boolean) -> Unit) {
        val collectionId = collectionsRef.push().key

        val collection = Collection(collectionId!!, collectionName, hashMapOf())

        collectionsRef.child(collectionId).setValue(collection)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error creating collection", exception)
                callback(false)
            }
    }
    fun getAllCollections(callback: (MutableList<Collection>) -> Unit) {
        collectionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val collections = mutableListOf<Collection>()
                snapshot.children.forEach { child ->
                    val collection = child.getValue(Collection::class.java)
                    collection?.id = child.key
                    collection?.let { collections.add(it) }
                }
                callback(collections)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
            }
        })
    }

    fun getAllCollectionCards(callback: (List<Card>) -> Unit) {
        collectionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val allCards = mutableListOf<Card>()

                for (collectionSnapshot in dataSnapshot.children) {
                    val cardsSnapshot = collectionSnapshot.child("cards")

                    for (cardSnapshot in cardsSnapshot.children) {
                        val cardKey = cardSnapshot.key
                        val front = cardSnapshot.child("front").getValue(String::class.java)
                        val back = cardSnapshot.child("back").getValue(String::class.java)
                        val date = cardSnapshot.child("date").getValue(String::class.java)
                        val collectionId = cardSnapshot.child("collectionId").getValue(String::class.java)

                        val card = front?.let { back?.let { it1 ->
                            Card(cardKey, it,
                                it1, date, collectionId)
                        } }
                        card?.let { allCards.add(it) }
                    }
                }

                callback(allCards)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибки получения данных
            }
        })
    }


    fun addCard(card: Card, collectionId: String, callback: (Boolean) -> Unit) {
        getCardsRefByCollectionId(collectionId).push().setValue(card)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error adding card", exception)
                callback(false) }
    }
    fun updateCard(card: Card, collectionId: String, callback: (Boolean) -> Unit) {
        card.id?.let {
            getCardsRefByCollectionId(collectionId).child(it).setValue(card)
                .addOnSuccessListener { callback(true) }
                .addOnFailureListener { callback(false) }
        }
    }
    fun deleteCard(cardId: String, collectionId: String, callback: (Boolean) -> Unit) {
        getCardsRefByCollectionId(collectionId).child(cardId).removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
    fun getAllCards(collectionId: String, callback: (MutableList<Card>) -> Unit) {
        getCardsRefByCollectionId(collectionId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cards = mutableListOf<Card>()
                snapshot.children.forEach { child ->
                    val card = child.getValue(Card::class.java)
                    card?.id = child.key
                    card?.let { cards.add(it) }
                }
                callback(cards)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadCards:onCancelled", error.toException())
                Log.d("FirebaseHelper", "cards: users/$userId/cards")

            }
        })
    }
    fun getCardById(cardId: String, collectionId: String, callback: (Card?) -> Unit) {
        getCardsRefByCollectionId(collectionId).child(cardId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val card = snapshot.getValue(Card::class.java)
                callback(card)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadCard:onCancelled", error.toException())
                Log.d("FirebaseHelper", "cards: users/$userId/cards")
            }
        })
    }

    fun getCardsRefByCollectionId(collectionId: String): DatabaseReference {
        return database.getReference("users/$userId/collections/$collectionId/cards")
    }
}
