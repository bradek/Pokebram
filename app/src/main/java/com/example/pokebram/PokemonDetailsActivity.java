package com.example.pokebram;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.pokebram.R;
import com.example.pokebram.api.ApiClient;
import com.example.pokebram.api.PokemonApiService;
import com.example.pokebram.api.PokemonDetailsResponse;
import com.example.pokebram.api.PokemonSpeciesResponse;

import java.util.List;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonDetailsActivity extends AppCompatActivity {

    /*All variables I need are declared here.
     * I use them for the views (generally TextViews) I did put on the activity_pokemon_details.xml.*/
    private TextView textViewPokemonId;
    private TextView textViewPokemonTypes;
    private TextView textViewPokemonName;
    private ImageView imageViewPokemon;
    private TextView textViewHeight;
    private TextView textViewWeight;
    // Declare the TextView for Pokemon abilities
    private TextView textViewPokemonAbilities;

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
        LinearLayout statsContainer = findViewById(R.id.statsContainer);
        // Inside onCreate method
        textViewPokemonAbilities = findViewById(R.id.textViewPokemonAbilities); // Replace with the actual ID of the TextView in your XML layout

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
                /*I retrieve the stats.*/
                List<PokemonDetailsResponse.Stat> stats = detailsResponse.getStats();

                // After receiving the PokemonDetailsResponse object
                List<PokemonDetailsResponse.Ability> abilities = detailsResponse.getAbilities();
                List<String> abilityNames = new ArrayList<>();
                for (PokemonDetailsResponse.Ability ability : abilities) {
                    if (ability != null && ability.getAbility() != null && ability.getAbility().getName() != null) {
                        abilityNames.add(ability.getAbility().getName());
                    }
                }
                textViewPokemonAbilities.setText("Abilities: " + String.join(", ", abilityNames));

                // Create a TextView for the title 'Base stats'
                TextView titleView = new TextView(this);
                titleView.setText("Base stats");
                titleView.setTypeface(null, Typeface.BOLD);

                // Apply the BaseStatTitleText style to the TextView
                if (Build.VERSION.SDK_INT < 23) {
                    titleView.setTextAppearance(this, R.style.BaseStatTitleText);
                } else {
                    titleView.setTextAppearance(R.style.BaseStatTitleText);
                }

                // Add the TextView to the statsContainer view
                statsContainer.addView(titleView);

                for (PokemonDetailsResponse.Stat stat : stats) {
                    String statName = stat.getStatName().getName();
                    int baseStat = stat.getBaseStat();

                    // Create a TextView for each stat
                    TextView statView = new TextView(this);
                    statView.setText(statName + ": " + baseStat);

                    // Apply the BaseStatText style to the TextView
                    if (Build.VERSION.SDK_INT < 23) {
                        statView.setTextAppearance(this, R.style.BaseStatText);
                    } else {
                        statView.setTextAppearance(R.style.BaseStatText);
                    }

                    // Add the TextView to the statsContainer view
                    statsContainer.addView(statView);

                    /*I create a ProgressBar for each stat.*/
                    ProgressBar statBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
                    statBar.setMax(255); // Set the maximum possible value of a stat
                    statBar.setProgress(baseStat); // Set the current value of the stat

                    /*I add the ProgressBar to the statsContainer view*/
                    statsContainer.addView(statBar);
                }

                // Fetch and display the flavor text
                PokemonApiService api = ApiClient.getClient().create(PokemonApiService.class);
                Call<PokemonSpeciesResponse> call = api.getPokemonSpecies(detailsResponse.getId());
                call.enqueue(new Callback<PokemonSpeciesResponse>() {
                    @Override
                    public void onResponse(Call<PokemonSpeciesResponse> call, Response<PokemonSpeciesResponse> response) {
                        if (response.isSuccessful()) {
                            PokemonSpeciesResponse speciesResponse = response.body();
                            if (speciesResponse != null) {
                                for (PokemonSpeciesResponse.FlavorTextEntry entry : speciesResponse.getFlavorTextEntries()) {
                                    if (entry.getLanguage().getName().equals("en")) {
                                        String flavorText = entry.getFlavorText();

                                        // Create a TextView for the title 'Entry'
                                        TextView entryTitleView = new TextView(PokemonDetailsActivity.this);
                                        entryTitleView.setText("Entry");
                                        entryTitleView.setTypeface(null, Typeface.BOLD);

                                        // Apply the BaseStatTitleText style to the TextView
                                        if (Build.VERSION.SDK_INT < 23) {
                                            entryTitleView.setTextAppearance(PokemonDetailsActivity.this, R.style.BaseStatTitleText);
                                        } else {
                                            entryTitleView.setTextAppearance(R.style.BaseStatTitleText);
                                        }

                                        // Add the TextView to the flavorTextContainer view
                                        LinearLayout flavorTextContainer = findViewById(R.id.flavorTextContainer);
                                        flavorTextContainer.addView(entryTitleView);

                                        // Create a TextView for the flavor text
                                        TextView flavorTextView = new TextView(PokemonDetailsActivity.this);
                                        flavorTextView.setText(flavorText);

                                        // Apply the desired style to the TextView
                                        if (Build.VERSION.SDK_INT < 23) {
                                            flavorTextView.setTextAppearance(PokemonDetailsActivity.this, R.style.BaseStatText);
                                        } else {
                                            flavorTextView.setTextAppearance(R.style.BaseStatText);
                                        }

                                        // Add the TextView to the flavorTextContainer view
                                        flavorTextContainer.addView(flavorTextView);

                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PokemonSpeciesResponse> call, Throwable t) {
                        Log.e("PokemonDetailsActivity", "Failed to get Pokemon species", t);
                    }
                });
            } else {
                Log.e("PokemonDetailsActivity", "PokemonDetailsResponse is null");
            }
        }
    }
}