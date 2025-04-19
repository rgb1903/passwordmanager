package com.example.passwordmanager.presentation.passwords.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.databinding.ItemPasswordBinding
import com.example.passwordmanager.domain.model.Password

class PasswordAdapter(
    private val onPasswordClick: (Password) -> Unit,
    private val onDeleteClick: (Password) -> Unit
) : ListAdapter<Password, PasswordAdapter.PasswordViewHolder>(PasswordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val binding = ItemPasswordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PasswordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PasswordViewHolder(
        private val binding: ItemPasswordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPasswordClick(getItem(position))
                }
            }

            binding.buttonDelete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(getItem(position))
                }
            }
        }

        fun bind(password: Password) {
            binding.apply {
                textTitle.text = password.title
                textUsername.text = password.username
                textPassword.text = "••••••••"
            }
        }
    }

    private class PasswordDiffCallback : DiffUtil.ItemCallback<Password>() {
        override fun areItemsTheSame(oldItem: Password, newItem: Password): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Password, newItem: Password): Boolean {
            return oldItem == newItem
        }
    }
}