package com.example.pokebram.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Pokemon {
    @PrimaryKey
    public int id;
    public String name;
    public int height;
    public int weight;
    // Foreign keys to other tables
    public int spritesId;
    public int typeId;
    public int statId;
    public int abilityId;
    public int speciesId;

    // Add the abilities attribute
    @ColumnInfo(name = "abilities")
    public List<String> abilities;
}