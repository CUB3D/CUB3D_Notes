package pw.cub3d.cub3_notes.ui.home

import androidx.recyclerview.selection.ItemDetailsLookup

interface ProvidesItemDetails {
    fun getItemDetails(key: Long): ItemDetailsLookup.ItemDetails<Long>
    fun getItemPosition(): Int
}
