package io.github.ch8n.pokehurddle.ui.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import io.github.ch8n.pokehurddle.ui.MainActivity
import io.github.ch8n.pokehurddle.data.models.PlayerBerry
import io.github.ch8n.pokehurddle.data.models.PlayerPokeball
import io.github.ch8n.pokehurddle.R
import io.github.ch8n.pokehurddle.data.models.PokemonDTO
import io.github.ch8n.pokehurddle.databinding.FragmentExploreBinding
import io.github.ch8n.setVisible


class ExploreFragment : Fragment() {

    private var binding: FragmentExploreBinding? = null
    private val viewModel by lazy {
        (requireActivity() as MainActivity).sharedViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run { setup() }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isEscapedFromBattleOrPet) {
            viewModel.setEscapedFromBattleOrPet(false)
            binding?.run {
                val pokemon = viewModel.pokemonEncounter
                // TODO fix state not retained
                displayPokemon(pokemon)
                binding?.btnEscape?.performClick()
            }
        }
    }

    private fun FragmentExploreBinding.displayPokemon(pokemon: PokemonDTO) {
        containerPokemon.setVisible(true)

        Glide.with(requireContext())
            .load(pokemon.sprites.front_default)
            .into(imgEncounter)

        labelEncounter.setText("${pokemon.name}")
    }

    private fun FragmentExploreBinding.setup() {

        btnEscape.setOnClickListener {

            fun onEscape(message: String) {
                containerPokemon.setVisible(false)
                imgEncounter.setImageResource(R.drawable.escape)
                labelEncounter.setText(message)
            }

            viewModel.onEscapePokemon(
                onLostBerry = {
                    val qtyMsg = if (it.qty > 0) it.qty else "all"
                    onEscape("You dropped $qtyMsg ${it.berry.name} while escaping")
                },
                onLostMoney = {
                    val qtyMsg = if (it > 0) it else "all"
                    onEscape("You dropped $qtyMsg Coins while escaping")
                },
                onLostPokeball = {
                    onEscape("You dropped ${it.pokeball.name} while escaping")
                },
                onEscapeNoLoss = {
                    onEscape("You escaped!...")
                }
            )
        }

        btnFight.setOnClickListener {
            val pokemon = viewModel.pokemonEncounter
            findNavController().navigate(R.id.action_exploreFragment_to_battleFragment)
            viewModel.updatePlayer(playerPokemon = pokemon)
        }

        btnPet.setOnClickListener {
            val pokemon = viewModel.pokemonEncounter
            findNavController().navigate(R.id.action_exploreFragment_to_petFragment)
            viewModel.updatePlayer(playerPokemon = pokemon)
        }


        btnGenerate.setOnClickListener {
            viewModel.generateEncounter(
                onNothing = {
                    containerPokemon.setVisible(false)
                    Glide.with(requireContext())
                        .load(R.drawable.nothing)
                        .into(imgEncounter)
                    labelEncounter.setText("Nothing happened!")
                },
                onBerry = {
                    val berry = it
                    val qty = berry.randomQty
                    containerPokemon.setVisible(false)
                    Glide.with(requireContext())
                        .load(berry.sprite)
                        .into(imgEncounter)
                    viewModel.updatePlayer(playerBerry = PlayerBerry(berry, qty))
                    labelEncounter.setText("${berry.name.capitalize()} | Qty: ${qty} | Rate: ${berry.attractionRate}")
                },
                onPokemon = {
                    val pokemon = it
                    displayPokemon(pokemon)
                },
                onPokeball = {
                    val pokeball = it
                    containerPokemon.setVisible(false)
                    Glide.with(requireContext())
                        .load(pokeball.sprite)
                        .into(imgEncounter)
                    viewModel.updatePlayer(playerPokeball = PlayerPokeball(pokeball, 1))
                    labelEncounter.setText("${pokeball.name} | Rate: ${pokeball.successRate}")
                },
                onMoney = {
                    val money = it
                    containerPokemon.setVisible(false)
                    Glide.with(requireContext())
                        .load(R.drawable.coin)
                        .into(imgEncounter)
                    viewModel.updatePlayer(playerMoney = money)
                    labelEncounter.setText("Qty: ${money}")
                },
                onLoading = { isLoading ->
                    loader.setVisible(isLoading)
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}