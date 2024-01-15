package com.example.pokebram.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Sprites {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String frontDefault;
}
