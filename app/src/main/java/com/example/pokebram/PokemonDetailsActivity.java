package com.example.pokebram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.pokebram.R;
import com.example.pokebram.api.PokemonDetailsResponse;

import java.util.List;
import java.util.ArrayList;

public class PokemonDetailsActivity extends AppCompatActivity {

    /*All variables I need are declared here.
    * I use them for the views (generally TextViews) I did put on the activity_pokemon_details.xml.*/
    private TextView textViewPokemonId;
    private TextView textViewPokemonTypes;
    private TextView textViewPokemonName;
    private ImageView imageViewPokemon;
    private TextView textViewHeight;
    private TextView textViewWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*I set the ContentView and link it to the Layout of the activity_pokemon_details.xml-file.*/
        setContentView(R.layout.activity_pokemon_details);

        /*I state that the buttonReturn variable is equal to the view with the id buttonReturn.*/
        Button buttonReturn = findViewById(R.id.buttonReturn);

        /*I set an onClickListener on the button.*/
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*I link all variables to the id's of the correct views.
        * These views are in the activity_pokemon_details.xml.*/
        textViewPokemonId = findViewById(R.id.textViewPokemonId);
        textViewPokemonTypes = findViewById(R.id.textViewPokemonTypes);
        textViewPokemonName = findViewById(R.id.textViewPokemonName);
        imageViewPokemon = findViewById(R.id.imageViewPokemon);
        textViewHeight = findViewById(R.id.textViewHeight);
        textViewWeight = findViewById(R.id.textViewWeight);

        /*I make a new intent and call getIntent() to be the intent.
        * I use it to receive the PokemonDetailsResponse object.
        * The PokemonDetailsResponse object contains the details of a specific pokemon.
        * It is then used for the views.*/
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pokemonDetails")) {
            PokemonDetailsResponse detailsResponse = (PokemonDetailsResponse) intent.getSerializableExtra("pokemonDetails");

            if (detailsResponse != null) {
                textViewPokemonId.setText("Dexnumber: #" + detailsResponse.getId());

                List<String> typeNames = new ArrayList<>();
                if (detailsResponse.getTypes() != null) {
                    for (PokemonDetailsResponse.PokemonType type : detailsResponse.getTypes()) {
                        if (type != null && type.getType() != null && type.getType().getName() != null) {
                            typeNames.add(type.getType().getName());
                        }
                    }
                    textViewPokemonTypes.setText("Types: " + String.join(", ", typeNames));
                } else {
                    Log.e("PokemonDetailsActivity", "Pokemon types are null");
                }

                String name = detailsResponse.getName();
                String capitalizedPokemonName = name.substring(0, 1).toUpperCase() + name.substring(1);
                textViewPokemonName.setText("Name: " + capitalizedPokemonName);

                if (detailsResponse.getSprites() != null && detailsResponse.getSprites().getFrontDefault() != null) {
                    String imageUrl = detailsResponse.getSprites().getFrontDefault();
                    Glide.with(this).load(imageUrl).into(imageViewPokemon);
                } else {
                    Log.e("PokemonDetailsActivity", "Pokemon image URL is null");
                }

                // Set the height and weight of the Pokemon
                textViewHeight.setText("Height: " + detailsResponse.getHeight() / 10.0 + " m");
                textViewWeight.setText("Weight: " + detailsResponse.getWeight() / 10.0 + " kg");
            } else {
                Log.e("PokemonDetailsActivity", "PokemonDetailsResponse is null");
            }
        }
    }
}