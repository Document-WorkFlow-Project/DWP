package isel.ps.dwp.http.pipeline.authorization

import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
annotation class Admin

@Target(FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RoleNeeded(val value: String)