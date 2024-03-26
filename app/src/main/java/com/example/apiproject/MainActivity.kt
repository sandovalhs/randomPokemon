package com.example.apiproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class MainActivity : AppCompatActivity() {
    var imageUrl = ""
    var name = ""
    var types = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val pokemonName = findViewById<TextView>(R.id.pokename)
        val pokemonType = findViewById<TextView>(R.id.poketype)
        getNextImage(button,imageView, pokemonName, pokemonType)



    }

    private fun pokemonAPI() {
        val client = AsyncHttpClient()
        client["https://pokeapi.co/api/v2/pokemon/${(1..898).random()}", object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JsonHttpResponseHandler.JSON
            ) {
                Log.d("Success", "response successful")
                name = json.jsonObject.getString("name")
                val typesArray = json.jsonObject.getJSONArray("types")
                    types = mutableListOf<String>()
                for (i in 0 until typesArray.length()) {
                    val typeObject = typesArray.getJSONObject(i).getJSONObject("type")
                    types.add(typeObject.getString("name"))
                }

                val spritesObject = json.jsonObject.getJSONObject("sprites")
                imageUrl = spritesObject.getString("front_default")


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
    private fun getNextImage(button: Button, imageView: ImageView, pokemonName: TextView, pokemonType: TextView) {

        button.setOnClickListener {
            pokemonAPI()

            pokemonName.text = "$name"
            pokemonType.text = "Type(s): ${types.joinToString(", ")}"
            Glide.with(this)
                .load(imageUrl)
                .fitCenter()
                .into(imageView)
        }
    }}