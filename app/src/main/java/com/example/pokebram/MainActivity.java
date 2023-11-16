package com.example.pokebram;

// imports...

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;

import com.example.pokebram.adapters.PokemonListAdapter;
import com.example.pokebram.api.ApiManager;
import com.example.pokebram.api.PokemonApiService;
import com.example.pokebram.api.PokemonListResponse;
import com.example.pokebram.api.PokemonDetailsResponse;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    /*I declare RecyclerView for displaying the list of Pokemon,
    * Adapter for managing the data of the RecyclerView,
    * And a list to store all the Pokemon items I fetched from pokeapi.*/
    private RecyclerView recyclerView;
    private PokemonListAdapter adapter;
    private List<PokemonListResponse.PokemonListItem> allPokemonListItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*I set the layout of the activity to activity_main.xml*/
        setContentView(R.layout.activity_main);

        /*I link the recyclerView to the RecyclerView in the activity_main.xml.*/
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*I set the animation for item changes in the RecyclerView.*/
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /*With the ApiManager I create an instance of PokemonApiService.
        * Then I make a call to fetch the list of Pokemon from pokeapi and a call to the ApiManager for tracking.*/
        PokemonApiService apiService = ApiManager.getPokemonApiService();
        Call<PokemonListResponse> listCall = apiService.getPokemonList();
        ApiManager.addCall(listCall);

        /*I send the network request asynchronously through the enque.*/
        listCall.enqueue(new Callback<PokemonListResponse>() {

            /*The onResponse method is called when a response is received.
            * If the response is successful, the list of Pokemon gets fetched withe names of the Pokemon from the response.
            * I add them to the existing list and then check if there are more pages to fetch.*/
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                if (response.isSuccessful()) {
                    PokemonListResponse nextPageResponse = response.body();
                    List<String> nextPagePokemonNames = getPokemonNames(nextPageResponse);

                    /* I append the new items to the existing list.*/
                    allPokemonListItems.addAll(nextPageResponse.getResults());
                    if (adapter != null) {
                        runOnUiThread(() -> adapter.addAll(nextPagePokemonNames));
                    } else {

                        /*I create a new PokemonListAdapter with the name of the Pokemon from the next page.*/
                        adapter = new PokemonListAdapter(nextPagePokemonNames, new PokemonListAdapter.OnItemClickListener() {

                            /*I set an OnItemClickListener to make sure click events can be used for the item.
                            * When an items is clicked, I fetch and display the details for that specific Pokemon.*/
                            @Override
                            public void onItemClick(String pokemonName) {
                                /*I handle item click event*/
                                Log.d("PokemonListAdapter", "Clicked on: " + pokemonName);
                                /*I fetch and show details for the clicked Pokemon*/
                                fetchPokemonDetails(getPokemonUrlFromList(pokemonName));
                            }
                        });
                        /*I set the adapter to the RecyclerView.*/
                        recyclerView.setAdapter(adapter);
                    }

                    /*I check if there are more pages of Pokemon.
                    * (Pages of 20 pokemon.)*/
                    if (nextPageResponse.getNext() != null) {
                        /*By using the getNext() method in the fetchNextPage, the next 20 pokemon are being fetched.*/
                        fetchNextPage(nextPageResponse.getNext());
                    }
                } else {
                    showToast("API connection failed for next page");
                }
            }

            /*When a network failure occures, a toast message is being shown to inform about the API failure.*/
            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                showToast("API connection failed: " + t.getMessage());
            }
        });
    }

    private void fetchPokemonDetails(String pokemonUrl) {
        PokemonApiService apiService = ApiManager.getPokemonApiService();
        int pokemonId = getPokemonIdFromUrl(pokemonUrl);

        // Log the extracted ID for debugging
        Log.d("MainActivity", "Pokemon ID for details: " + pokemonId);

        Call<PokemonDetailsResponse> detailsCall = apiService.getPokemonDetails(String.valueOf(pokemonId));

        detailsCall.enqueue(new Callback<PokemonDetailsResponse>() {
            @Override
            public void onResponse(Call<PokemonDetailsResponse> call, Response<PokemonDetailsResponse> response) {
                if (response.isSuccessful()) {
                    PokemonDetailsResponse detailsResponse = response.body();

                    // Handle the detailsResponse based on your requirements
                    // For example, you might start a new activity to display details

                    Intent intent = new Intent(MainActivity.this, PokemonDetailsActivity.class);
                    intent.putExtra("pokemonDetails", (Serializable) detailsResponse);
                    startActivity(intent);

                } else {
                    showToast("API connection failed for Pokemon details");
                    Log.e("MainActivity", "API connection failed for Pokemon details");
                }
            }

            @Override
            public void onFailure(Call<PokemonDetailsResponse> call, Throwable t) {
                showToast("API connection failed for Pokemon details");
                Log.e("MainActivity", "API connection failed for Pokemon details", t);
            }
        });
    }

    private void fetchNextPage(String nextPageUrl) {
        Log.d("NextPage", "Next Page URL: " + nextPageUrl);

        PokemonApiService apiService = ApiManager.getPokemonApiService();
        Call<PokemonListResponse> nextPageCall = apiService.getNextPokemonList(nextPageUrl);

        nextPageCall.enqueue(new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                if (response.isSuccessful()) {
                    PokemonListResponse nextPageResponse = response.body();
                    List<String> nextPagePokemonNames = getPokemonNames(nextPageResponse);

                    // Append the new items to the existing list
                    allPokemonListItems.addAll(nextPageResponse.getResults());

                    // Ensure that this part is executed on the UI thread
                    runOnUiThread(() -> {
                        // Append the new items to the existing list
                        if (adapter != null) {
                            adapter.addAll(nextPagePokemonNames);
                        } else {
                            adapter = new PokemonListAdapter(nextPagePokemonNames, new PokemonListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(String pokemonName) {
                                    // Handle item click event
                                    Log.d("PokemonListAdapter", "Clicked on: " + pokemonName);
                                    // Fetch and display details for the clicked Pokémon
                                    fetchPokemonDetails(getPokemonUrlFromList(pokemonName));
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        }

                        // Check if there are more pages
                        if (nextPageResponse.getNext() != null) {
                            // Fetch the next page recursively
                            fetchNextPage(nextPageResponse.getNext());
                        }
                    });
                } else {
                    showToast("API connection failed for next page");
                }
            }

            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                showToast("API connection failed for next page");
            }
        });
    }

    private String getPokemonUrlFromList(String pokemonName) {
        for (PokemonListResponse.PokemonListItem item : allPokemonListItems) {
            if (item.getName().equals(pokemonName)) {
                return item.getUrl();
            }
        }
        return "";
    }

    private int getPokemonIdFromUrl(String url) {
        try {
            // Parse the URL
            Uri uri = Uri.parse(url);
            // Get the last path segment, which should be the Pokémon name
            String pokemonName = uri.getLastPathSegment();
            // Log the URL and the extracted name
            Log.d("MainActivity", "URL: " + url);
            Log.d("MainActivity", "Extracted Pokemon Name: " + pokemonName);

            // Extract the numeric part of the Pokemon name
            String numericPart = pokemonName.replaceAll("[^0-9]", "");

            if (!numericPart.isEmpty()) {
                // Log the extracted numeric part
                Log.d("MainActivity", "Extracted Numeric Part from Name: " + numericPart);

                // Parse the Pokémon ID as an integer and return it
                return Integer.parseInt(numericPart);
            } else {
                // Log an error and return a default value or handle it as needed
                Log.e("MainActivity", "Invalid Pokemon Name format: " + pokemonName);
                return -1; // or any default value that makes sense in your case
            }
        } catch (Exception e) {
            // Log the error and return a default value or handle it as needed
            Log.e("MainActivity", "Error extracting Pokemon ID: " + url, e);
            return -1; // or any default value that makes sense in your case
        }
    }

    private List<String> getPokemonNames(PokemonListResponse pokemonListResponse) {
        List<PokemonListResponse.PokemonListItem> pokemonListItems = pokemonListResponse.getResults();
        List<String> pokemonNames = new ArrayList<>();
        for (PokemonListResponse.PokemonListItem item : pokemonListItems) {
            pokemonNames.add(item.getName());
        }
        return pokemonNames;
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the pending API calls when the activity is destroyed
        ApiManager.cancelAllRequests();
    }
}