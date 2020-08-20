package com.example.newswavtest.ui.gists_list

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newswavtest.databinding.ItemGistBinding
import com.example.newswavtest.db.entity.GistListEntity
import com.example.newswavtest.ui.gist_detail.GistDetailActivity
import com.example.newswavtest.utils.EXTRA_GIST_ID

class GistsListAdapter :
    PagedListAdapter<GistListEntity, RecyclerView.ViewHolder>(GistsListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GistsViewHolder(
            ItemGistBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as GistsViewHolder).bind(item)
    }

    class GistsViewHolder(
        private val binding: ItemGistBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GistListEntity?) {
            binding.apply {
                this.item = item
                executePendingBindings()
            }

            itemView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, GistDetailActivity::class.java)
                intent.putExtra(EXTRA_GIST_ID, item?.id)

                context.startActivity(intent)
            }
        }
    }
}

private class GistsListDiffCallback : DiffUtil.ItemCallback<GistListEntity>() {
    override fun areItemsTheSame(oldItem: GistListEntity, newItem: GistListEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GistListEntity, newItem: GistListEntity): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.created_at == newItem.created_at &&
                oldItem.updated_at == newItem.updated_at
    }
}