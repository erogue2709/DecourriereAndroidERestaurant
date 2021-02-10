package fr.isen.decourriere.androiderestaurant.basket

import android.content.Context
import com.google.gson.GsonBuilder
import fr.isen.decourriere.androiderestaurant.network.Dish
import java.io.File
import java.io.Serializable


class Basket (val items: MutableList<BasketItem>): Serializable {

    var itemsCount: Int = 0
        get() {
            return if(items.count() > 0) {
                items
                    .map { it.count}
                    .reduce { acc, i -> acc + i}
            }else{
                0
            }
        }

    fun addItem(item: BasketItem) {
        val existingItem = items.firstOrNull {
            it.dish.name == item.dish.name
        }
        existingItem?.let {
            existingItem.count += item.count
        } ?: run {
            items.add(item)
        }
    }

    fun save(context: Context) {
        val jsonFile = File(context.cacheDir.absolutePath + BASKET_FILE)
        jsonFile.writeText(GsonBuilder().create().toJson(this))
        updateCounter(context)
    }

    private fun updateCounter(context: Context) {
        val sharedPreferences = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(ITEMS_COUNT, itemsCount)
        editor.apply()
    }

    companion object {
        fun getBasket(context: Context): Basket {
            val jsonFile = File(context.cacheDir.absolutePath + BASKET_FILE)
            return if(jsonFile.exists()) {
                val json = jsonFile.readText()
                GsonBuilder().create().fromJson(json, Basket::class.java)
            } else {
                Basket(mutableListOf())
            }
        }

        const val BASKET_FILE = "basket.json"
        const val ITEMS_COUNT = "ITEMS_COUNT"
        const val USER_PREFERENCES_NAME = "USER_PREFERENCES_NAME"
    }
}

class BasketItem(val dish: Dish, var count: Int): Serializable {}