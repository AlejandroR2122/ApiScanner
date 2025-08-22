import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.api_scanner.R
import com.example.api_scanner.altaPrinter.VentanaModal
import com.example.api_scanner.connection.ImpresoraData
import com.example.api_scanner.modales.modalSustituirImpresora
import com.google.gson.Gson

class SpinnerAdapter<T>(
    private val context: Context,
    private val spinner: Spinner,
    private val values: List<T>,
    private val nameExtractor: (T) -> String,
    private val idExtractor: (T) -> Int?,
    private val setterModificados: (Int?) -> Unit,
    private val fullObjectSetter: ((T) -> Unit)? = null, // Parámetro opcional para manejar el objeto completo
    private val fragmentManager: androidx.fragment.app.FragmentManager? = null, // Ahora puede ser nulo
    private val impresoraSustituidora:ImpresoraData? = null
) {
    private var isInitialized = false // Bandera para controlar la inicialización

    init {
        setupSpinner()
    }

    // Configurar el Spinner
    private fun setupSpinner() {
        val nombres =
            values.map(nameExtractor).takeIf { it.isNotEmpty() } ?: listOf("No hay resultados")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, nombres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Configuración para abrir el diálogo de búsqueda al tocar el spinner
        spinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showSearchDialog(nombres) { selectedName ->
                    // Establecer el valor seleccionado en el Spinner
                    val selectedIndex = nombres.indexOf(selectedName)
                    if(selectedIndex == 0){
                        
                    }
                    spinner.setSelection(selectedIndex)
                }
            }
            true
        }

        // Configurar el OnItemSelectedListener del Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (!isInitialized || position == 0) {
                    isInitialized = true // Ignora el primer evento al inicializar
                    return
                }

                val selectedItem = values[position]
                val selectedId = idExtractor(selectedItem)
                setterModificados(selectedId)

                fullObjectSetter?.invoke(selectedItem)
                openModalSustituir(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                setterModificados(0)
            }
        }
    }
    // MODAL SUSTITUIR
    fun openModalSustituir(selectedItem: T) {
        val dialog = modalSustituirImpresora()

        // Convertimos el objeto a JSON usando Gson
        val gson = Gson()
        val objetoDataModificatedJson = gson.toJson(selectedItem)
        val objetoImpresoraPincipal = gson.toJson(impresoraSustituidora)

        // Añadimos datos al Bundle
        val bundle = Bundle()
        bundle.putString("dataModificated", objetoDataModificatedJson)
        bundle.putString("ImpresoraPrincipal",objetoImpresoraPincipal)

        // Asignamos el Bundle al dialog
        dialog.arguments = bundle

        // Mostramos el dialog usando el fragmentManager pasado al adaptador
        if(fragmentManager != null){
            dialog.show(fragmentManager, "CustomDialog")
        }
    }


    // Mostrar el diálogo de búsqueda
    private fun showSearchDialog(nombres: List<String>, onItemSelected: (String) -> Unit) {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_spinner, null)
        val searchView = dialogView.findViewById<SearchView>(R.id.searchView)
        val listView = dialogView.findViewById<ListView>(R.id.listView)

        val dialogAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, nombres)
        listView.adapter = dialogAdapter

        val dialog = MaterialDialog(context).show {
            customView(view = dialogView)
        }

        // Filtrar los resultados del SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText.isNullOrEmpty()) {
                    // Si no hay texto, muestra todos los elementos
                    dialogAdapter.filter.filter("")
                } else {
                    dialogAdapter.filter.filter(newText)
                }

                return true
            }
        })

        // Manejar la selección del ListView
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = dialogAdapter.getItem(position) ?: return@setOnItemClickListener
            onItemSelected(selectedItem) // Pasar el valor seleccionado
            dialog.dismiss() // Cerrar el diálogo
        }
    }
}
