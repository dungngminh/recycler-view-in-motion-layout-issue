package me.dungngminh.recyclerviewinmotionlayoutissue

import java.util.UUID

sealed class Item {
    data class Interaction(
        val isLiked: Boolean,
    ) : Item()

    data class TabLayout(val selectedIndex: Int) : Item()

    data class Content(
        val id: String = UUID.randomUUID().toString(),
        val text: String,
    ) : Item()

    data class VerticalContent(
        val id: String = UUID.randomUUID().toString(),
        val text1: String,
        val text2: String,
    ) : Item()
}