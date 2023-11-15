package com.example.pokebram.api;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/*Standard PokémonDetailsResponse class with the necessary getters and setters.
* I generated this class with Github Copilot and then added the information for height and weight.*/
public class PokemonDetailsResponse implements Serializable{
    // Define fields based on the API response structure
    @SerializedName("name")
    private String name;

    @SerializedName("height")
    private int height;

    @SerializedName("weight")
    private int weight;

    // Add getters and setters as needed

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
