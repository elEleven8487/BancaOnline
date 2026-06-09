package com.curso.banca.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.curso.banca.R
import com.curso.banca.data.model.BankTransaction
import com.curso.banca.databinding.ItemMovimientoBinding
import com.curso.banca.util.toCurrencyMXN
import java.text.SimpleDateFormat
import java.util.Locale

class MovimientoAdapter : RecyclerView.Adapter<MovimientoAdapter.ViewHolder>() {
    private val items = mutableListOf<BankTransaction>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))

    fun submitList(newItems: List<BankTransaction>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovimientoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemMovimientoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BankTransaction) {
            val incoming = item.direction == "in"
            binding.tvDescripcion.text = item.description.ifBlank { if (incoming) "Depósito recibido" else "Transferencia enviada" }
            binding.tvFecha.text = item.date?.toDate()?.let { dateFormat.format(it) }.orEmpty()
            binding.tvMonto.text = (if (incoming) "+" else "-") + item.amount.toCurrencyMXN()
            binding.tvMonto.setTextColor(
                ContextCompat.getColor(binding.root.context, if (incoming) R.color.success else R.color.danger_text)
            )
        }
    }
}
