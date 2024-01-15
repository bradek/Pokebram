package com.example.pokebram.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Pokemon.class, PokemonType.class, Sprites.class, Stat.class, Ability.class, PokemonSpecies.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class PokemonDatabase extends RoomDatabase {
    public abstract PokemonDao pokemonDao();
    public abstract PokemonTypeDao pokemonTypeDao();
    public abstract SpritesDao spritesDao();
    public abstract StatDao statDao();
    public abstract AbilityDao abilityDao();
    public abstract PokemonSpeciesDao pokemonSpeciesDao();
}