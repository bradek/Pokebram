package com.example.pokebram.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PokemonSpecies {
    @PrimaryKey(autoGenerate = true)
    public int id;
    // Add fields for the data you want to store for PokemonSpecies
}
