package pw.cub3d.cub3_notes.ui.labelEdit

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import pw.cub3d.cub3_notes.core.database.entity.Label
import pw.cub3d.cub3_notes.databinding.LabelEntryBinding
import pw.cub3d.cub3_notes.ui.home.BaseAdapter

class LabelAdapter(
    ctx: Context,
    private val callbacks: LabelViewHolderCallbacks
) : BaseAdapter<Label, LabelViewHolder>(ctx) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LabelViewHolder(
        LabelEntryBinding.inflate(layoutInflater, parent, false),
        callbacks
    )

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class LabelViewHolder(
    private val view: LabelEntryBinding,
    private val callbacks: LabelViewHolderCallbacks
) : RecyclerView.ViewHolder(view.root) {

    fun bind(label: Label) {
        view.model = label

        view.labelEntryColour.setOnClickListener {
            callbacks.onColorClicked(label)
        }
    }
}

class LabelViewHolderCallbacks(private val vm: LabelEditViewModel, private val act: Activity) {
    fun onColorClicked(label: Label) {
        val originalColor = label.colour?.let { Color.parseColor(it) } ?: Color.WHITE

        val cp = ColorPicker(act, Color.red(originalColor), Color.green(originalColor), Color.blue(originalColor))
        cp.show()
        cp.enableAutoClose()
        cp.setCallback { color ->
            vm.updateLabelColour(label, "#${Integer.toHexString(color)}")
        }
    }
}