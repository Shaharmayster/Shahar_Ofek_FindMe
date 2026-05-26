package com.example.findme_shahar_ofek

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.findme_shahar_ofek.databinding.FragmentMyPostsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/** Screen that shows only current user's posts and supports edit/delete. */
class MyPostsFragment : Fragment() {
    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPostsViewModel by viewModels()

    private val adapter by lazy {
        PostAdapter(
            onEditClick = { post ->
                val action =
                    MyPostsFragmentDirections.actionMyPostsFragmentToCreatePostFragment(post.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { post ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.delete_post_title)
                    .setMessage(R.string.delete_post_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        viewModel.deletePost(post)
                    }
                    .show()
            },
            currentUserId = viewModel.currentUserId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.myPostsRecyclerView.adapter = adapter
        binding.myPostsRecyclerView.setHasFixedSize(true)

        binding.backToFeedButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts) {
                binding.myPostsRecyclerView.scheduleLayoutAnimation()
            }
            binding.emptyStateContainer.isVisible = posts.isEmpty()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
            binding.backToFeedButton.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.errorText.isVisible = !error.isNullOrBlank()
            binding.errorText.text = error
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { deleted ->
            if (deleted) {
                viewModel.clearDeleteSuccess()
                Snackbar.make(binding.root, R.string.post_deleted, Snackbar.LENGTH_SHORT).show()
            }
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
