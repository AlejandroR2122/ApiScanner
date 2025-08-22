package com.example.api_scanner

import ViewPagerAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.api_scanner.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navegador: NavController   // Creamos un nav controller que nos servira para controlar los fragments
    private lateinit var viewPager: ViewPager2   // Creamos viewPager, para hacer scroll horizontal y navegar por el nav
    private lateinit var binding: ActivityMainBinding  // Creamos el binding para utilizar las vistas de forma mas eficiente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater) // Inflamos la vista
        setContentView(binding.root)                          // Establecer el diseño principal de forma mas segura y eficiente

        setSupportActionBar(binding.toolbar)                  // Establezco mi toolbar

        SetupViewPager()     // Gestor de paginacion, lo iniciamos
        driveNavigation()
        setupBottomNavigation()

        // Agregar el OnPageChangeCallback
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // Al hacer scroll si quiero que se ejecute algo lo añadiria aqui
            }

            override fun onPageSelected(position: Int) {

                // Sincronizar la selección del BottomNavigationView con la página del ViewPager
                val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)   // Extraigo el bottomnavigation del nav
                bottomNavigationView.selectedItemId = when (position) {     // Cuando cambia de posicion, navega hasta el fragment y devuelve el btn seleccionado
                    0 -> {
                        // Navegar al fragmento correspondiente (home)
                        navegador.navigate(R.id.fragment_home)
                        R.id.btnHome  // Devuelve btn seleccionado
                    }
                    1 -> {
                        // Navegar al fragmento correspondiente (details)
                        navegador.navigate(R.id.fragment_details)
                        R.id.btnImpresora
                    }
                    2 -> {
                        // Navegar al fragmento correspondiente (alta printer)
                        navegador.navigate(R.id.fragmentAltaPrinter)
                        R.id.btnDarAlta
                    }
                    3 -> {
                        // Navegar al fragmento correspondiente (otra página)
                        navegador.navigate(R.id.fragmentSettings)
                        R.id.btnSettings
                    }

                    else -> {
                        // En caso de que haya más fragmentos, se puede añadir otro bloque
                        R.id.btnHome  // Default
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
               // Vacia
            }
        }) // Cierra el bloque aquí

    }

    /*
     *  El navegador,por cada indice de los botones se navega a un fragment especifico
     *  El viewPager es el encargado de gestionar los fragments
     */
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)  // Obtengo el bottomnavigation del nav
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->  // Al seleccionar uno, se navega al fragment segun su id
            when (item.itemId) {
                R.id.btnHome -> {
                    viewPager.currentItem = 0
                    true
                }

                R.id.btnImpresora -> {
                    viewPager.currentItem = 1
                    true
                }

                R.id.btnDarAlta -> {
                    viewPager.currentItem = 2
                    true
                }
                // Cambiar a 2 si solo tienes 3 fragmentos
                R.id.btnSettings -> {
                    viewPager.currentItem = 3
                    true
                }


                else -> false
            }
        }
    }

    private fun SetupViewPager() {
        viewPager = findViewById(R.id.viewPager)        // Buscamos el widget ViewPager2, gestiona los fragments
        val adapter = ViewPagerAdapter(this) // Creamos el adaptador del view
        viewPager.adapter = adapter                     // Añadimos al widget el adaptador
    }

    private fun driveNavigation() {
        //El navHostFragment gestiona el la navegacion en la aplicacion
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController       // navController es el que maneja la navegacion entre fragmentos
        navegador = navController

    }

    override fun onSupportNavigateUp(): Boolean {
        // metodo que forma parte de la clase AppCompatActivity se ejecuta cuando retrocedes "flecha atras"
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
    }


}