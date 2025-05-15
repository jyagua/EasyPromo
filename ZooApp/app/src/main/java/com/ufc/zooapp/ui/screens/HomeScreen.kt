package com.ufc.zooapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ufc.zooapp.models.Animal
import com.ufc.zooapp.models.animalList
import com.ufc.zooapp.ui.components.AnimalListItem
import java.nio.file.WatchEvent
import androidx.compose.foundation.lazy.items


@Composable
fun HomeScreen(
    onAnimalSelected: (Animal) -> Unit
) {
    var searchQuery by remember{
        mutableStateOf("")
    }

    val filteredAnimals = remember(searchQuery) {
        animalList.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Column { TextField(
        value = searchQuery,
        onValueChange = { searchQuery = it},
        label = { Text(text = "Pesquisar") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        )

        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ){
            items (filteredAnimals){ animal ->
                AnimalListItem(animal, onAnimalSelected)

            }
        }

    }
}