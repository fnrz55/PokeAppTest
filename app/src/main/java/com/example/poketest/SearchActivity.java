package com.example.poketest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poketest.Models.Pokemon;
import com.example.poketest.Models.PokemonResponse;
import com.example.poketest.PokeAPI.PokeapiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchBar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    private Retrofit retrofit;

    private RecyclerView recyclerView;
    private PokemonListAdapter pokemonListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.item_2);
        EditText searchInput = findViewById(R.id.search_input);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentText = s.toString();

                pokemonListAdapter.filterPokemonsByName(currentText);


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.item_1) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.item_3) {
                startActivity(new Intent(this, SortActivity.class));
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

        pokemonListAdapter.setOnItemClickListener(pokemon -> {
            Intent intent = new Intent(SearchActivity.this, PokemonDetailActivity.class);
            intent.putExtra("pokemon_id", pokemon.getNumber());
            startActivity(intent);
        });

        fetchSearchData();
    }
    private void fetchSearchData() {
        PokeapiService service = retrofit.create(PokeapiService.class);
        Call<PokemonResponse> responseCall = service.getPokemonList(1400, 0);

        responseCall.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.isSuccessful()) {
                    PokemonResponse responseBody = response.body();
                    ArrayList<Pokemon> pokemonList = responseBody.getResults();
                    Log.e("tag", " onResponse search: " + pokemonList.size());
                    pokemonListAdapter.appendPokemonList(pokemonList);
                } else {
                    Log.e("tag", " onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                Log.e("tag", " onFailure: " + t.getMessage());
            }
        });
    }
}
