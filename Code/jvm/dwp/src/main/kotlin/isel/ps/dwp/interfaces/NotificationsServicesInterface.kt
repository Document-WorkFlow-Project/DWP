package isel.ps.dwp.interfaces

import isel.ps.dwp.model.EmailDetails
import isel.ps.dwp.model.StageInfo

interface NotificationsServicesInterface {

    fun sendSimpleMail(details: EmailDetails): String

    /**
     * Schedules an email for the defined frequency in days
     */
    fun scheduleStageNotifications(stageInfo: StageInfo, stageResponsible: List<String>)

    /**
     * Cancel the defined emails schedule
     */
    fun cancelScheduledNotification(notificationId: String)
}