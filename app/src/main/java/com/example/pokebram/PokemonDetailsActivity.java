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
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.pokebram.R;
import com.example.pokebram.api.ApiManager;
import com.example.pokebram.api.PokemonApiService;
import com.example.pokebram.api.PokemonDetailsResponse;
import com.example.pokebram.api.PokemonSpeciesResponse;
import com.example.pokebram.database.Pokemon;
import com.example.pokebram.database.PokemonDatabase;

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

    /*I use the onCreate method to set the content view to the activity_pokemon_details.xml.
    I also set the onClickListener for the buttonReturn.
    When the button is clicked, the activity is finished.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_details);

        Button buttonReturn = findViewById(R.id.buttonReturn);
        buttonReturn.setOnClickListener(v -> finish());

        /*I initialize the variables I declared earlier.
        I use findViewById to find the views by their id.*/
        textViewPokemonId = findViewById(R.id.textViewPokemonId);
        textViewPokemonTypes = findViewById(R.id.textViewPokemonTypes);
        textViewPokemonName = findViewById(R.id.textViewPokemonName);
        imageViewPokemon = findViewById(R.id.imageViewPokemon);
        textViewHeight = findViewById(R.id.textViewHeight);
        textViewWeight = findViewById(R.id.textViewWeight);
        textViewPokemonAbilities = findViewById(R.id.textViewPokemonAbilities);
        /*I create a new LinearLayout and I use findViewById to find the view by its id.*/
        LinearLayout statsContainer = findViewById(R.id.statsContainer);

        /*I create a new Intent and I use getIntent to get the intent.*/
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pokemonDetails")) {
            PokemonDetailsResponse detailsResponse = (PokemonDetailsResponse) intent.getSerializableExtra("pokemonDetails");

            /*I check if the detailsResponse is not null.*/
            if (detailsResponse != null) {
                textViewPokemonId.setText("Dexnumber: #" + detailsResponse.getId());

                /*I retrieve the types.*/
                List<String> typeNames = new ArrayList<>();
                if (detailsResponse.getTypes() != null) {
                    for (PokemonDetailsResponse.PokemonType type : detailsResponse.getTypes()) {
                        if (type != null && type.getType() != null && type.getType().getName() != null) {
                            typeNames.add(type.getType().getName());
                        }
                    }
                    textViewPokemonTypes.setText("Types: " + String.join(", ", typeNames));
                }
                /*I check if the types are null.*/
                else {
                    Log.e("PokemonDetailsActivity", "Pokemon types are null");
                }

                String name = detailsResponse.getName();
                String capitalizedPokemonName = name.substring(0, 1).toUpperCase() + name.substring(1);
                /*I set the name of the Pokemon.*/
                textViewPokemonName.setText("Name: " + capitalizedPokemonName);

                /*I check if the sprites are not null.
                * Then I retrieve the image URL and I use Glide to load the image into the imageViewPokemon.*/
                if (detailsResponse.getSprites() != null && detailsResponse.getSprites().getFrontDefault() != null) {
                    String imageUrl = detailsResponse.getSprites().getFrontDefault();
                    Glide.with(this).load(imageUrl).into(imageViewPokemon);
                }
                else {
                    Log.e("PokemonDetailsActivity", "Pokemon image URL is null");
                }

                /* I set the height and weight of the Pokemon.*/
                textViewHeight.setText("Height: " + detailsResponse.getHeight() / 10.0 + " m");
                textViewWeight.setText("Weight: " + detailsResponse.getWeight() / 10.0 + " kg");

                /*I retrieve the stats.*/
                List<PokemonDetailsResponse.Stat> stats = detailsResponse.getStats();

                /*I retrieve the abilities.*/
                List<PokemonDetailsResponse.Ability> abilities = detailsResponse.getAbilities();
                List<String> abilityNames = new ArrayList<>();
                for (PokemonDetailsResponse.Ability ability : abilities) {
                    if (ability != null && ability.getAbility() != null && ability.getAbility().getName() != null) {
                        abilityNames.add(ability.getAbility().getName());
                    }
                }
                /*I set the abilities of the Pokemon.*/
                textViewPokemonAbilities.setText("Abilities: " + String.join(", ", abilityNames));

                /*I create a TextView for the title 'Base stats'.*/
                TextView titleView = new TextView(this);
                titleView.setText("Base stats");
                /*I set the typeface to bold.*/
                titleView.setTypeface(null, Typeface.BOLD);

                /*I apply the BaseStatTitleText style to the TextView.*/
                if (Build.VERSION.SDK_INT < 23) {
                    titleView.setTextAppearance(this, R.style.BaseStatTitleText);
                }
                else {
                    titleView.setTextAppearance(R.style.BaseStatTitleText);
                }

                /*I add the TextView to the statsContainer view.*/
                statsContainer.addView(titleView);

                /*I loop through the stats.*/
                for (PokemonDetailsResponse.Stat stat : stats) {
                    String statName = stat.getStatName().getName();
                    int baseStat = stat.getBaseStat();

                    /*I create a TextView for each stat.*/
                    TextView statView = new TextView(this);
                    statView.setText(statName + ": " + baseStat);

                    /*I apply the BaseStatText style to the TextView.*/
                    if (Build.VERSION.SDK_INT < 23) {
                        statView.setTextAppearance(this, R.style.BaseStatText);
                    }
                    else {
                        statView.setTextAppearance(R.style.BaseStatText);
                    }

                    /*I add the TextView to the statsContainer view.*/
                    statsContainer.addView(statView);

                    /*I create a ProgressBar for each stat.*/
                    ProgressBar statBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
                    statBar.setMax(255); // Set the maximum possible value of a stat
                    statBar.setProgress(baseStat); // Set the current value of the stat

                    /*I add the ProgressBar to the statsContainer view*/
                    statsContainer.addView(statBar);
                }

                /*I fetch and display the flavor text.*/
                /*I create a new PokemonApiService and I use ApiManager to get the PokemonApiService.*/
                PokemonApiService api = ApiManager.getPokemonApiService();
                Call<PokemonSpeciesResponse> call = api.getPokemonSpecies(detailsResponse.getId());
                call.enqueue(new Callback<PokemonSpeciesResponse>() {
                    /*I check if the response is successful.*/
                    @Override
                    public void onResponse(Call<PokemonSpeciesResponse> call, Response<PokemonSpeciesResponse> response) {
                        /*I check if the response is successful.*/
                        if (response.isSuccessful()) {
                            /*I retrieve the speciesResponse.*/
                            PokemonSpeciesResponse speciesResponse = response.body();
                            if (speciesResponse != null) {
                                /*I loop through the flavor text entries.*/
                                for (PokemonSpeciesResponse.FlavorTextEntry entry : speciesResponse.getFlavorTextEntries()) {
                                    if (entry.getLanguage().getName().equals("en")) {
                                        String flavorText = entry.getFlavorText();

                                        /*I create a TextView for the title 'Entry'.*/
                                        TextView entryTitleView = new TextView(PokemonDetailsActivity.this);
                                        entryTitleView.setText("Entry");
                                        entryTitleView.setTypeface(null, Typeface.BOLD);

                                        /*I apply the BaseStatTitleText style to the TextView.*/
                                        if (Build.VERSION.SDK_INT < 23) {
                                            entryTitleView.setTextAppearance(PokemonDetailsActivity.this, R.style.BaseStatTitleText);
                                        } else {
                                            entryTitleView.setTextAppearance(R.style.BaseStatTitleText);
                                        }

                                        /*I add the TextView to the flavorTextContainer view.*/
                                        LinearLayout flavorTextContainer = findViewById(R.id.flavorTextContainer);
                                        flavorTextContainer.addView(entryTitleView);

                                        /*I create a TextView for the flavor text.*/
                                        TextView flavorTextView = new TextView(PokemonDetailsActivity.this);
                                        flavorTextView.setText(flavorText);

                                        /*I apply the BaseStatText style to the TextView.*/
                                        if (Build.VERSION.SDK_INT < 23) {
                                            flavorTextView.setTextAppearance(PokemonDetailsActivity.this, R.style.BaseStatText);
                                        }
                                        else {
                                            flavorTextView.setTextAppearance(R.style.BaseStatText);
                                        }

                                        /*I add the TextView to the flavorTextContainer view.*/
                                        flavorTextContainer.addView(flavorTextView);

                                        break;
                                    }
                                }
                            }
                        }
                    }

                    /*I check if the response is not successful.
                    * I log the error.*/
                    @Override
                    public void onFailure(Call<PokemonSpeciesResponse> call, Throwable t) {
                        Log.e("PokemonDetailsActivity", "Failed to get Pokemon species", t);
                    }
                });
            }
        }
    }
}