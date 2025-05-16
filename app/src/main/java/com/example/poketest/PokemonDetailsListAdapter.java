package com.example.poketest;

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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PokemonDetailsListAdapter extends RecyclerView.Adapter<PokemonDetailsListAdapter.ViewHolder> {

    private ArrayList<PokemonDetail> allPokemonDetailsList;
    private Context context;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PokemonDetail pokemon);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public PokemonDetailsListAdapter(Context context) {
        this.context = context;
        allPokemonDetailsList = new ArrayList<PokemonDetail>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        PokemonDetail pokemon = allPokemonDetailsList.get(position);
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
        return allPokemonDetailsList.size();
    }

    public void appendPokemonList(ArrayList<PokemonDetail> pokemonList) {
        this.allPokemonDetailsList.addAll(pokemonList);
        notifyDataSetChanged();
    }

    public void sortPokemonsByName(boolean reverse) {

        if (!reverse){
            Collections.sort(allPokemonDetailsList, (p1, p2) ->
                    p1.getName().compareToIgnoreCase(p2.getName()));
        }else {
            Collections.sort(allPokemonDetailsList, (p1, p2) ->
                    p2.getName().compareToIgnoreCase(p1.getName()));
        }

        notifyDataSetChanged();

    }

    public void sortPokemonsByHeight() {

        Collections.sort(allPokemonDetailsList, Comparator.comparingInt(PokemonDetail::getHeight));

        notifyDataSetChanged();

    }

    public void sortPokemonsByWeight() {

        Collections.sort(allPokemonDetailsList, Comparator.comparingInt(PokemonDetail::getWeight));

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
