package com.emad.phishingdetector.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.emad.domain.model.FolderType
import com.emad.phishingdetector.R
import com.emad.phishingdetector.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    companion object {
        const val REQUEST_FOLDER_CHANGE = "request_folder_change"
        const val KEY_FOLDER = "folder"
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var emailAdapter: EmailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupChips()
        setupSwipeRefresh()
        observeViewModel()

        parentFragmentManager.setFragmentResultListener(
            REQUEST_FOLDER_CHANGE,
            viewLifecycleOwner
        ) { _, bundle ->
            val key = bundle.getString(KEY_FOLDER) ?: return@setFragmentResultListener
            when (key) {
                "INBOX" -> selectFolder(FolderType.INBOX, binding.chipInbox)
                "SENT" -> selectFolder(FolderType.SENT, binding.chipSent)
                "SPAM" -> selectFolder(FolderType.SPAM, binding.chipSpam)
                "TRASH" -> selectFolder(FolderType.TRASH, binding.chipTrash)
                "HOOKED" -> selectFolder(FolderType.HOOKED, binding.chipHooked)
                "STARRED" -> {
                    // special case: Starred is not a FolderType, we’ll handle separately below
                    showSnackbar("Starred view not implemented yet")
                }
            }
        }
    }

    private fun selectFolder(folder: FolderType, chip: com.google.android.material.chip.Chip) {
        // Update chips UI
        chip.isChecked = true
        // If folder changed, reload
        if (folder != viewModel.state.value.currentFolder) {
            viewModel.loadEmails(folder)
        }
    }
    // ── RecyclerView ──────────────────────────────────────────────────────

    private fun setupRecyclerView() {
        emailAdapter = EmailAdapter(
            onEmailClick = { email ->
                val bundle = Bundle().apply {
                    putString("emailId", email.emailId)
                }
                findNavController().navigate(R.id.nav_detail, bundle)
            },
            onStarClick = { email ->
                showSnackbar(
                    if (email.isStarred) "Removed from starred" else "Added to starred"
                )
            }
        )

        binding.recyclerView.apply {
            adapter = emailAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    // ── Folder chips ──────────────────────────────────────────────────────
    private fun setupChips() {
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val folder = when (checkedIds.firstOrNull()) {
                R.id.chipInbox -> FolderType.INBOX
                R.id.chipSent -> FolderType.SENT
                R.id.chipSpam -> FolderType.SPAM
                R.id.chipHooked -> FolderType.HOOKED
                R.id.chipTrash -> FolderType.TRASH
                else -> FolderType.INBOX
            }
            // Only reload if the folder actually changed
            if (folder != viewModel.state.value.currentFolder) {
                viewModel.loadEmails(folder)
            }
        }
    }

    // ── SwipeRefreshLayout ────────────────────────────────────────────────
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.blue)
            setOnRefreshListener { viewModel.refreshEmails() }
        }
    }

    // ── Observe ViewModel state ───────────────────────────────────────────
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->

                // 1. Update list
                emailAdapter.submitList(state.emails)

                // 2. SwipeRefresh spinner — only when user pulled down
                binding.swipeRefreshLayout.isRefreshing =
                    state.isLoading && state.isRefreshing

                // 3. Center ProgressBar — only on cold load with empty list
                binding.progressBar.isVisible =
                    state.isLoading && !state.isRefreshing && state.emails.isEmpty()

                // 4. Empty state — not loading, no emails, no error
                binding.layoutEmptyState.isVisible =
                    !state.isLoading && state.emails.isEmpty() && state.errorMessage == null

                // 5. Update empty state subtitle based on current folder
                binding.tvEmptySubtitle.text = when (state.currentFolder) {
                    FolderType.HOOKED -> "No phishing emails found. 🎉\nYour inbox looks safe!"
                    FolderType.SPAM -> "No spam detected.\nPull down to refresh."
                    FolderType.TRASH -> "Trash is empty."
                    else -> "No emails here.\nPull down to refresh."
                }

                // 6. Error Snackbar with RETRY action
                state.errorMessage?.let { message ->
                    showSnackbar(
                        message = message,
                        isError = true,
                        actionLabel = "RETRY"
                    ) {
                        viewModel.refreshEmails()
                    }
                    // Consume the error so it doesn't re-show on next emission
                    viewModel.onErrorShown()
                }
            }
        }
    }

    // ── Snackbar helper ───────────────────────────────────────────────────
    private fun showSnackbar(
        message: String,
        isError: Boolean = false,
        actionLabel: String? = null,
        action: (() -> Unit)? = null
    ) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)

        if (isError) {
            snackbar.setBackgroundTint(
                requireContext().getColor(android.R.color.holo_red_dark)
            )
            snackbar.setTextColor(requireContext().getColor(android.R.color.white))
        }

        if (actionLabel != null && action != null) {
            snackbar.setAction(actionLabel) { action() }
            snackbar.setActionTextColor(requireContext().getColor(R.color.blue))
        }

        snackbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null  // Prevent memory leak
        _binding = null
    }
}