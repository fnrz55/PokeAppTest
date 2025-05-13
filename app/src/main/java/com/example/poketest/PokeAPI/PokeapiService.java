package com.example.poketest.PokeAPI;

import com.example.poketest.Models.PokemonDetail;
import com.example.poketest.Models.PokemonRespuesta;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Path;


public interface PokeapiService {

    @GET("pokemon")
    Call<PokemonRespuesta> obtenerListaPokemon(@Query("limit") int limit, @Query("offset") int offset);

    @GET("pokemon/{id}")
    Call<PokemonDetail> getPokemonDetails(@Path("id") int id);

}