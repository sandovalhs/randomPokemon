package com.example.apiproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    var name = ""

    private lateinit var rvPokemon : RecyclerView
    private val adapter = PokemonAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvPokemon = findViewById(R.id.pokemon_list)
        rvPokemon.adapter = adapter
        rvPokemon.layoutManager = LinearLayoutManager(this@MainActivity)

        pokemonAPI()

    }

    private fun pokemonAPI() {
        val client = AsyncHttpClient()
        val randomOffset = (0..898).random()
        val url = "https://pokeapi.co/api/v2/pokemon?limit=20&offset=$randomOffset"
        client[url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON?
            ) {
                if (json != null) {
                    Log.d("PokemonAPI", "JSON Response: $json")
                    // Check if "results" array exists in the JSON response
                    if (json.jsonObject.has("results")) {
                        val resultsArray = json.jsonObject.optJSONArray("results")
                        if (resultsArray != null) {
                            Log.d("PokemonAPI", "Results Array: $resultsArray")
                            processPokemonData(resultsArray)
                            Log.d("Success", "response successful")
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
    private fun processPokemonData(jsonArray: JSONArray) {
        // Process the JSON array containing Pokémon data here
        for (i in 0 until jsonArray.length()) {
            val pokemonObject = jsonArray.getJSONObject(i)
            val name = pokemonObject.getString("name")
            val url = pokemonObject.getString("url")

            // Fetch additional details using the URL
            fetchPokemonDetails(name, url)
        }
    }

    private fun fetchPokemonDetails(name: String, url: String) {
        val client = AsyncHttpClient()
        client[url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                if (json != null) {
                    val types = mutableListOf<String>()
                    val spritesObject = json.jsonObject.getJSONObject("sprites")
                    val imageUrl = spritesObject.getString("front_default")

                    // Check if "types" field exists in the JSON response
                    if (json.jsonObject.has("types")) {
                        val typesArray = json.jsonObject.getJSONArray("types")
                        for (j in 0 until typesArray.length()) {
                            val typeObject = typesArray.getJSONObject(j).getJSONObject("type")
                            types.add(typeObject.getString("name"))
                        }
                    } else {
                        Log.e("PokemonAPI", "Types field not found for Pokemon: $name")
                    }

                    // Create Pokemon object with fetched details
                    val pokemon = Pokemon(name, types, imageUrl)
                    adapter.addPokemon(pokemon)

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

