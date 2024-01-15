package com.example.pokebram.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PokemonDao {
    @Query("SELECT * FROM Pokemon")
    List<Pokemon> getAll();

    @Query("SELECT * FROM Pokemon WHERE id = :id")
    Pokemon getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Pokemon... pokemons);
}