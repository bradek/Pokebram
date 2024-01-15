package com.example.pokebram.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SpritesDao {
    @Query("SELECT * FROM Sprites")
    List<Sprites> getAll();

    @Insert
    void insertAll(Sprites... sprites);
}