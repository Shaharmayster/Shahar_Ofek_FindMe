package com.example.findme_shahar_ofek

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.findme_shahar_ofek.databinding.FragmentApiBinding

/** Screen that displays REST API posts with cache fallback and retry. */
class ApiFragment : Fragment() {
    private var _binding: FragmentApiBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ApiViewModel by viewModels()
    private val adapter = ApiPostAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apiPostsRecyclerView.adapter = adapter
        binding.apiPostsRecyclerView.setHasFixedSize(true)

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts) {
                binding.apiPostsRecyclerView.scheduleLayoutAnimation()
            }
            binding.apiEmptyContainer.isVisible = posts.isEmpty() && viewModel.error.value.isNullOrBlank()
            if (posts.isNotEmpty()) {
                binding.apiErrorLayout.isVisible = false
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.apiProgressBar.isVisible = isLoading
            binding.apiRetryButton.isEnabled = !isLoading
            binding.backToFeedButton.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.apiErrorLayout.isVisible = !error.isNullOrBlank() && adapter.currentList.isEmpty()
            binding.apiEmptyContainer.isVisible = error.isNullOrBlank() && adapter.currentList.isEmpty()
            binding.apiErrorText.text = error
        }

        binding.apiRetryButton.setOnClickListener {
            viewModel.refresh()
        }

        binding.backToFeedButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
