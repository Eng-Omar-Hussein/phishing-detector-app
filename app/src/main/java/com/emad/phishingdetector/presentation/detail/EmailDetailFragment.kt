package com.emad.phishingdetector.presentation.detail

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.emad.phishingdetector.databinding.FragmentEmailDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailDetailFragment : Fragment() {

    private var _binding: FragmentEmailDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EmailDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailId = requireArguments().getString("emailId") ?: return
        viewModel.loadEmail(emailId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.email.collectLatest { email ->
                email ?: return@collectLatest

                binding.tvFrom.text = "From: ${email.sender.email}"
                binding.tvTo.text = "To: ${email.recipients.joinToString { it.email }}"
                binding.tvSubject.text = email.subject ?: "(No subject)"
                binding.tvBody.text = email.bodyFull ?: email.bodySnippet ?: ""

                binding.chipHooked.isVisible = email.isHooked

                binding.tvDate.text = DateUtils.getRelativeTimeSpanString(
                    email.timestamp,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}