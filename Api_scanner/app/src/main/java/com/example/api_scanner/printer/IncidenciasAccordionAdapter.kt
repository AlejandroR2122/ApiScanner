package com.example.api_scanner.printer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.api_scanner.R
import com.example.api_scanner.connection.DataIncidencia
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IncidenciasAccordionAdapter(private val incidencias: MutableList<DataIncidencia>) :
    RecyclerView.Adapter<IncidenciasAccordionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvDetalles: TextView = itemView.findViewById(R.id.tvDetalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incidencia, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val incidencia = incidencias[position]
        val timeIncidencia= changeTime(incidencia.fechaAveria)
        // Formatear los datos para el título y detalles
        val titulo = "Fecha Averia: ${timeIncidencia}"
        val detalles = """
            
            Usuario Técnico: ${incidencia.usuarioTecnico}
            Motivo de Avería: ${incidencia.motivoAveria}
            Copias Entrada: ${incidencia.copiasEntradaAveria ?: "N/A"}
            Copias Salida: ${incidencia. copiasSalidaAveria?: "N/A"}
            Incidencia: ${incidencia.incidencia ?: "N/A"}
        """.trimIndent()

        // Asignar valores a los TextViews
        holder.tvTitulo.text = titulo
        holder.tvDetalles.text = detalles
    }

    fun changeTime(milisegundosTotales: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val date = Date(milisegundosTotales)
        return dateFormat.format(date)
    }
    override fun getItemCount() = incidencias.size
}
