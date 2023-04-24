package isel.ps.dwp.model

import java.util.Date

data class User (
    val email: String,
    val name: String,
    val token: String
)

data class Role (
    val roleId: String,
    val name: String,
    val description: String
)

data class Process (
    val processId: String,
    val name: String,
    val description: String,
    val type: String,
    var state: String,
    val responsible: String,
    val startDate: Date,
    val endDate: Date,
    val duration: Int
)

data class Stage (
    val stageId: String,
    val name: String,
    val description: String,
    var state: String,
    val responsible: String,
    val startDate: Date,
    val endDate: Date,
    val duration: Int
)

data class Comment (
    val commentId: String,
    val date: Date,
    val text: String,
    val userId: String
)

data class Document (
    val documentId: String,
    val name: String,
    val description: String,
    val location: String
)