package com.example.pokebram.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Stat {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int baseStat;
    public String statName;
}
