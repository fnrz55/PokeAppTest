package com.example.poketest;


import static android.content.Intent.getIntent;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.poketest.Models.PokemonDetail;
import com.example.poketest.PokeAPI.PokeapiService;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detail_test);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onSupportNavigateUp());

        int pokemonId = getIntent().getIntExtra("pokemon_id", 0);
        fetchPokemonDetails(pokemonId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.e("tag", "Нажата кнопка назад");
        onBackPressed();
        return true;
    }

    private void fetchPokemonDetails(int id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PokeapiService service = retrofit.create(PokeapiService.class);
        Call<PokemonDetail> call = service.getPokemonDetails(id);

        call.enqueue(new Callback<PokemonDetail>() {
            @Override
            public void onResponse(Call<PokemonDetail> call, Response<PokemonDetail> response) {
                if (response.isSuccessful()) {
                    PokemonDetail details = response.body();
                    updateUI(details);
                    Log.e("tag", "Получено: " + details.getHeight());
                }
            }

            @Override
            public void onFailure(Call<PokemonDetail> call, Throwable t) {
                Log.e("tag", "Error fetching details: " + t.getMessage());
            }
        });
    }

    private void updateUI(PokemonDetail details) {
        TextView nameView = findViewById(R.id.nameTextView);
        ImageView imageView = findViewById(R.id.detailImageView);

        nameView.setText(details.getName());
        Glide.with(this)
                .load(details.getSprites().getFrontDefault())
                .into(imageView);

        TextView heightView = findViewById(R.id.heightTextView);
        heightView.setText("Height: " + Integer.toString(details.getHeight()));

        TextView weightView = findViewById(R.id.weightTextView);
        weightView.setText("Weight: " + Integer.toString(details.getWeight()));

        List<PokemonDetail.AbilityEntry> abilityList = details.getAbilities();
        Log.e("tag", "updateUI: " + abilityList.size());
        for (PokemonDetail.AbilityEntry ability:
        abilityList) {

            TextView abilitiesView = findViewById(R.id.abilitiesTextView);
                abilitiesView.setText(ability.getAbility().getName());

        }


//        TextView abilitiesView = findViewById(R.id.abilitiesTextView);
//        abilitiesView.setText(details.getAbilities().toString());

//        TextView statsView = findViewById(R.id.statsTextView);
//        statsView.setText(details.getStats().toString());

    }
}
