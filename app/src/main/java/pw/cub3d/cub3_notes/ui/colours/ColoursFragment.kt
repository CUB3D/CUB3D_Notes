package pw.cub3d.cub3_notes.ui.colours

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.databinding.FragmentColoursBinding


class ColoursFragment : Fragment() {

    private val viewModel: ColoursViewModel by viewModels { injector.coloursViewModelFactory() }
    lateinit var binding: FragmentColoursBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentColoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.coloursRecycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ColoursAdapter(requireContext())
        binding.coloursRecycler.adapter = adapter

        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(direction == ItemTouchHelper.RIGHT) {
                    val it = adapter.colours
                    viewModel.deleteColour(it.find { it.id == viewHolder.itemId }!!)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.coloursRecycler)


        viewModel.colours.observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })

        binding.coloursAdd.setOnClickListener {
            val cp = ColorPicker(requireActivity(), 200, 200, 0)
            cp.show()

            cp.enableAutoClose() // Enable auto-dismiss for the dialog

            cp.setCallback { color ->
                viewModel.addColour("#${Integer.toHexString(color)}")
            }
        }
    }
}
