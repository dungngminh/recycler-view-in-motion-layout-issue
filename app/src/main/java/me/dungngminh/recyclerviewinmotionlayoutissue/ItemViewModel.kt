package me.dungngminh.recyclerviewinmotionlayoutissue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ItemUiState(
    val isLiked: Boolean = false,
    val selectedIndex: Int = 0,
    val items: List<Item> = emptyList(),
)

class ItemViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ItemUiState())

    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = ItemUiState(
                items = listOf(
                    Item.Interaction(uiState.value.isLiked),
                    Item.TabLayout(0),
                    Item.Content(
                        text = "Lorem ipsum dolor sit amet, consectetur " +
                                "dipiscing elit, sed do eiusmod tempor incididunt ut lab"
                    ),
                    Item.Content(
                        text = "Lorem ipsum dolor sit amet, consectetur " +
                                "dipiscing elit, sed do eiusmod tempor incididunt ut lab"
                    ),
                    Item.Content(
                        text = "Lorem ipsum dolor sit amet, consectetur " +
                                "dipiscing elit, sed do eiusmod tempor incididunt ut lab"
                    ),
                    Item.Content(
                        text = "Lorem ipsum dolor sit amet, consectetur " +
                                "dipiscing elit, sed do eiusmod tempor incididunt ut lab"
                    ),
                    Item.Content(
                        text = "Lorem ipsum dolor sit amet, consectetur " +
                                "dipiscing elit, sed do eiusmod tempor incididunt ut lab"
                    ),
                    Item.Content(
                        text = "Lorem ipsum dolor sit amet, consectetur " +
                                "dipiscing elit, sed do eiusmod tempor incididunt ut lab"
                    ),
                    Item.Content(
                        text = "Lorem ipsum dolor sit amet, consectetur " +
                                "dipiscing elit, sed do eiusmod tempor incididunt ut lab"
                    )

                )
            )
        }
    }

    fun onLikeClick(isLiked: Boolean) {
        val selectedTab = Item.Interaction(isLiked)
        val updatedItems = _uiState.value.items.map {
            if (it is Item.Interaction) {
                selectedTab
            } else {
                it
            }
        }
        _uiState.value = _uiState.value.copy(isLiked = isLiked, items = updatedItems)
    }

    fun onActionTabClick(index: Int) {
        _uiState.value = _uiState.value.copy(selectedIndex = index)
        if (index == 1) {
            dumpVerticalContents(index)
        } else {
            dumpItems(index)
        }

    }

    private fun dumpVerticalContents(index: Int) {
        val dumpVerticalContents = List(10) {
            Item.VerticalContent(text1 = "Vertical Item $it", text2 = "Vertical Item $it")
        }
        val selectedTab = Item.TabLayout(selectedIndex = index)
        val updatedTabItems = _uiState.value.items.map {
            if (it is Item.TabLayout) {
                selectedTab
            } else {
                it
            }
        }
        val itemsNotContent = updatedTabItems.filter { it !is Item.Content }
        _uiState.value = _uiState.value.copy(items = itemsNotContent + dumpVerticalContents)
    }

    private fun dumpItems(index: Int) {
        val dummyContents = List(10) {
            Item.Content(text = "Item $it")
        }
        val selectedTab = Item.TabLayout(selectedIndex = index)
        val updatedTabItems = _uiState.value.items.map {
            if (it is Item.TabLayout) {
                selectedTab
            } else {
                it
            }
        }
        val itemsNotVerticalContent = updatedTabItems.filter { it !is Item.VerticalContent }
        _uiState.value = _uiState.value.copy(items = itemsNotVerticalContent + dummyContents)
    }
}