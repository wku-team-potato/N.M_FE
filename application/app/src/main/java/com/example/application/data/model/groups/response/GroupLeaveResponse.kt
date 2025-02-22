package com.example.application.data.model.groups.response

import androidx.core.app.NotificationCompat.MessagingStyle.Message

data class GroupLeaveResponse(
    val success: Boolean,
    val message: Message,
    val deleted: Boolean
)
