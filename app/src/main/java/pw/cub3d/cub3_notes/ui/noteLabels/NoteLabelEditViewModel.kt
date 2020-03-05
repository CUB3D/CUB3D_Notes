package pw.cub3d.cub3_notes.ui.noteLabels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.database.dao.LabelDao

class NoteLabelEditViewModel(
    private val labelDao: LabelDao
): ViewModel() {
    val searchText = MutableLiveData<String>("")
    val labels = Transformations.map(searchText) {
        labelDao.findByTitle("%$it%")
    }
}