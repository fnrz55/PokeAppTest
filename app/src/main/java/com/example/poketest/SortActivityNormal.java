package com.example.poketest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poketest.Models.Pokemon;
import com.example.poketest.Models.PokemonDetail;
import com.example.poketest.Models.PokemonResponse;
import com.example.poketest.PokeAPI.PokeapiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SortActivityNormal extends AppCompatActivity {
    private Retrofit retrofit;

    private RecyclerView recyclerView;
    private PokemonListAdapter pokemonListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.item_3);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.item_1) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.item_2) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            }
            return false;
        });
        recyclerView = findViewById(R.id.recyclerView);
        pokemonListAdapter = new PokemonListAdapter(this);
        recyclerView.setAdapter(pokemonListAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        int pokemonId = getIntent().getIntExtra("pokemon_id", 0);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        fetchSortedData();
    }
    private void fetchSortedData() {
        PokeapiService service = retrofit.create(PokeapiService.class);
        Call<PokemonResponse> responseCall = service.getPokemonList(1400, 0);

        responseCall.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.isSuccessful()) {
                    PokemonResponse responseBody = response.body();
                    ArrayList<Pokemon> pokemonList = responseBody.getResults();

                    ArrayList<PokemonDetail> result = new ArrayList<>();
                    final boolean[] isSuccess = {true};

                    for (Pokemon pItem:
                            pokemonList) {

                        int id = pItem.getNumber();

                        PokeapiService service = retrofit.create(PokeapiService.class);
                        Call<PokemonDetail> call2 = service.getPokemonDetails(id);

                        call2.enqueue(new Callback<PokemonDetail>() {
                                         @Override
                                         public void onResponse(Call<PokemonDetail> call, Response<PokemonDetail> response) {
                                             if (response.isSuccessful()) {
                                                 PokemonDetail details = response.body();
                                                 result.add(details);

                                             } else {
                                                 isSuccess[0] = false;
                                             }
                                         }

                                         @Override
                                         public void onFailure(Call<PokemonDetail> call, Throwable throwable) {
                                             isSuccess[0] = false;
                                         }

                                     });

                    Log.e("tag", " onResponse search: " + pokemonList.size());
                }
                    if (isSuccess[0]){
                        pokemonListAdapter.appendPokemonDetailsList(result);
                    }
                }
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                Log.e("tag", " onFailure: " + t.getMessage());
            }
        });
    }
}
