package com.example.pokebram.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PokemonTypeDao {
    @Query("SELECT * FROM PokemonType")
    List<PokemonType> getAll();

    @Insert
    void insertAll(PokemonType... pokemonTypes);
}