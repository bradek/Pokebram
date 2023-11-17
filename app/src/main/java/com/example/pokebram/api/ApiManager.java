package com.example.pokebram.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class ApiManager {

    /*This is the base URL of the api.
    * The api I'm using is pokeapi.*/
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    /*I'm building a new Retrofit.
    * Retrofit is a tool which makes the usage of JSON easier.
    * I let it obviously use the base URL we defined earlier.*/
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    /*I make a list which stores active calls.
    * I use retrofit for it.*/
    private static List<retrofit2.Call<?>> callList = new ArrayList<>();

    /*I create and return an instance of PokemonApiService.
    * With this Retrofit call I tell Retrofit to create an implementation of the PokemonApiService interface.
    * The returned PokemonApiService can then be used to make API calls to the PokeAPI.*/
    public static PokemonApiService getPokemonApiService() {
        return retrofit.create(PokemonApiService.class);
    }

    /*I add a new call to the list.*/
    public static void addCall(retrofit2.Call<?> call) {
        callList.add(call);
    }

    /*I cancel all still on going calls.*/
    public static void cancelAllRequests() {
        for (retrofit2.Call<?> call : callList) {
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }
        }
        callList.clear(); // Clear the list after canceling calls
    }
}