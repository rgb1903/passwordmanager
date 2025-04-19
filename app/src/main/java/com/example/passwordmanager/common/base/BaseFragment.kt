package com.example.passwordmanager.common.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    abstract fun initViews()
    abstract fun observeData()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeData()
    }
}