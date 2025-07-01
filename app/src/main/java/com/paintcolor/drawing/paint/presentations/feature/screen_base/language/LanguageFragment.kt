package com.paintcolor.drawing.paint.presentations.feature.screen_base.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentLanguageBinding
import com.paintcolor.drawing.paint.utils.SystemUtils
import com.paintcolor.drawing.paint.widget.tap

class LanguageFragment : BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {
    private lateinit var adapter: LanguageAdapter
    private var lang = "en"

    override fun initData() {

    }

    override fun setupView() {

        adapter = LanguageAdapter(onClick = {
            lang = it.code
            adapter.setCheck(it.code)
            selectLanguage()
        })

        binding.recyclerView.adapter = adapter
        binding.ivBack.tap {
            popBackStack()
        }
        val list = SystemUtils.listLanguage()
        list.forEach {
            if (it.code == SystemUtils.getPreLanguage(requireContext())) {
                lang = it.code
                it.active = true
                return@forEach
            }
        }
        adapter.addList(list)
    }

    override fun dataCollect() {

    }

    private fun selectLanguage() {
        SystemUtils.saveLocale(requireContext(), lang)
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
        AppCompatDelegate.setApplicationLocales(appLocale)
        popBackStack()
//        launchActivity(ContainerActivity::class.java)
//        requireActivity().finishAffinity()
    }
}