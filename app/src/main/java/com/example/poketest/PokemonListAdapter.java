package com.example.poketest;

import static java.util.List.copyOf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.poketest.Models.Pokemon;
import com.example.poketest.Models.PokemonDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PokemonListAdapter extends RecyclerView.Adapter<PokemonListAdapter.ViewHolder> {

    private ArrayList<Pokemon> pokemonList;
    private ArrayList<Pokemon> allPokemonList;
    private ArrayList<PokemonDetail> allPokemonDetailsList;
    private Context context;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Pokemon pokemon);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public PokemonListAdapter(Context context) {
        this.context = context;
        pokemonList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Pokemon pokemon = pokemonList.get(position);
        holder.nameTextView.setText(pokemon.getName());

        Glide.with(context)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getNumber() + ".png")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.photoImageView);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(pokemon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public void appendPokemonList(ArrayList<Pokemon> pokemonList) {
        this.pokemonList.addAll(pokemonList);
        if(this.pokemonList.size() > 1300){
            this.allPokemonList = new ArrayList<>(this.pokemonList);
        }
        notifyDataSetChanged();
    }

    public void appendPokemonDetailsList(ArrayList<PokemonDetail> pokemonList) {
        this.allPokemonDetailsList.addAll(pokemonList);
        notifyDataSetChanged();
    }

    public void filterPokemonsByName(String input) {

        if(!this.allPokemonList.isEmpty()){

            ArrayList<Pokemon> allPokemonsCopy = new ArrayList<>(this.allPokemonList);

            List<Pokemon> filteredList = allPokemonsCopy.stream().filter(pokemon -> pokemon.getName().toLowerCase().contains(input.toLowerCase())).collect(Collectors.toList());
            pokemonList = new ArrayList<>(filteredList);
            notifyDataSetChanged();
        }

    }
    public void sortPokemonsByName(boolean reverse) {

        if (!reverse){
            Collections.sort(pokemonList, (p1, p2) ->
                    p1.getName().compareToIgnoreCase(p2.getName()));
        }else {
            Collections.sort(pokemonList, (p1, p2) ->
                    p2.getName().compareToIgnoreCase(p1.getName()));
        }

        notifyDataSetChanged();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView photoImageView;
        private TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.fotoImageView);
            nameTextView = itemView.findViewById(R.id.nombreTextView);
        }
    }
}
