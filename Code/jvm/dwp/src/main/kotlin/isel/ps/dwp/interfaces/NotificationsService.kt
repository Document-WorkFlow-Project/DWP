package isel.ps.dwp.interfaces

import isel.ps.dwp.model.EmailDetails

interface NotificationsService {

    fun sendSimpleMail(details: EmailDetails): String
}