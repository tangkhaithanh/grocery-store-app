package com.store.grocery_store_app.data.models.response

class ReviewStatsResponse (
    val averageRating: Double,
    val totalReviews: Int,
    val ratingDistribution: Map<String, Int>
)