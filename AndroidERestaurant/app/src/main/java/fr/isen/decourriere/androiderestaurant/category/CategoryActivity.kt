package fr.isen.decourriere.androiderestaurant.category

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isen_2021.category.CategoryAdapter
import fr.isen.decourriere.androiderestaurant.databinding.ActivityCategoryBinding
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import fr.isen.decourriere.androiderestaurant.network.Dish
import fr.isen.decourriere.androiderestaurant.network.MenuResult
import fr.isen.decourriere.androiderestaurant.network.NetworkConstant
import org.json.JSONObject
import android.content.Context
import android.content.Intent
import fr.isen.decourriere.androiderestaurant.*
import fr.isen.decourriere.androiderestaurant.detail.DetailActivity
import fr.isen.decourriere.androiderestaurant.util.Loader

enum class ItemType {
    STARTER, MAIN, DESSERT;

    companion object {
        fun categoryTitle(item: ItemType?) : String {
            return when(item) {
                STARTER -> "Entrées"
                MAIN -> "Plats"
                DESSERT -> "Desserts"
                else -> ""
            }
        }
    }
}


class CategoryActivity : BaseActivity() {

    private lateinit var binding: ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedItem = intent.getSerializableExtra(HomeActivity.CATEGORY_NAME) as? ItemType
        title = getCategoryTitle(selectedItem)

        binding.swipeLayout.setOnRefreshListener {
            resetCache()
            makeRequest(selectedItem)
        }

        loadList(listOf<Dish>())


        makeRequest(selectedItem)
    }

    private fun getCategoryTitle(item: ItemType?): String {
        return when (item) {
            ItemType.STARTER -> getString(
                R.string.starter
            )
            ItemType.MAIN -> getString(
                R.string.mainDish
            )
            ItemType.DESSERT -> getString(
                R.string.dessert
            )
            else -> ""
        }
    }

    private fun makeRequest(selectedItem: ItemType?) {
        resultFromCache()?.let {
            parseResult(it, selectedItem)
        } ?: run {
            val loader = Loader()
            loader.show(this, "récupération du menu")
            val queue = Volley.newRequestQueue(this)
            val url = NetworkConstant.BASE_URL + NetworkConstant.PATH_MENU

            val jsonData = JSONObject()
            jsonData.put(NetworkConstant.ID_SHOP, "1")

            var request = JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonData,
                { response ->
                    loader.hide(this)
                    Log.d("request", response.toString(2))
                    binding.swipeLayout.isRefreshing = false
                    cacheResult(response.toString())
                    parseResult(response.toString(), selectedItem)
                },
                { error ->
                    loader.hide(this)
                    binding.swipeLayout.isRefreshing = false
                    error.message?.let {
                        Log.d("request", it)
                    } ?: run {
                        Log.d("request", error.toString())
                    }
                }
            )
            queue.add(request)
        }
    }

    private fun cacheResult(response: String) {
        val sharedPreferences = getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(REQUEST_CACHE, response)
        editor.apply()
    }

    private fun resultFromCache(): String? {
        val sharedPreferences = getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(REQUEST_CACHE, null)
    }

    private fun parseResult(response: String, selectedItem: ItemType?) {
        val menuResult = GsonBuilder().create().fromJson(response, MenuResult::class.java)
        val items = menuResult.data.firstOrNull { it.name == ItemType.categoryTitle(selectedItem)}
        loadList(items?.items)
    }

    private fun resetCache() {
        val sharedPreferences = getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(REQUEST_CACHE)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("myLog", "CategoryActivity destroyed")
    }


    override fun onResume() {
        super.onResume()
        Log.d("lifecycle", "onResume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("lifecycle", "onRestart")
    }

    private fun loadList(dishes: List<Dish>?) {
        dishes?.let {
            val adapter = CategoryAdapter(it) { dish ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra(DetailActivity.DISH_EXTRA, dish)
                startActivity(intent)
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }
    }

    companion object {
        const val USER_PREFERENCES_NAME = "USER_PREFERENCES_NAME"
        const val REQUEST_CACHE = "REQUEST_CACHE"
    }
}