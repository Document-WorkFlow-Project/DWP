package isel.ps.dwp.interfaces

import isel.ps.dwp.model.EmailDetails
import java.time.Duration

interface NotificationsServicesInterface {

    fun sendSimpleMail(details: EmailDetails): String

    /**
     * Schedules an email for the defined frequency in days
     */
    fun scheduleEmail(details: EmailDetails, period: Duration): String

    /**
     * Cancel the defined emails schedule
     */
    fun cancelScheduledEmails(notificationId: String)
}