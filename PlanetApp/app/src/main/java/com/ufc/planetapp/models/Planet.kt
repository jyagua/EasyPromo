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
        name = "Mercúrio",
        type = "Rochoso",
        galaxy = "Via Láctea",
        distanceFromSun = "Muito Longe",
        diameter = "1 Fusca",
        characteristics = "Mais perto do Sol",
        imageRes = R.drawable.mercury
    ),
    Planet(
        id = 2,
        name = "Vênus",
        type = "Rochoso",
        galaxy = "Via Láctea",
        distanceFromSun = "Muito Longe",
        diameter = "2 Fuscas",
        characteristics = "Planeta mais quente do Sistema Solar",
        imageRes = R.drawable.venus
    ),
    Planet(
        id = 3,
        name = "Terra",
        type = "Rochoso",
        galaxy = "Via Láctea",
        distanceFromSun = "Muito Longe",
        diameter = "1 Celta",
        characteristics = "Planeta habitado por seres vivos",
        imageRes = R.drawable.earth
    ),
    Planet(
        id = 4,
        name = "Marte",
        type = "Rochoso",
        galaxy = "Via Láctea",
        distanceFromSun = "Muito Longe",
        diameter = "1/3 de Celta",
        characteristics = "Planeta vermelho parecido com a Terra",
        imageRes = R.drawable.mars
    ),
    Planet(
        id = 5,
        name = "Júpiter",
        type = "Gasoso",
        galaxy = "Via Láctea",
        distanceFromSun = "Muito Longe",
        diameter = "3 Ônibus",
        characteristics = "Maior planeta do sistema solar",
        imageRes = R.drawable.jupiter
    ),
    Planet(
        id = 6,
        name = "Saturno",
        type = "Gasoso",
        galaxy = "Via Láctea",
        distanceFromSun = "Muito Longe",
        diameter = "2 Ônibus",
        characteristics = "Aneis",
        imageRes = R.drawable.saturn
    ),
    Planet(
        id = 7,
        name = "Urano",
        type = "Gasoso",
        galaxy = "Via Láctea",
        distanceFromSun = "Muito Longe",
        diameter = "1 Ônibus",
        characteristics = "Azul e gelado",
        imageRes = R.drawable.uranus
    ),
    Planet(
        id = 8,
        name = "Netuno",
        type = "Gasoso",
        galaxy = "Via Láctea",
        distanceFromSun = "Muito Longe",
        diameter = "1 Ônibus",
        characteristics = "Bem gelado",
        imageRes = R.drawable.neptune
    )


)