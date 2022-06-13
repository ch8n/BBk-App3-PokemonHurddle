package io.github.ch8n.pokehurddle.ui.pokeMart.pages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.ch8n.pokehurddle.databinding.FragmentItemListingBinding
import io.github.ch8n.pokehurddle.ui.MainViewModel
import io.github.ch8n.pokehurddle.ui.pokeMart.adapters.BerriesListAdapter
import io.github.ch8n.pokehurddle.ui.utils.ViewBindingFragment

@AndroidEntryPoint
class MartBerriesFragment : ViewBindingFragment<FragmentItemListingBinding>() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun setup() = with(binding) {
        val berriesAdapter = BerriesListAdapter(
            onBerryClicked = {
                viewModel.buyBerry(
                    berry = it,
                    onError = { msg -> msg.snack() },
                    onSuccess = {
                        "You purchased ${it.name} x1!".snack()
                    }
                )
            })

        list.layoutManager = GridLayoutManager(requireContext(), 2)
        list.adapter = berriesAdapter
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentItemListingBinding
        get() = FragmentItemListingBinding::inflate
}