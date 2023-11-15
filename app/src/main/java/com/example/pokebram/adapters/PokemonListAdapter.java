package com.example.pokebram.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokebram.R;

import java.util.List;

public class PokemonListAdapter extends RecyclerView.Adapter<PokemonListAdapter.ViewHolder> {
    private List<String> pokemonNames;
    private OnItemClickListener onItemClickListener; // Define the listener

    // Constructor
    public PokemonListAdapter(List<String> pokemonNames, OnItemClickListener onItemClickListener) {
        this.pokemonNames = pokemonNames;
        if (onItemClickListener == null) {
            throw new IllegalArgumentException("OnItemClickListener cannot be null");
        }
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String pokemonName = pokemonNames.get(position);
        holder.bind(pokemonName);

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(pokemonName));
        }
    }

    @Override
    public int getItemCount() {
        return pokemonNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewPokemonName);
        }

        public void bind(String pokemonName) {
            textView.setText(pokemonName);
        }
    }

    // Interface for item click events
// Interface for item click events
    public interface OnItemClickListener {
        void onItemClick(String pokemonName);
    }

    public void addAll(List<String> newPokemonNames) {
        pokemonNames.addAll(newPokemonNames);
        notifyDataSetChanged();
    }


}