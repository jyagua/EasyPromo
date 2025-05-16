package com.ufc.planetapp.models;

import com.ufc.planetapp.R

data class Planet (
    val id: Int,
    val name: String,
    val type: String,
    val galaxy: String,
    val distanceFromSun: String,
    val diameter: String,
    val characteristics: String,
    val imageRes: Int,
    var isFavorite: Boolean = false
)

val planetList = listOf(
    Planet(
        id = 1,
        name = "Saturno",
        type = "Gasoso",
        galaxy = "Via Láctea",
        distanceFromSun = "Muito Longe",
        diameter = "3 corsas",
        characteristics = "Aneis",
        imageRes = R.drawable.ic_launcher_background
    )
)