package com.example.apiproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
class pokemonAdapter(private val pokemonList: List<MainActivity.Pokemon>) : RecyclerView.Adapter<pokemonAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pokemonImage: ImageView
        val pokemonName: TextView
        val pokemonType: TextView

        init {
            // Find our RecyclerView item's ImageView for future use
            pokemonImage = view.findViewById(R.id.pokeimage)
            pokemonName = view.findViewById<TextView>(R.id.name)
            pokemonType = view.findViewById<TextView>(R.id.types)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pokemon_item, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return pokemonList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemonList[position]

        Glide.with(holder.itemView)
            .load(pokemon.imageUrl)
            .centerCrop()
            .into(holder.pokemonImage)

        holder.pokemonName.text = pokemon.name
        holder.pokemonType.text = pokemon.types.joinToString(", ")
    }
}