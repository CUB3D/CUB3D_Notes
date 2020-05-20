package pw.cub3d.cub3_notes.ui.labelEdit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.core.database.dao.LabelDao
import pw.cub3d.cub3_notes.core.database.entity.Label

class LabelEditViewModel @Inject constructor(
    private val labelDao: LabelDao
) : ViewModel() {
    val labels = labelDao.getAll()

    var newLabelName = MutableLiveData<String>("")

    fun saveNewLabel(colour: String?) {
        println("Saving: $newLabelName")
        GlobalScope.launch {
            newLabelName.value.takeIf { it?.isNotEmpty() ?: false }?.let {
                labelDao.insert(Label(title = it, colour = colour))
                newLabelName.postValue("")
            }
        }
    }
}
