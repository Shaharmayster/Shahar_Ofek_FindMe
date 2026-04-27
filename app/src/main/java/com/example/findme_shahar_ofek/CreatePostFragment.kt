package com.example.findme_shahar_ofek

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.findme_shahar_ofek.databinding.FragmentCreatePostBinding
import com.google.android.material.snackbar.Snackbar

/** Screen for creating and editing posts with optional image upload. */
class CreatePostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val args: CreatePostFragmentArgs by navArgs()
    private val viewModel: CreatePostViewModel by viewModels()

    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.bg_media_placeholder)
                .error(R.drawable.bg_media_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(binding.postImageView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadPost(args.postId)
        binding.postImageView.setImageResource(R.drawable.bg_media_placeholder)

        binding.postImageView.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.submitButton.setOnClickListener {
            viewModel.submitPost(
                postId = args.postId,
                title = binding.titleEditText.text.toString(),
                imageUri = selectedImageUri
            )
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.editingPost.observe(viewLifecycleOwner) { post ->
            if (post != null) {
                binding.titleEditText.setText(post.title)
                binding.submitButton.text = getString(R.string.update)
                val hasSavedImage = ImageCache.existingFileOrNull(post.localImagePath) != null ||
                    !post.imageUrl.isNullOrBlank()
                if (selectedImageUri == null && hasSavedImage) {
                    binding.postImageView.loadCachedImage(
                        localImagePath = post.localImagePath,
                        remoteImageUrl = post.imageUrl,
                        placeholderResId = R.drawable.bg_media_placeholder
                    )
                }
            } else {
                binding.submitButton.text = getString(R.string.submit)
                if (selectedImageUri == null) {
                    binding.postImageView.setImageResource(R.drawable.bg_media_placeholder)
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
            binding.submitButton.isEnabled = !loading
            binding.cancelButton.isEnabled = !loading
            binding.titleEditText.isEnabled = !loading
            binding.postImageView.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrBlank()) {
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.savedPostId.observe(viewLifecycleOwner) { savedPostId ->
            if (!savedPostId.isNullOrBlank()) {
                viewModel.clearSaveEvent()
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
