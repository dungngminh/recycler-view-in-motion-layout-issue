package me.dungngminh.recyclerviewinmotionlayoutissue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import me.dungngminh.recyclerviewinmotionlayoutissue.databinding.ItemContentBinding
import me.dungngminh.recyclerviewinmotionlayoutissue.databinding.ItemInteractionBinding
import me.dungngminh.recyclerviewinmotionlayoutissue.databinding.ItemTabLayoutBinding
import me.dungngminh.recyclerviewinmotionlayoutissue.databinding.ItemVerticalContentBinding

sealed interface ItemChangedPayload {
    data class LikeButton(val isLiked: Boolean) : ItemChangedPayload
    data class ActionTab(val selectedIndex: Int) : ItemChangedPayload
}

class ItemAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Item, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return when {
                oldItem is Item.Content && newItem is Item.Content -> oldItem.id == newItem.id
                oldItem is Item.VerticalContent && newItem is Item.VerticalContent -> oldItem.id == newItem.id
                oldItem is Item.TabLayout && newItem is Item.TabLayout -> true
                oldItem is Item.Interaction && newItem is Item.Interaction -> true
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return when {
                oldItem is Item.Content && newItem is Item.Content -> oldItem.text == newItem.text
                oldItem is Item.VerticalContent && newItem is Item.VerticalContent -> oldItem.text1 == newItem.text1 && oldItem.text2 == newItem.text2
                oldItem is Item.TabLayout && newItem is Item.TabLayout -> oldItem.selectedIndex == newItem.selectedIndex
                oldItem is Item.Interaction && newItem is Item.Interaction -> oldItem.isLiked == newItem.isLiked
                else -> false

            }
        }

        override fun getChangePayload(oldItem: Item, newItem: Item): Any? {
            return when {
                oldItem is Item.Interaction && newItem is Item.Interaction -> {
                    if (oldItem.isLiked != newItem.isLiked) {
                        ItemChangedPayload.LikeButton(newItem.isLiked)
                    } else {
                        null
                    }
                }

                oldItem is Item.TabLayout && newItem is Item.TabLayout -> {
                    if (oldItem.selectedIndex != newItem.selectedIndex) {
                        ItemChangedPayload.ActionTab(newItem.selectedIndex)
                    } else {
                        null
                    }
                }

                else -> null
            }
        }
    }) {

    // View Holder for item_content
    inner class ContentViewHolder(private val binding: ItemContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item.Content) {
            binding.textView.text = item.text
        }
    }

    // View Holder for item_vertical_content

    inner class VerticalContentViewHolder(private val binding: ItemVerticalContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item.VerticalContent) {
            binding.text1.text = item.text1
            binding.text2.text = item.text2
        }
    }

    // View Holder for item_action_buttons

    inner class TabLayoutViewHolder(private val binding: ItemTabLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var selectedIndex = 0

        init {
            binding.tlAction.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        if (tab == null || selectedIndex == tab.position) return
                        onClickListener.onActionTabClick(tab.position)
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

                    override fun onTabReselected(tab: TabLayout.Tab?) = Unit
                },
            )
        }

        fun bind(item: Item.TabLayout) {
            selectedIndex = item.selectedIndex
            binding.tlAction.removeAllTabs()
            repeat(3) {
                binding.tlAction.addTab(
                    binding.tlAction.newTab().setText("Tab $it"), it == item.selectedIndex
                )
            }
        }

        fun bindSelected(index: Int) {
            binding.tlAction.getTabAt(index)
                ?.select()
                .also {
                    selectedIndex = index
                }
        }
    }

    // View Holder for item_interaction

    inner class InteractionViewHolder(private val binding: ItemInteractionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item.Interaction) {
            bindLikeButtonState(item.isLiked)
            binding.button.setOnClickListener {
                onClickListener.onLikeClick(!binding.button.isSelected)
            }
        }

        fun bindLikeButtonState(isLiked: Boolean) {
            binding.button.isSelected = isLiked
            binding.button.text = if (isLiked) "Liked" else "Like"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> InteractionViewHolder(
                ItemInteractionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            1 -> TabLayoutViewHolder(
                ItemTabLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            2 -> ContentViewHolder(
                ItemContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            3 -> VerticalContentViewHolder(
                ItemVerticalContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> {
                holder.bind(getItem(position) as Item.Content)
            }

            is VerticalContentViewHolder -> {
                holder.bind(getItem(position) as Item.VerticalContent)
            }

            is TabLayoutViewHolder -> {
                holder.bind(getItem(position) as Item.TabLayout)
            }

            is InteractionViewHolder -> {
                holder.bind(getItem(position) as Item.Interaction)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.Interaction -> 0
            is Item.TabLayout -> 1
            is Item.Content -> 2
            is Item.VerticalContent -> 3
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        when (val latestPayload = payloads.lastOrNull()) {
            is ItemChangedPayload.LikeButton -> {
                (holder as? InteractionViewHolder)?.bindLikeButtonState(latestPayload.isLiked)
            }

            is ItemChangedPayload.ActionTab -> {
                (holder as? TabLayoutViewHolder)?.bindSelected(latestPayload.selectedIndex)
            }

            else -> super.onBindViewHolder(holder, position, payloads)
        }
    }
}

interface OnClickListener {
    fun onActionTabClick(index: Int)

    fun onLikeClick(isLiked: Boolean)
}