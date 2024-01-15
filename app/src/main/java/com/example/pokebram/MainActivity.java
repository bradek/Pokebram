package com.example.pokebram;

// imports...

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.net.Uri;

import com.example.pokebram.adapters.PokemonListAdapter;
import com.example.pokebram.api.ApiManager;
import com.example.pokebram.api.PokemonApiService;
import com.example.pokebram.api.PokemonListResponse;
import com.example.pokebram.api.PokemonDetailsResponse;
import com.example.pokebram.database.Pokemon;
import com.example.pokebram.database.PokemonDatabase;

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

    private PokemonDatabase db;
    /*I declare RecyclerView for displaying the list of Pokemon,
    * Adapter for managing the data of the RecyclerView,
    * And a list to store all the Pokemon items I fetched from pokeapi.*/
    private RecyclerView recyclerView;
    private PokemonListAdapter adapter;
    private List<PokemonListResponse.PokemonListItem> allPokemonListItems = new ArrayList<>();
    private List<Integer> pokedexNumbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*I set the layout of the activity to activity_main.xml*/
        setContentView(R.layout.activity_main);

        /*I create an instance of the Room database.*/
        db = Room.databaseBuilder(getApplicationContext(),
                PokemonDatabase.class, "pokemon-database").build();

        /*I link the recyclerView to the RecyclerView in the activity_main.xml.*/
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*I set the animation for item changes in the RecyclerView.*/
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Assuming you have a SearchView in your layout with the id "searchView"
        SearchView searchView = findViewById(R.id.searchView);

        /*I make a setOnQueryText listener on the SearchView.
        * This way, through a query the attribute you're asking for can be loaded.*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /*This method gets called when the user submits the query in the search bar.*/
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            /*I call this method when the query text is changed.*/
            @Override
            public boolean onQueryTextChange(String newText) {
                List<String> filteredList = new ArrayList<>();
                /*I loop through all the pokemon in the original list.*/
                for (PokemonListResponse.PokemonListItem pokemon : allPokemonListItems) {
                    /*If the Pokemon's name contains the new text, the editted list will get made by what has been selected.*/
                    if (pokemon.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(pokemon.getName());
                    }
                }

                adapter.updateList(filteredList);
                return false;
            }
        });


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
                    List<Integer> nextPagePokedexNumbers = getPokedexNumbers(nextPageResponse);

                    /*I append the new items to the existing list*/
                    allPokemonListItems.addAll(nextPageResponse.getResults());
                    pokedexNumbers.addAll(nextPagePokedexNumbers);

                    /*I ensure that this part is executed on the UI thread*/
                    List<Pokemon> pokemons = new ArrayList<>();
                    for (PokemonListResponse.PokemonListItem item : allPokemonListItems) {
                        Pokemon pokemon = new Pokemon();
                        pokemon.id = getPokemonIdFromUrl(item.getUrl());
                        pokemon.name = item.getName();
                        pokemons.add(pokemon);
                    }

                    /*I insert the Pokemon into the database*/
                    new Thread(() -> {
                        db.pokemonDao().insertAll(pokemons.toArray(new Pokemon[0]));
                    }).start();

                    /*I ensure that this part is executed on the UI thread*/
                    runOnUiThread(() -> {
                        if (adapter != null) {
                            adapter.addAll(nextPagePokemonNames, nextPagePokedexNumbers);
                        } else {
                            adapter = new PokemonListAdapter(nextPagePokemonNames, nextPagePokedexNumbers, new PokemonListAdapter.OnItemClickListener() {
                                // I handle item click event
                                @Override
                                public void onItemClick(String pokemonName) {
                                    Log.d("PokemonListAdapter", "Clicked on: " + pokemonName);
                                    fetchPokemonDetails(getPokemonUrlFromList(pokemonName));
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        }

                        if (nextPageResponse.getNext() != null) {
                            fetchNextPage(nextPageResponse.getNext());
                        }
                    });
                } else {
                    /*I show a TOAST-message if it fails.*/
                    showToast("API connection failed for next page");
                }
            }
            /*I handle the failure of the API call*/
            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                showToast("API connection failed: " + t.getMessage());
            }


            /*I extract and return the Pokedex numbers (ID's).
            * I extract them from PokemonListResponse.
            * The parameters are representing the response of pokeapi.
            * I return a list of integers, which are these Pokedex numbers (ID's), I extracted earlier.*/
            private List<Integer> getPokedexNumbers(PokemonListResponse pokemonListResponse) {
                List<PokemonListResponse.PokemonListItem> pokemonListItems = pokemonListResponse.getResults();
                List<Integer> pokedexNumbers = new ArrayList<>();
                for (PokemonListResponse.PokemonListItem item : pokemonListItems) {
                    String url = item.getUrl();
                    String[] urlParts = url.split("/");
                    String idString = urlParts[urlParts.length - 1];
                    int id = Integer.parseInt(idString);
                    pokedexNumbers.add(id);
                }
                return pokedexNumbers;
            }

            /*I extract and return the Pokemon names.*/
    private void fetchPokemonDetails(String pokemonUrl) {
        PokemonApiService apiService = ApiManager.getPokemonApiService();
        int pokemonId = getPokemonIdFromUrl(pokemonUrl);

        /*I log the extracted ID for debugging*/
        Log.d("MainActivity", "Pokemon ID for details: " + pokemonId);

        /*I make the API call to fetch the details.*/
        Call<PokemonDetailsResponse> detailsCall = apiService.getPokemonDetails(String.valueOf(pokemonId));

        /*I send the network request asynchronously through the enque.*/
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

                }
                /*I show a TOAST-message if it fails.*/
                else {
                    showToast("API connection failed for Pokemon details");
                    Log.e("MainActivity", "API connection failed for Pokemon details");
                }
            }

            /*I handle the failure of the API call*/
            @Override
            public void onFailure(Call<PokemonDetailsResponse> call, Throwable t) {
                showToast("API connection failed for Pokemon details");
                Log.e("MainActivity", "API connection failed for Pokemon details", t);
            }
        });
    }

    /*I fetch the next page (next 20 pokémon => Pagination).*/
    private void fetchNextPage(String nextPageUrl) {
        Log.d("NextPage", "Next Page URL: " + nextPageUrl);

        PokemonApiService apiService = ApiManager.getPokemonApiService();
        Call<PokemonListResponse> nextPageCall = apiService.getNextPokemonList(nextPageUrl);

        /*I send the network request asynchronously through the enque.*/
        nextPageCall.enqueue(new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                if (response.isSuccessful()) {
                    PokemonListResponse nextPageResponse = response.body();
                    List<String> nextPagePokemonNames = getPokemonNames(nextPageResponse);
                    List<Integer> nextPagePokedexNumbers = getPokedexNumbers(nextPageResponse); // Get the Pokedex numbers

                    /* I append the new items to the existing list*/
                    allPokemonListItems.addAll(nextPageResponse.getResults());

                    /*I ensure that this part is executed on the UI thread*/
                    runOnUiThread(() -> {
                        /*I append the new items to the existing list.*/
                        if (adapter != null) {
                            adapter.addAll(nextPagePokemonNames, nextPagePokedexNumbers);
                        }
                        /*I create a new adapter if there is none.*/
                        else {
                            adapter = new PokemonListAdapter(nextPagePokemonNames, nextPagePokedexNumbers, new PokemonListAdapter.OnItemClickListener() {
                                /*I handle item click event.*/
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

                        /*I check if there are more pages to fetch.*/
                        if (nextPageResponse.getNext() != null) {
                            /*I fetch the next page.*/
                            fetchNextPage(nextPageResponse.getNext());
                        }
                    });
                }
                /*I show a TOAST-message if it fails.*/
                else {
                    showToast("API connection failed for next page");
                }
            }

            /*I handle the failure of the API call*/
            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                showToast("API connection failed for next page");
            }
        });
    }

    /*I extract and return the Pokemon names.*/
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
            /*I parse the URL*/
            Uri uri = Uri.parse(url);
            /* I get the last path segment, which should be the Pokémon name.*/
            String pokemonName = uri.getLastPathSegment();
            /*I log the URL and the extracted Pokémon name for debugging.*/
            Log.d("MainActivity", "URL: " + url);
            Log.d("MainActivity", "Extracted Pokemon Name: " + pokemonName);

            /*I extract the numeric part of the Pokemon name.*/
            String numericPart = pokemonName.replaceAll("[^0-9]", "");

            if (!numericPart.isEmpty()) {
                /* I log the extracted numeric part.*/
                Log.d("MainActivity", "Extracted Numeric Part from Name: " + numericPart);

                /*I return the extracted numeric part as an integer.*/
                return Integer.parseInt(numericPart);
            }
            /*I return -1 if the extraction fails.*/
            else {
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

    /*I extract and return the Pokemon names.*/
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


});
    }
    /*I cancel all pending API calls when the activity is destroyed.*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the pending API calls when the activity is destroyed
        ApiManager.cancelAllRequests();
    }
}