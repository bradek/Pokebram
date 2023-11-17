package com.example.pokebram.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/*This is the PokemonListResponse class.
* I generated this class with Github Copilot.*/
public class PokemonListResponse {
    @SerializedName("results")
    private List<PokemonListItem> results;
    private String next;

    public List<PokemonListItem> getResults() {
        return results;
    }

    public String getNext() {
        return next;
    }

    public static class PokemonListItem {
        @SerializedName("name")
        private String name;

        @SerializedName("url")
        private String url;

        @SerializedName("pokedexNumber") // replace "pokedexNumber" with the correct key from the API response
        private int pokedexNumber;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public int getPokedexNumber() {
            return pokedexNumber;
        }
    }
}