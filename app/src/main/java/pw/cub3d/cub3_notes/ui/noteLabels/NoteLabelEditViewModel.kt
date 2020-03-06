package pw.cub3d.cub3_notes.ui.noteLabels

import androidx.lifecycle.*
import pw.cub3d.cub3_notes.database.dao.LabelDao
import pw.cub3d.cub3_notes.database.entity.Label
import pw.cub3d.cub3_notes.database.entity.Note

class NoteLabelEditViewModel(
    private val labelDao: LabelDao
): ViewModel() {
    var noteId = MutableLiveData(0L)

    fun loadNote(l: Long) {
        noteId.postValue(l)
        noteLabels = labelDao.findByNote(l)

        labels.apply {
            addSource(noteLabels!!) {
                println("noteLabels updated")
                value = onUpdate()
            }
        }
    }

    val searchText = MutableLiveData<String>("")

    val filteredLabels = Transformations.map(searchText) {
        labelDao.findByTitle("%$it%")
    }

    var noteLabels: LiveData<List<Label>>? = null

//    val labels = Transformations.map(searchText) {
//        val al = labelDao.findByTitleAndNote(noteId, "%$it%")
//
//        labelDao.findByTitleAndNote(noteId, "%$it%").map { Pair(it, true) } +
//                labelDao.findByTitle("%$it%").filterNot { al.contains(it) }.map { Pair(it, false) }
//
//    }

    val labels = MediatorLiveData<List<Pair<Label, Boolean>>>().apply {
        addSource(filteredLabels) {
            println("filtered labels updated")
            value = onUpdate()
        }
    }

    fun onUpdate(): List<Pair<Label, Boolean>> {
        if (filteredLabels.value.isNullOrEmpty() || noteLabels?.value == null) {
            return emptyList()
        }

        val uncheckedLabels = filteredLabels.value!!.filterNot { filteredLabel ->
            noteLabels!!.value!!.map { it.id }.contains(filteredLabel.id)
        }

        val checkedLabels = noteLabels!!.value!!

        return uncheckedLabels.map { Pair(it, false) } + checkedLabels.map { Pair(it, true) }
    }
}