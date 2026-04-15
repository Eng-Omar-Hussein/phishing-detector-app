package com.emad.phishingdetector.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.emad.data.local.SessionManager
import com.emad.domain.util.NetworkResult
import com.emad.phishingdetector.MainActivity
import com.emad.phishingdetector.databinding.ActivityWelcomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var sessionManager: SessionManager

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            if (idToken != null) {
                viewModel.loginWithGoogle(idToken)
            } else {
                showError("Sign-In failed: No ID Token retrieved.")
                resetUi()
            }
        } catch (e: ApiException) {
            showError("Google Sign-In failed (Code: ${e.statusCode})")
            resetUi()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!sessionManager.getAuthToken().isNullOrBlank()) {
            navigateToHome()
            return
        }

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoogleSignIn()
        observeViewModel()

        resetUi()

        binding.btnGoogleSignIn.setOnClickListener {
            binding.progressBar.isVisible = true
            binding.btnGoogleSignIn.text = ""
            binding.btnGoogleSignIn.isEnabled = false
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun setupGoogleSignIn() {
        val webClientId = "591472832320-jildbbs0e9vts2fc1ev9rhfai8gl9asd.apps.googleusercontent.com"

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        // Already handled in the click listener
                    }

                    is NetworkResult.Success -> {
                        viewModel.resetLoginState()
                        navigateToHome()
                    }

                    is NetworkResult.Error -> {
                        showError(result.message ?: "Backend login failed")
                        resetUi()
                        viewModel.resetLoginState()
                    }

                    is NetworkResult.Empty -> {
                        // Auth never returns empty, but required for exhaustive when
                        showError("Unexpected empty response. Please try again.")
                        resetUi()
                    }

                    null -> { /* Initial state, do nothing */
                    }
                }
            }
        }
    }

    private fun resetUi() {
        binding.progressBar.isVisible = false
        binding.btnGoogleSignIn.text = "Continue with Google"
        binding.btnGoogleSignIn.isEnabled = true
    }

    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun signOutFromGoogle() {
        googleSignInClient.signOut()
    }
}