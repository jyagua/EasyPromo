package com.ufc.easypromo.auth

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.ufc.easypromo.models.Product
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = Firebase.firestore
    private val authRepo = FirebaseAuthRepository()

    private fun userCollection(collection: String) =
        db.collection("users")
            .document(authRepo.getCurrentUserId() ?: "unknown")
            .collection(collection)

    suspend fun saveFavourite(product: Product) {
        userCollection("favourites").document(product.id.toString()).set(product).await()
    }

    suspend fun saveToCart(product: Product) {
        userCollection("cart").document(product.id.toString()).set(product).await()
    }

    suspend fun removeFavourite(productId: String) {
        userCollection("favourites").document(productId).delete().await()
    }

    suspend fun removeFromCart(productId: String) {
        userCollection("cart").document(productId).delete().await()
    }

    suspend fun getFavourites(): List<Product> {
        return userCollection("favourites").get().await().toObjects(Product::class.java)
    }

    suspend fun getCart(): List<Product> {
        return userCollection("cart").get().await().toObjects(Product::class.java)
    }
}
