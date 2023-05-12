package isel.ps.dwp.interfaces

import isel.ps.dwp.model.EmailDetails

interface NotificationsServicesInterface {

    fun sendSimpleMail(details: EmailDetails): String

    /**
     * Schedules an email for the defined frequency in days
     */
    fun scheduleEmail(details: EmailDetails, period: Long): String

    /**
     * Cancel the defined emails schedule
     */
    fun cancelScheduledEmails(notificationId: String)
}