package com.example.pokebram.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface PokemonApiService {
    @GET("pokemon")
    Call<PokemonListResponse> getPokemonList();

    @GET("pokemon/{name}")
    Call<PokemonDetailsResponse> getPokemonDetails(@Path("name") String name);

    @GET
    Call<PokemonListResponse> getNextPokemonList(@Url String url);
}