package com.example.poketest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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
    private Spinner sortSpinner;

    String[] sortOptions = {"По высоте", "По весу", "A-Z", "Z-A"};

    private PokemonDetailsListAdapter pokemonListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
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

        sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, sortOptions );
        sortSpinner.setAdapter(adapter);
        recyclerView = findViewById(R.id.recyclerView);
        pokemonListAdapter = new PokemonDetailsListAdapter(this);
        recyclerView.setAdapter(pokemonListAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        pokemonListAdapter.sortPokemonsByHeight();
                        break;
                    case 1:
                        pokemonListAdapter.sortPokemonsByWeight();
                        break;
                    case 2:
                        pokemonListAdapter.sortPokemonsByName(false);
                        break;
                    case 3:
                        pokemonListAdapter.sortPokemonsByName(true);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        pokemonListAdapter.setOnItemClickListener(pokemon -> {
            Intent intent = new Intent(SortActivity.this, PokemonDetailActivity.class);
            intent.putExtra("pokemon_id", pokemon.getNumber());
            startActivity(intent);
        });
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
                                            pokemonListAdapter.appendPokemonList(result);
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
