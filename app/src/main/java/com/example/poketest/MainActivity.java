package com.example.poketest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poketest.Models.Pokemon;
import com.example.poketest.Models.PokemonRespuesta;
import com.example.poketest.PokeAPI.PokeapiService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "POKEDEX";

    private Retrofit retrofit;

    private RecyclerView recyclerView;
    private ListaPokemonAdapter pokemonListAdapter;
    private int offset;
    private boolean canLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        pokemonListAdapter = new ListaPokemonAdapter(this);
        recyclerView.setAdapter(pokemonListAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i("tag", "scroll");
                Log.i("tag", String.valueOf(dy));
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    Log.i("tag", String.valueOf(visibleItemCount));
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (canLoadMore) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            Log.i("tag", "Reached the end.");
                            canLoadMore = false;
                            offset += 20;
                            fetchData(offset);
                        }
                    }
                }
            }
        });
        pokemonListAdapter.setOnItemClickListener(pokemon -> {
            Intent intent = new Intent(MainActivity.this, PokemonDetailActivity.class);
            intent.putExtra("pokemon_id", pokemon.getNumber());
            startActivity(intent);
        });

        retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        canLoadMore = true;
        offset = 0;
        fetchData(offset);
    }

    private void fetchData(int offset) {
        PokeapiService service = retrofit.create(PokeapiService.class);
        Call<PokemonRespuesta> responseCall = service.obtenerListaPokemon(30, offset);

        responseCall.enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                canLoadMore = true;
                if (response.isSuccessful()) {
                    PokemonRespuesta responseBody = response.body();
                    ArrayList<Pokemon> pokemonList = responseBody.getResults();
                    Log.e("tag", " onResponse: " + pokemonList.size());
                    pokemonListAdapter.adicionarListaPokemon(pokemonList);
                } else {
                    Log.e(TAG, " onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                canLoadMore = true;
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });
    }
}
