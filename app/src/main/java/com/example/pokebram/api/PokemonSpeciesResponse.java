package com.example.pokebram.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// PokemonSpeciesResponse.java
public class PokemonSpeciesResponse {
    @SerializedName("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries;

    public List<FlavorTextEntry> getFlavorTextEntries() {
        return flavorTextEntries;
    }

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