package com.example.apiproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray
import java.util.Random

class MainActivity : AppCompatActivity() {
    var name = ""
    private lateinit var pokemonList : MutableList<Pokemon>
    private lateinit var rvPokemon : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvPokemon = findViewById(R.id.pokemon_list)
        pokemonList = mutableListOf()
        pokemonAPI()
    }

    data class Pokemon(
        val name: String,
        val types: List<String>,
        val imageUrl: String
    )

    private fun pokemonAPI() {
        val client = AsyncHttpClient()
        val randomOffset = (0..898).random()
        val url = "https://pokeapi.co/api/v2/pokemon?limit=20&offset=$randomOffset"
        client[url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON
            ) {
                if (json != null) {
                    Log.d("PokemonAPI", "JSON Response: $json")
                    // Check if "results" array exists in the JSON response
                    if (json.jsonObject.has("results")) {
                        val resultsArray = json.jsonObject.getJSONArray("results")
                        if (resultsArray != null) {
                            Log.d("PokemonAPI", "Results Array: $resultsArray")
                            processPokemonData(resultsArray)
                            Log.d("Success", "response successful")
                            val adapter = pokemonAdapter(pokemonList)
                            rvPokemon.adapter = adapter
                            rvPokemon.layoutManager = LinearLayoutManager(this@MainActivity)
                        } else {
                            Log.e("PokemonAPI", "Results Array is null")
                        }
                    } else {
                        Log.e("PokemonAPI", "Results Array not found in JSON response")
                    }
                } else {
                    Log.e("PokemonAPI", "JSON Response is null")
                }


            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Error", errorResponse)
            }
        }]



    }
    private fun processPokemonData(jsonArray: JSONArray?) {
        jsonArray?.let {
            pokemonList.clear()
            // Process the JSON array containing Pokémon data here
            for (i in 0 until it.length()) {
                val pokemonObject = it.getJSONObject(i)
                val name = pokemonObject.getString("name")
                val url = pokemonObject.getString("url")

                // Fetch additional details using the URL
                fetchPokemonDetails(name, url)
            }
        } ?: Log.e("PokemonAPI", "JSON Array is null")

    }

    private fun fetchPokemonDetails(name: String, url: String) {
        val client = AsyncHttpClient()
        client[url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                if (json != null) {
                    val types = mutableListOf<String>()
                    val spritesObject = json.jsonObject.getJSONObject("sprites")
                    val imageUrl = spritesObject.getString("front_default")

                    // Check if "types" array exists in the JSON response
                    if (json.jsonObject.has("types")) {
                        val typesArray = json.jsonObject.getJSONArray("types")
                        for (j in 0 until typesArray.length()) {
                            val typeObject = typesArray.getJSONObject(j).getJSONObject("type")
                            types.add(typeObject.getString("name"))
                        }
                    } else {
                        Log.e("PokemonAPI", "Types array not found for Pokemon: $name")
                    }

                    // Create Pokemon object with fetched details
                    val pokemon = Pokemon(name, types, imageUrl)
                    pokemonList.add(pokemon)

                    // Now you can use the extracted data as needed
                    Log.d("Pokemon", "Name: $name, Types: $types, Image URL: $imageUrl")
                } else {
                    Log.e("PokemonAPI", "JSON Response is null")
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.e("Error", "Failed to fetch details for Pokemon: $name")
            }
        }]
    }

}

