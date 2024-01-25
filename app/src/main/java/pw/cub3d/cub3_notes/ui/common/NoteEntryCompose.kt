package pw.cub3d.cub3_notes.ui.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderState
import org.burnoutcrew.reorderable.reorderable
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.database.entity.*
import pw.cub3d.cub3_notes.ui.home.HomeViewModel

@Composable
fun Chip(
    text: String,
    icon: Painter? = null,
    icon_desc: String? = null,
    color: Color = Color.White
) {
    Surface(
        modifier = Modifier.padding(end = 8.dp, bottom = 0.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
        color = colorResource(com.wdullaer.materialdatetimepicker.R.color.mdtp_light_gray),
        border = BorderStroke(1.dp, color),
    ) {
        Row() {
            icon?.let {
                Image(painter = icon, contentDescription = icon_desc, modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .then(Modifier.padding(2.dp, 0.dp)))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                color = color,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun NoteListCompose(
    notes: List<NoteAndCheckboxes>,
    noteMoved: (NoteAndCheckboxes, Int) -> Unit,
    newNoteCallback: ()->Unit = {},
) {

    Column() {
        val state = rememberReorderState()
        LazyColumn(state = state.listState, modifier = Modifier
            .reorderable(
                state = state,
                onMove = { from, to ->
                    println("Moving from $from to $to")
                    noteMoved(notes[from], to)
                },
                canDragOver = { _ -> true }
            )
            .detectReorderAfterLongPress(state)) {
            items(notes, key = { it.note.id }) {
                NoteTheme(darkTheme = true) {
                    NoteEntryCompose(note = it)
                }
            }
        }
        BottomAppBar() {
            FloatingActionButton(onClick = { newNoteCallback() }) {
            }
        }
    }

//    LazyVerticalGrid(cells = GridCells.Fixed(2), content = {
//        items(notes) {
//            NoteEntryCompose(note = it)
//        }
//    })
}

@ExperimentalFoundationApi
@Composable
fun NoteListComposeLive(
    liveNotes: LiveData<List<NoteAndCheckboxes>>,
    noteMoved: (NoteAndCheckboxes, Int) -> Unit,
    newNoteCallback: ()->Unit = {},
    ) {
    val notes = liveNotes.observeAsState()
    NoteTheme(darkTheme = true) {
        NoteListCompose(
            notes = notes.value ?: emptyList(),
            noteMoved = noteMoved,
            newNoteCallback = newNoteCallback,
        )
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
fun NoteListComposePreview() {
    val (noteList, setNoteList) = remember { mutableStateOf(listOf(
        NoteAndCheckboxes(Note(id = 0).apply { title = "Hello, world!" }, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
        NoteAndCheckboxes(Note(id = 1), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
        NoteAndCheckboxes(Note(id = 2), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
        NoteAndCheckboxes(Note(id = 3), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
    ))}

    NoteTheme(darkTheme = true) {
        NoteListCompose(
            notes = noteList, noteMoved = { note,pos ->
                val newNoteList = noteList.toMutableList()
                newNoteList.remove(note)
                newNoteList.add(pos, note)
                setNoteList(newNoteList)
            }
        )
    }
}

@Composable
fun NoteEntryCompose(
    note: NoteAndCheckboxes,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val bg =
        note.note.colour?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.Transparent
    Card(
        modifier = Modifier
            .padding(16.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)),
        elevation = 0.dp,
        backgroundColor = bg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
        ) {
            if (note.isEmpty()) {
                Text(
                    text = "Empty Note",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.LightGray,
                    fontSize = 20.sp
                )
            } else {
                if (note.images.isNotEmpty()) {
                    Column() {
                        note.images.forEach {
                            Text("Image here")
                        }
                    }
                }

                if (note.note.title.isNotEmpty()) {
                    Text(text = note.note.title, color = Color.White)
                }
                if (!note.note.hiddenContent) {
                    if (note.note.text.isNotEmpty()) {
                        Text(text = note.note.text)
                    }
                }

                if (note.checkboxes.isNotEmpty()) {
                    Column() {
                        note.checkboxes.filterNot { it.checked }.sortedBy { it.position }.forEach {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Row() {
                                    Checkbox(checked = it.checked, onCheckedChange = {
                                        println("Checked")
                                    }, modifier = Modifier.padding(end = 8.dp))
                                    Text(
                                        text = it.content,
                                        color = Color.White,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    val ticked = note.checkboxes.count { it.checked }
                    if (ticked > 0) {
                        val postfix = if (ticked == 1) {
                            "item"
                        } else {
                            "items"
                        }
                        Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                            Text(text = "+ $ticked ticked $postfix", color = Color.LightGray)
                            LinearProgressIndicator(
                                progress = (ticked.toFloat() / note.checkboxes.size.toFloat()) * 100.0f,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .width(18.dp)
                                    .padding(start = 16.dp)
                            )
                        }
                    }
                }

                if (note.audioClips.isNotEmpty()) {
                    Column() {
                        note.audioClips.forEach {
                            Text("Audio here")
                        }
                    }
                }

                Column() {
                    if (note.note.timeReminder != null) {
                        Chip(
                            text = note.note.formattedReminderTime()!!,
                            icon = painterResource(id = R.drawable.ic_clock)
                        )
                    }
                    note.labels.forEach {
                        Chip(
                            text = it.title,
                            color = Color(android.graphics.Color.parseColor(it.colour))
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun EmptyNoteEntryComposePreview() {
    NoteTheme(darkTheme = true) {
        NoteEntryCompose(note = NoteAndCheckboxes(
            Note(),
            emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        )
    }
}

@Preview
@Composable
fun BackgroundCheckboxNoteEntryComposePreview() {
    NoteTheme(darkTheme = true) {
            NoteEntryCompose(note = NoteAndCheckboxes(
                Note().apply {
                    title = "ddddddd"
                    colour = "#00FF00"
                },
                listOf(
                    CheckboxEntry().apply {
                        content = "ddd"
                        checked = false
                    },
                ), emptyList(), emptyList(), emptyList(), emptyList())
            )
    }
}

@Preview
@Composable
fun CheckboxCheckedNoteEntryComposePreview() {
    NoteTheme(darkTheme = true) {
        NoteEntryCompose(note = NoteAndCheckboxes(
            Note(),
            listOf(
                CheckboxEntry().apply {
                    checked = true
                },
                CheckboxEntry().apply {
                    checked = true
                },
                CheckboxEntry().apply {
                    checked = true
                },
                CheckboxEntry().apply {
                    checked = true
                },
            ), emptyList(), emptyList(), emptyList(), emptyList())
        )
    }
}

@Preview
@Composable
fun LabelNoteEntryComposePreview() {
    NoteTheme(darkTheme = true) {
        NoteEntryCompose(note = NoteAndCheckboxes(
            Note().apply {
                title = "# Important"
                text = "**DONT** forget to be happy :)"
                colour = "#FF0050"
            },
            emptyList(), listOf(Label(title = "c", colour = "#00FF00"), Label(title = "test2", colour = "#FF0000"), Label(title = "test3", colour = "#0000FF")), listOf(
                ImageEntry()
            ), emptyList(), emptyList())
        )
    }
}