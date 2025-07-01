package com.paintcolor.drawing.paint.presentations.feature.main

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentMainBinding
import com.paintcolor.drawing.paint.presentations.components.views.BottomNavView.Companion.COLORING_FRAGMENT
import com.paintcolor.drawing.paint.presentations.components.views.BottomNavView.Companion.MY_CREATION_FRAGMENT
import com.paintcolor.drawing.paint.presentations.components.views.BottomNavView.Companion.SETTINGS_FRAGMENT
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MainFragment: BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private val viewModel by activityViewModel<MainViewModel>()
    private val navController by lazy {
        childFragmentManager
            .findFragmentById(R.id.fcv_main)
            ?.findNavController()
    }

    override fun initData() {
        viewModel.setSelectedFragment(COLORING_FRAGMENT)
    }

    override fun setupView() {
        binding.apply {
            bottomNav.onBottomClick = {
                viewModel.setSelectedFragment(it)
            }
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateStore.collect {
                    binding.bottomNav.setSelected(it.fragmentSelected)

                    when (it.fragmentSelected) {
                        COLORING_FRAGMENT -> {
                            safeNavigateMain(navController, R.id.coloringFragment)
                        }
                        MY_CREATION_FRAGMENT -> {
                            safeNavigateMain(navController, R.id.myCreationFragment)
                        }
                        SETTINGS_FRAGMENT -> {
                            safeNavigateMain(navController, R.id.settingsFragment)
                        }
                    }
                }
            }
        }
    }

    private fun safeNavigateMain(navController: NavController?, destinationId: Int) {
        navController?.let {
            if (it.currentDestination?.id != destinationId) {
                it.navigate(destinationId)
            }
        }
    }
}