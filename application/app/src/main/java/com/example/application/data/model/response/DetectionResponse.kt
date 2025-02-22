package com.example.application.data.model.response

data class DetectionResult(
    val label: String,
    val confidence: Float
)

data class DetectionResponse(
    val result: List<DetectionResult>
)
