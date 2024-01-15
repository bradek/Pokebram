package com.example.pokebram.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PokemonType {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
}
