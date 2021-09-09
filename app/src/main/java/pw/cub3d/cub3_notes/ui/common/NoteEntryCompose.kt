package pw.cub3d.cub3_notes.ui.common

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import androidx.core.graphics.toColorInt
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.core.database.entity.Note
import pw.cub3d.cub3_notes.core.database.entity.NoteAndCheckboxes

@Composable
fun Chip(
    text: String,
    icon: Painter? = null,
    icon_desc: String? = null,
) {
    Surface(
        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
        color = colorResource(R.color.mdtp_light_gray)
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
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun NoteEntryCompose(note: NoteAndCheckboxes) {
    val bg = note.note.colour?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.Transparent
    Card(modifier = Modifier
        .padding(16.dp)
        .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)), elevation = 0.dp, backgroundColor = bg, shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            if (note.isEmpty()) {
                Text(text = "Empty Note", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.LightGray, fontSize = 20.sp)
            } else {
                if (note.images.isNotEmpty()) {
                    LazyColumn(content = {
                        items(note.images, key=null) {
                            Text("Image here")
                        }
                    })
                }

                Text(text = note.note.title, color = Color.White)
                if (!note.note.hiddenContent) {
                    Text(text = note.note.text)
                }

                if (note.checkboxes.isNotEmpty()) {
                    LazyColumn(content = {
                        items(
                            note.checkboxes.filterNot { it.checked }.sortedBy { it.position },
                            key = null
                        ) {
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
//                                Icon(painter = painterResource(id = R.drawable.ic_x), contentDescription = "Close", modifier = Modifier.align(Alignment.CenterEnd))
                            }
                        }
                    })
                    val ticked = note.checkboxes.count { it.checked }
                    if (ticked > 0) {
                        val postfix = if (ticked == 1) {
                            "item"
                        } else {
                            "items"
                        }
                        Row {
                            Text("+ $ticked $postfix")
                            LinearProgressIndicator(progress = (ticked.toFloat() / note.checkboxes.size.toFloat()) * 100.0f)
                        }
                    }
                }

                if (note.audioClips.isNotEmpty()) {
                    LazyColumn(content = {
                        items(note.images, key=null) {
                            Text("Audio here")
                        }
                    })
                }

                LazyRow(content = {
                    if (note.note.timeReminder != null) {
                        item {
                            Chip(
                                text = note.note.formattedReminderTime()!!,
                                icon = painterResource(id = R.drawable.ic_clock)
                            )
                        }
                    }
                    items(note.labels) {
                        Chip(text = it.title)
                    }
                })


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