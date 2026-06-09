package com.curso.banca.ui.beneficiarios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.curso.banca.data.model.Beneficiary
import com.curso.banca.databinding.ItemBeneficiarioBinding
import com.curso.banca.util.maskAccount

enum class BeneficiarioMode { CRUD, SELECT }

class BeneficiarioAdapter(
    private val mode: BeneficiarioMode,
    private val onClick: (Beneficiary) -> Unit,
    private val onMore: (View, Beneficiary) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<BeneficiarioAdapter.ViewHolder>() {
    private val allItems = mutableListOf<Beneficiary>()
    private val visibleItems = mutableListOf<Beneficiary>()

    fun submitList(items: List<Beneficiary>) {
        allItems.clear()
        allItems.addAll(items)
        filter("")
    }

    fun filter(query: String): Int {
        visibleItems.clear()
        val q = query.trim().lowercase()
        visibleItems.addAll(
            if (q.isBlank()) allItems else allItems.filter {
                it.fullName.lowercase().contains(q) || it.alias.lowercase().contains(q) || it.accountNumber.contains(q)
            }
        )
        notifyDataSetChanged()
        return visibleItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBeneficiarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(visibleItems[position])
    override fun getItemCount(): Int = visibleItems.size

    inner class ViewHolder(private val binding: ItemBeneficiarioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Beneficiary) {
            binding.imgAvatar.text = initials(item)
            binding.tvNombre.text = item.fullName.ifBlank { item.alias }
            binding.tvBanco.text = "${item.alias} · ${item.accountNumber.maskAccount()}"
            binding.btnMore.visibility = if (mode == BeneficiarioMode.CRUD) View.VISIBLE else View.GONE
            binding.root.setOnClickListener { onClick(item) }
            binding.btnMore.setOnClickListener { onMore(binding.btnMore, item) }
        }

        private fun initials(item: Beneficiary): String {
            val first = item.name.firstOrNull()?.uppercaseChar()?.toString().orEmpty()
            val second = item.lastName.firstOrNull()?.uppercaseChar()?.toString().orEmpty()
            return (first + second).ifBlank { "B" }
        }
    }
}
