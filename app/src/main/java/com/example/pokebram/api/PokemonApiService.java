package com.example.pokebram.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/*I made the PokemonApiService interface to make calls from the pokeApi.
* The reason for the existence of the 'NextPokemonList' is that I needed to use pagination.
* Only 20 pokemon could be fetched at a time,
* so I used pagination to continue fetching till the entire list has been walked through.*/
public interface PokemonApiService {
    @GET("pokemon")
    Call<PokemonListResponse> getPokemonList();

    @GET("pokemon/{name}")
    Call<PokemonDetailsResponse> getPokemonDetails(@Path("name") String name);

    @GET
    Call<PokemonListResponse> getNextPokemonList(@Url String url);
}