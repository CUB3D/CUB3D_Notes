package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R

class CheckBoxAdapter(ctx: Context) : RecyclerView.Adapter<CheckBoxViewHolder>() {
    private val layoutInflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CheckBoxViewHolder(
        layoutInflater.inflate(R.layout.checkbox_entry, parent, false)
    )

    override fun getItemCount() = 1

    override fun onBindViewHolder(holder: CheckBoxViewHolder, position: Int) {

    }
}

class CheckBoxViewHolder(view: View): RecyclerView.ViewHolder(view) {

}
