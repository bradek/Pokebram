package com.example.pokebram;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokebram.R;
import com.example.pokebram.api.PokemonDetailsResponse;

public class PokemonDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_details);

        // Retrieve Pokemon details from Intent extras
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pokemonDetails")) {
            PokemonDetailsResponse detailsResponse = (PokemonDetailsResponse) intent.getSerializableExtra("pokemonDetails");

            // Display the details
            TextView textViewPokemonName = findViewById(R.id.textViewPokemonName);
            TextView textViewHeight = findViewById(R.id.textViewHeight);
            TextView textViewWeight = findViewById(R.id.textViewWeight);

            textViewPokemonName.setText("Name: " + detailsResponse.getName());
            textViewHeight.setText(getString(R.string.height_placeholder, "1m")); // Replace with actual height
            textViewWeight.setText(getString(R.string.weight_placeholder, "10kg")); // Replace with actual weight
        }
    }
}
