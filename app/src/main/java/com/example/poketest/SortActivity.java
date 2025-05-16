package com.example.poketest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poketest.Models.Pokemon;
import com.example.poketest.Models.PokemonDetail;
import com.example.poketest.Models.PokemonResponse;
import com.example.poketest.PokeAPI.PokeapiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SortActivity extends AppCompatActivity {
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
                    AtomicInteger completedRequests = new AtomicInteger(0); // Счётчик завершённых запросов
                    int totalRequests = pokemonList.size();
                    AtomicBoolean isSuccess = new AtomicBoolean(true);

                    // Создаём сервис один раз перед циклом
                    PokeapiService detailsService = retrofit.create(PokeapiService.class);

                    for (Pokemon pItem : pokemonList) {
                        int id = pItem.getNumber();

                        Call<PokemonDetail> call2 = detailsService.getPokemonDetails(id);
                        call2.enqueue(new Callback<PokemonDetail>() {
                            @Override
                            public void onResponse(Call<PokemonDetail> call, Response<PokemonDetail> response) {
                                synchronized (result) {
                                    if (response.isSuccessful()) {
                                        PokemonDetail details = response.body();
                                        result.add(details);
                                        Log.i("tag", "DetailAdded  " + id);
                                    } else {
                                        isSuccess.set(false);
                                    }
                                }

                                // Проверяем, все ли запросы завершены
                                if (completedRequests.incrementAndGet() == totalRequests) {
                                    runOnUiThread(() -> {
                                        if (isSuccess.get()) {
                                            pokemonListAdapter.appendPokemonDetailsList(result);
                                        } else {
                                            // Обработка ошибки (например, показать Toast)
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<PokemonDetail> call, Throwable t) {
                                synchronized (result) {
                                    isSuccess.set(false);
                                }

                                if (completedRequests.incrementAndGet() == totalRequests) {
                                    runOnUiThread(() -> {
                                        // Обновление UI при ошибке
                                    });
                                }
                            }
                        });
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
