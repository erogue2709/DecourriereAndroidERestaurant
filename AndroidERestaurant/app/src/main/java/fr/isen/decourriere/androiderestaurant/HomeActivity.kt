package fr.isen.decourriere.androiderestaurant

import android.content.Intent
import android.os.Bundle
import fr.isen.decourriere.androiderestaurant.category.CategoryActivity
import fr.isen.decourriere.androiderestaurant.category.ItemType
import fr.isen.decourriere.androiderestaurant.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.starter.setOnClickListener {
            //startCategoryActivity(binding.starter.text.toString())
            statCategoryActivity(ItemType.STARTER)
        }

        binding.mainDish.setOnClickListener {
            //startCategoryActivity(binding.mainDish.text.toString())
            statCategoryActivity(ItemType.MAIN)
        }

        binding.dessert.setOnClickListener {
            //startCategoryActivity(binding.dessert.text.toString())
            statCategoryActivity(ItemType.DESSERT)
        }
    }

    /*
    private fun startCategoryActivity(item: String) {
        Toast.makeText(this, item, Toast.LENGTH_LONG).show()
        startActivity(Intent(this, CategoryActivity::class.java).putExtra(CATEGORY_NAME, item))
    }
     */
    private fun statCategoryActivity(item: ItemType) {
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra(CATEGORY_NAME, item)
        startActivity(intent)
    }

    companion object {
        const val CATEGORY_NAME = "CATEGORY_NAME"
    }
}