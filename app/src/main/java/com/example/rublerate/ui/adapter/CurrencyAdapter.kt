package com.example.rublerate.ui.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rublerate.R
import com.example.rublerate.data.Currency
import com.example.rublerate.databinding.ItemCurrencyBinding
import com.example.rublerate.util.Constants
import java.util.Locale

/**
 * Adapter for displaying a list of currencies in a RecyclerView.
 */
class CurrencyAdapter(private var currencyList: List<Currency>) :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        // Inflate the layout for each item
        val binding = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        // Bind data to the view holder
        val currency = currencyList[position]
        holder.bind(currency)
    }

    override fun getItemCount(): Int = currencyList.size

    inner class CurrencyViewHolder(private val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currency: Currency) {
            // Bind currency data to views
            binding.nominalTextView.text = currency.nominal.toInt().toString()
            binding.charCodeTextView.text = currency.charCode
            binding.nameTextView.text = currency.name
            binding.valueTextView.text = String.format(Locale.getDefault(), Constants.VALUE_FORMAT, currency.value)

            // Calculate difference between current and previous values
            val difference = currency.value - currency.previous

            // Format difference text
            val formattedDifference = String.format(Locale.getDefault(), Constants.DIFFERENCE_FORMAT, if (difference >= 0) "+" else "", difference)
            binding.differenceTextView.text = formattedDifference

            // Set text color based on difference
            val attributeResId = if (difference >= 0) R.attr.differencePositiveColor else R.attr.differenceNegativeColor
            val typedValue = TypedValue()
            itemView.context.theme.resolveAttribute(attributeResId, typedValue, true)
            val color = typedValue.data
            binding.differenceTextView.setTextColor(color)
        }
    }

    // Update adapter data with new list and calculate diff
    fun updateData(newCurrencyList: List<Currency>) {
        val diffResult = DiffUtil.calculateDiff(CurrencyDiffCallback(currencyList, newCurrencyList))
        currencyList = newCurrencyList
        diffResult.dispatchUpdatesTo(this)
    }

    // Diff callback for RecyclerView
    private class CurrencyDiffCallback(
        private val oldList: List<Currency>,
        private val newList: List<Currency>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        // Check if items are the same
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        // Check if contents are the same
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
