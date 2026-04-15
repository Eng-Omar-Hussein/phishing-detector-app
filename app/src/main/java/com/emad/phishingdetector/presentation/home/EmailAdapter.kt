package com.emad.phishingdetector.presentation.home

import android.graphics.Typeface
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emad.domain.model.Email
import com.emad.phishingdetector.R
import com.emad.phishingdetector.databinding.ItemEmailBinding

class EmailAdapter(
    private val onEmailClick: (Email) -> Unit,
    private val onStarClick: (Email) -> Unit
) : ListAdapter<Email, EmailAdapter.EmailViewHolder>(EmailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val binding = ItemEmailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EmailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EmailViewHolder(private val binding: ItemEmailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(email: Email) {

            // ── Text fields ───────────────────────────────────────────────
            binding.tvSender.text = email.sender.name
            binding.tvSubject.text = email.subject ?: "(No Subject)"
            binding.tvSnippet.text = email.bodySnippet ?: ""

            // ── Timestamp ─────────────────────────────────────────────────
            binding.tvDate.text = DateUtils.getRelativeTimeSpanString(
                email.timestamp,
                System.currentTimeMillis(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            )

            // ── Unread indicator dot ──────────────────────────────────────
            binding.viewUnreadDot.isVisible = !email.isRead

            // Bold sender + subject when unread, normal when read
            val typeface = if (!email.isRead) Typeface.BOLD else Typeface.NORMAL
            binding.tvSender.setTypeface(null, typeface)
            binding.tvSubject.setTypeface(null, typeface)

            // ── Attachment icon ───────────────────────────────────────────
            binding.imgAttachment.isVisible = email.attachments.isNotEmpty()

            // ── Star icon ─────────────────────────────────────────────────
            binding.imgStar.setImageResource(
                if (email.isStarred) R.drawable.ic_star else R.drawable.ic_star_border
            )
            binding.imgStar.setColorFilter(
                ContextCompat.getColor(
                    binding.root.context,
                    if (email.isStarred) R.color.blue else R.color.gray
                )
            )

            // ── Phishing warning chip ─────────────────────────────────────
            // Visible only when the ML/cybersecurity backend flagged this email
            binding.chipHookedWarning.isVisible = email.isHooked

            // ── Card background — subtle red tint for hooked emails ───────
            binding.root.setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (email.isHooked) R.color.red_surface else R.color.card_background
                )
            )

            // ── Click listeners ───────────────────────────────────────────
            binding.root.setOnClickListener { onEmailClick(email) }
            binding.imgStar.setOnClickListener { onStarClick(email) }
        }
    }

    class EmailDiffCallback : DiffUtil.ItemCallback<Email>() {
        override fun areItemsTheSame(oldItem: Email, newItem: Email) =
            oldItem.emailId == newItem.emailId

        override fun areContentsTheSame(oldItem: Email, newItem: Email) =
            oldItem == newItem
    }
}