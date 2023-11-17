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
    private List<Integer> pokedexNumbers; // New list for Pokedex numbers
    private OnItemClickListener onItemClickListener; // Define the listener

    /*This is the constructor for the PokemonListAdapter class.
     * It takes in a list of strings, a list of integers and an OnItemClickListener object.*/
    public PokemonListAdapter(List<String> pokemonNames, List<Integer> pokedexNumbers, OnItemClickListener onItemClickListener) {
        this.pokemonNames = pokemonNames;
        this.pokedexNumbers = pokedexNumbers; // Initialize the Pokedex numbers list
        if (onItemClickListener == null) {
            throw new IllegalArgumentException("OnItemClickListener cannot be null");
        }
        this.onItemClickListener = onItemClickListener;
    }

    /*I create a ViewHolder object that holds the view.
     * I use the LayoutInflater class to instantiate layout XML into the View objects.*/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_list_item, parent, false);
        return new ViewHolder(view);
    }

    /*The onBindViewHolder method binds the view holder to the adapter.*/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String pokemonName = pokemonNames.get(position);
        int pokedexNumber = pokedexNumbers.get(position); // Get the Pokedex number
        holder.bind(pokemonName, pokedexNumber); // Bind the Pokedex number

        /*I'm checking if the the onItemClickListener is not null.
        This if statement checks if the onItemClickListener is not null.
        If the onItemClickListener is not null, I set the onClickListener to the itemView*/
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(pokemonName));
        }
    }

    // This method returns the size of the list
    @Override
    public int getItemCount() {
        return pokemonNames.size();
    }

    /*I create a ViewHolder class that extends the RecyclerView.ViewHolder class.*/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /*I made TextView textView final so it cannot be reassigned.
         * Once it is assigned to a TextView object, I don't want it to be reassigned to another.*/
        private final TextView textView;
        private final TextView textViewPokedexNumber; // New TextView for the Pokedex number

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewPokemonName);
            textViewPokedexNumber = itemView.findViewById(R.id.textViewPokedexNumber); // Initialize the Pokedex number TextView
        }

        /*I create a bind method which sets the text of textView to a pokemonName and the text of textViewPokedexNumber to a pokedexNumber.*/
        public void bind(String pokemonName, int pokedexNumber) {
            String displayText = pokedexNumber + " : " + pokemonName;
            textView.setText(displayText);
        }
    }

    /*This is an interface for item Click-events.*/
    public interface OnItemClickListener {
        void onItemClick(String pokemonName);
    }

    /*I made an addAll()-method to add all the Pokémon names from the newPokemonNames list and all the Pokedex numbers from the newPokedexNumbers list to the pokemonNames and pokedexNumbers lists.
     * The pokemonNames and pokedexNumbers lists are member variables of PokemonListAdapter.*/
    public void addAll(List<String> newPokemonNames, List<Integer> newPokedexNumbers) { // New parameter for the Pokedex numbers
        pokemonNames.addAll(newPokemonNames);
        pokedexNumbers.addAll(newPokedexNumbers); // Add the new Pokedex numbers
        notifyDataSetChanged();
    }
}