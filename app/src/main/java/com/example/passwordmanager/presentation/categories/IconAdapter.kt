package com.example.passwordmanager.presentation.categories

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.databinding.ItemIconBinding
import com.google.android.material.R as MaterialR

class IconAdapter(
    private val icons: List<Int>,
    private val onIconSelected: (Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val binding = ItemIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IconViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val iconResId = icons[position]
        holder.bind(iconResId, position == selectedPosition)
    }

    override fun getItemCount(): Int = icons.size

    inner class IconViewHolder(
        private val binding: ItemIconBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(iconResId: Int, isSelected: Boolean) {
            binding.iconImageView.setImageResource(iconResId)
            binding.cardView.strokeWidth = if (isSelected) 3 else 0
            binding.cardView.setStrokeColor(
                if (isSelected) {
                    val typedValue = TypedValue()
                    binding.root.context.theme.resolveAttribute(
                        MaterialR.attr.colorAccent,
                        typedValue,
                        true
                    )
                    ColorStateList.valueOf(typedValue.data)
                } else {
                    null
                }
            )

            val scale = if (isSelected) 1.1f else 1.0f
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(binding.cardView, "scaleX", scale),
                    ObjectAnimator.ofFloat(binding.cardView, "scaleY", scale)
                )
                duration = 150
                start()
            }

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onIconSelected(iconResId)
            }
        }
    }
}