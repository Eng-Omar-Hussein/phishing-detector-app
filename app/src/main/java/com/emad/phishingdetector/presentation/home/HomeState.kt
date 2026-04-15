package com.emad.phishingdetector.presentation.home

import com.emad.domain.model.Email
import com.emad.domain.model.FolderType

data class HomeState(
    val emails: List<Email> = emptyList(),
    val isLoading: Boolean = false,

    // Distinguishes between:
    //   isRefreshing = false → cold/initial load → show center ProgressBar
    //   isRefreshing = true  → user pulled down  → show SwipeRefresh spinner at top
    val isRefreshing: Boolean = false,

    // Consumed once shown (set back to null via onErrorShown())
    // Prevents the Snackbar from re-triggering on every StateFlow emission
    val errorMessage: String? = null,

    val currentFolder: FolderType = FolderType.INBOX
)