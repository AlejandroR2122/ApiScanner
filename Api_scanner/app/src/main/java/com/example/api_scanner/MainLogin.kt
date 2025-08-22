package com.example.api_scanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.api_scanner.connection.connections
import com.example.api_scanner.databinding.ActivityMainLoginBinding

class MainLogin : AppCompatActivity() {
    private lateinit var user: String
    private lateinit var password: String

    private lateinit var binding: ActivityMainLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Creamos el binding para manejar la vista de forma sencilla
        binding = ActivityMainLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bloquear botón atrás
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // No hacer nada
            }
        })
        //Definir btn login
        binding.loginButton.setOnClickListener {
            getCredenciales()
           // val intent = Intent(this, MainActivity::class.java)
           // startActivity(intent)

        }
    }

    /*
     * Eliminar ifEmpty{"A"} en  pruebas y produccion
     *
     */
    fun getCredenciales() {
        //Obtengo valor del campo username
        user = binding.usernameLogin.text.toString().ifEmpty { "A" }
        setUser(user)
        //Obtengo valor del campo passwrod
        val password = binding.passwordLogin.text.toString()
        // hago peticion de login
        connectionLoginUser("RodrigoAlejandro55y", "Armilla13$")
        // Paso la variable userName
        val sharedPref = getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        sharedPref.edit().putString("user_name", user).apply()

    }

    // Modificia el username
    fun setUser(newUser: String) {
        user = newUser
    }

    // creamos la funcion que hara la peticion
    fun connectionLoginUser(username: String, password: String) {
        val connections = connections() // Instancia de tu clase de conexión

        println("CHEEEE " + username)
        println("CHEEEE " + password)
          connections.connectionLogin(
              username,
              password,
              onResponse = { apiResponse ->
                  if (apiResponse.success) {
                      println(apiResponse.accessToken)
                      println(apiResponse.refreshToken)
                      if(apiResponse.accessToken != null && apiResponse.refreshToken != null){
                          saveTokens( apiResponse.accessToken,apiResponse.refreshToken)
                          // Si la respuesta es ok, nos envia al MainActivity
                          val intent = Intent(this, MainActivity::class.java)
                          startActivity(intent)
                          Toast.makeText(this, "Autentificacion correcta ", Toast.LENGTH_LONG)
                              .show()
                      }else{
                          Toast.makeText(this, "Token no guardado correctamente, credenciales correctas", Toast.LENGTH_LONG).show()
                          val intent = Intent(this, MainActivity::class.java)
                          startActivity(intent)
                      }



                  } else {
                      Toast.makeText(this, "Error en la autentificacion", Toast.LENGTH_LONG)
                          .show()
                  }
              },
              onFailure = { throwable ->
                  Toast.makeText(this, "Error de red", Toast.LENGTH_LONG)
                      .show()
              }
          )


    }
    // Guardar el token
    fun saveTokens(accessToken: String, refreshToken: String) {
        val sharedPreferences = this.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
            apply()
        }
    }

    // Recuperar el token
    fun getSecureData() {
        val sharedPreferences = this.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
    println("accesToken " +accessToken)
    }
}
