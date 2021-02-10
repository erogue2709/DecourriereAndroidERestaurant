package fr.isen.decourriere.androiderestaurant.util

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import fr.isen.decourriere.androiderestaurant.databinding.ViewLoaderBinding

class Loader {

    lateinit var binding: ViewLoaderBinding

    fun show(context: Context, caption: String) {
        val inflater = LayoutInflater.from(context)
        binding = ViewLoaderBinding.inflate(inflater)
        binding.loaderCaption.text = caption

        val activity = context as? Activity
        val view = activity?.window?.peekDecorView() as? ViewGroup

        activity?.runOnUiThread {
            view?.addView(binding.root)
        }
    }

    fun hide(context: Context) {
        val activity = context as? Activity
        val view = activity?.window?.peekDecorView() as? ViewGroup
        activity?.runOnUiThread {
            view?.removeView(binding.root)
        }
    }
}