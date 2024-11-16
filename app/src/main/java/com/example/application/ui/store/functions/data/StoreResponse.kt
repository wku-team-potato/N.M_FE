package com.example.application.ui.store.functions.data

import java.io.Serializable

data class StoreResponse(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val img: String,
    val created_at: String
) : Serializable
