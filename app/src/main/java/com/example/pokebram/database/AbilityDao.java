package com.example.pokebram.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AbilityDao {
    @Query("SELECT * FROM Ability")
    List<Ability> getAll();

    @Insert
    void insertAll(Ability... abilities);
}