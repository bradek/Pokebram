package com.example.pokebram.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StatDao {
    @Query("SELECT * FROM Stat")
    List<Stat> getAll();

    @Insert
    void insertAll(Stat... stats);
}