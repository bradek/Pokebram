package com.example.pokebram.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*In PokemonSpeciesResponse, I do the API calls for the flavor text.*/
public class PokemonSpeciesResponse {
    @SerializedName("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries;

    /*The Flavor texts are returned as a list in the API.*/
    public List<FlavorTextEntry> getFlavorTextEntries() {
        return flavorTextEntries;
    }

    /*I make a nested class for the flavor text entries.*/
    public static class FlavorTextEntry {
        @SerializedName("flavor_text")
        private String flavorText;

        @SerializedName("language")
        private Language language;

        public String getFlavorText() {
            return flavorText;
        }

        public Language getLanguage() {
            return language;
        }

        public static class Language {
            @SerializedName("name")
            private String name;

            public String getName() {
                return name;
            }
        }
    }
}