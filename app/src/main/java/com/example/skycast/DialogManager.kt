package com.example.skycast

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import java.util.*


object DialogManager {
    val language = Locale.getDefault().getLanguage()
    fun locationSettingsDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.location)
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _,_ ->
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _,_ ->
            listener.onClickNot()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun permissionDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.location)
        dialog.setMessage(if (language == "ru") "Разрешение надо для определения вашего местоположения," +
                " иначе по умолчанию будет выставлен город Москва" else "Permission is needed to determine your location," +
                " otherwise the city of Moscow will be set by default")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _,_ ->
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _,_ ->
            listener.onClickNot()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun searchByNameDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val name = EditText(context)
        builder.setView(name)
        val dialog = builder.create()
        dialog.setTitle(R.string.cityName)
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _,_ ->
            listener.onClick(name.text.toString())
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener {
        fun onClick(name: String?)
        fun onClickNot()
    }
}