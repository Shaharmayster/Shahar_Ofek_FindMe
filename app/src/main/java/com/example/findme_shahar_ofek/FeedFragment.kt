package com.example.findme_shahar_ofek

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.findme_shahar_ofek.databinding.FragmentFeedBinding
import com.google.android.material.snackbar.Snackbar

/** Main feed screen that shows cached posts and refreshes from Firestore. */
class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedViewModel by viewModels()
    private val adapter = PostAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postsRecyclerView.adapter = adapter
        binding.postsRecyclerView.setHasFixedSize(true)
        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.findme_primary,
            R.color.findme_secondary,
            R.color.findme_tertiary
        )

        binding.addPostButton.setOnClickListener {
            val action = FeedFragmentDirections.actionFeedFragmentToCreatePostFragment(null)
            findNavController().navigate(action)
        }

        binding.myPostsButton.setOnClickListener {
            val action = FeedFragmentDirections.actionFeedFragmentToMyPostsFragment()
            findNavController().navigate(action)
        }

        binding.apiScreenButton.setOnClickListener {
            val action = FeedFragmentDirections.actionFeedFragmentToApiFragment()
            findNavController().navigate(action)
        }

        binding.goProfileButton.setOnClickListener {
            val action = FeedFragmentDirections.actionFeedFragmentToProfileFragment()
            findNavController().navigate(action)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts) {
                binding.postsRecyclerView.scheduleLayoutAnimation()
            }
            binding.emptyStateContainer.isVisible = posts.isEmpty()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
            binding.swipeRefreshLayout.isRefreshing = loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.errorText.isVisible = !error.isNullOrBlank()
            binding.errorText.text = error
        }

        viewModel.lastSyncText.observe(viewLifecycleOwner) { lastSyncText ->
            binding.lastSyncText.isVisible = !lastSyncText.isNullOrBlank()
            binding.lastSyncText.text = lastSyncText
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>(POST_RESULT_KEY)
            ?.observe(viewLifecycleOwner) { message ->
                if (!message.isNullOrBlank()) {
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<String>(POST_RESULT_KEY)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val POST_RESULT_KEY = "post_result"
    }
}
