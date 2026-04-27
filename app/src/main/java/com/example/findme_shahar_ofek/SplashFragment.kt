package com.example.findme_shahar_ofek

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

/** Entry screen that routes user by persisted auth state. */
class SplashFragment : Fragment() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            val action = if (viewModel.isUserLoggedIn()) {
                R.id.action_splashFragment_to_feedFragment
            } else {
                R.id.action_splashFragment_to_loginFragment
            }
            findNavController().navigate(action)
        }
    }
}
