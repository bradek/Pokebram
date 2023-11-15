package com.example.pokebram.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class ApiManager {

    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // List to store active calls
    private static List<retrofit2.Call<?>> callList = new ArrayList<>();

    public static PokemonApiService getPokemonApiService() {
        return retrofit.create(PokemonApiService.class);
    }

    // Add a new call to the list
    public static void addCall(retrofit2.Call<?> call) {
        callList.add(call);
    }

    // Cancel all pending calls
    public static void cancelAllRequests() {
        for (retrofit2.Call<?> call : callList) {
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }
        }
        callList.clear(); // Clear the list after canceling calls
    }
}
