package com.example.api_scanner.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.api_scanner.MainLogin
import com.example.api_scanner.R
import android.app.AlertDialog
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class FragmentSettings : Fragment() {
    private lateinit var btnLogOut:Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)


        btnLogOut = view.findViewById(R.id.btnLogOut)


        setName(view)
        logOutModal()
        return view

    }

    fun logOutModal(){

        btnLogOut.setOnClickListener {
           showAceptModal()
       }
    }

    fun showAceptModal(){
        // Construcción del modal
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Cerrar sesión")
        builder.setMessage("¿ Estas seguro ?")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            val intent = Intent(context, MainLogin::class.java)
            startActivity(intent) // Usa startActivityForResult
            val MainLogin=MainLogin()
            MainLogin.setUser("")
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss() // También cierra el modal
        }
        val dialog = builder.create()
        dialog.setOnShowListener {
            // Cambiar el color del texto de los botones
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(resources.getColor(R.color.colorAccept))

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setTextColor(resources.getColor(R.color.red))

                // Ajustar márgenes del botón
                val params = layoutParams as ViewGroup.MarginLayoutParams
                params.marginEnd = resources.getDimensionPixelSize(R.dimen.margin_12dp) // Define el margen derecho
                layoutParams = params
            }
        }

        // Mostrar el modal
        dialog.show()

    }
    fun setName(view: View) {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "Nombre no guardado")
        val nameTextView = view.findViewById<TextView>(R.id.username)
        nameTextView?.text = userName
    }
}