package com.example.pokebram.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Ability {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
}
