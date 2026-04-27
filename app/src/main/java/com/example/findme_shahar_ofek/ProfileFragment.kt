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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.findme_shahar_ofek.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar

/** Profile screen for editing display name and profile image. */
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.bg_avatar_placeholder)
                .error(R.drawable.bg_avatar_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(binding.profileImageView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileImageView.setImageResource(R.drawable.bg_avatar_placeholder)

        binding.profileImageView.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.saveButton.setOnClickListener {
            viewModel.save(
                displayName = binding.displayNameEditText.text.toString(),
                imageUri = selectedImageUri
            )
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        binding.backToFeedButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            binding.userIdText.text = profile?.userId ?: ""
            if (binding.displayNameEditText.text.isNullOrBlank()) {
                binding.displayNameEditText.setText(profile?.displayName.orEmpty())
            }
            val hasSavedImage = ImageCache.existingFileOrNull(profile?.localImagePath) != null ||
                !profile?.imageUrl.isNullOrBlank()
            if (selectedImageUri == null && hasSavedImage) {
                binding.profileImageView.loadCachedImage(
                    localImagePath = profile?.localImagePath,
                    remoteImageUrl = profile?.imageUrl,
                    placeholderResId = R.drawable.bg_avatar_placeholder
                )
            } else if (selectedImageUri == null && !hasSavedImage) {
                binding.profileImageView.setImageResource(R.drawable.bg_avatar_placeholder)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
            binding.saveButton.isEnabled = !loading
            binding.logoutButton.isEnabled = !loading
            binding.backToFeedButton.isEnabled = !loading
            binding.displayNameEditText.isEnabled = !loading
            binding.profileImageView.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.errorText.isVisible = !error.isNullOrBlank()
            binding.errorText.text = error
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                viewModel.clearSaveSuccess()
                Snackbar.make(binding.root, R.string.profile_saved, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
