package com.example.application.utils

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord

object HealthPermissions {

    val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class)
    )

}