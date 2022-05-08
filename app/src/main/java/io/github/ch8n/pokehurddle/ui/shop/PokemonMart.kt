package io.github.ch8n.pokehurddle.ui.shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.github.ch8n.pokehurddle.databinding.FragmentMartBinding
import io.github.ch8n.pokehurddle.ui.MainViewModel
import io.github.ch8n.pokehurddle.ui.shop.pages.MartBerriesFragment
import io.github.ch8n.pokehurddle.ui.shop.pages.MartPokeballFragment
import io.github.ch8n.pokehurddle.ui.shop.pages.MartPokemonFragment
import io.github.ch8n.pokehurddle.ui.utils.AppPagerAdapter
import io.github.ch8n.pokehurddle.ui.utils.ViewBindingFragment
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class PokemonMart : ViewBindingFragment<FragmentMartBinding>() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun setup(): Unit = with(binding) {

        lifecycleScope.launchWhenResumed {
            viewModel.playerStats.collect {
                labelWallet.text = "Wallet : (${it.money}) P`Coins"
            }
        }

        val adapter = AppPagerAdapter.newInstance(
            fragmentManager = requireActivity().supportFragmentManager,
            lifecycle = lifecycle,
            "Pokemon Of Day" to MartPokemonFragment(),
            "Berries" to MartBerriesFragment(),
            "PokeBalls" to MartPokeballFragment()
        )

        pagerItems.adapter = adapter

        TabLayoutMediator(tabs, pagerItems) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMartBinding
        get() = FragmentMartBinding::inflate
}