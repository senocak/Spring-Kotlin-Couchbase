package com.github.senocak.auth.service

import com.github.senocak.auth.config.DataSourceConfig
import com.github.senocak.auth.domain.User
import com.github.senocak.auth.util.RoleName
import com.github.senocak.auth.util.logger
import java.util.Date
import java.util.UUID
import org.slf4j.Logger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@Async
class Listeners(
    private val dataSourceConfig: DataSourceConfig,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
){
    private val log: Logger by logger()

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReadyEvent(event: ApplicationReadyEvent) {
        if (dataSourceConfig.ddl == "create") {
            User(name = "anil1", email = "anil1@senocak.com", password = passwordEncoder.encode("asenocak"))
                .also {
                    it.id = UUID.fromString("2cb9374e-4e52-4142-a1af-16144ef4a27d")
                    it.roles = listOf(RoleName.ROLE_USER.role, RoleName.ROLE_ADMIN.role)
                    it.emailActivatedAt = Date()
                }
                .run {
                    userService.save(user = this)
                }

            User(name = "anil2", email = "anil2@gmail.com", password = passwordEncoder.encode("asenocak"))
                .also {
                    it.id = UUID.fromString("3cb9374e-4e52-4142-a1af-16144ef4a27d")
                    it.roles = listOf(RoleName.ROLE_USER.role)
                    it.emailActivatedAt = Date()
                }
                .run {
                    userService.save(user = this)
                }

            User(name = "anil3", email = "anil3@gmail.com", password = passwordEncoder.encode("asenocak"))
                .also {
                    it.id = UUID.fromString("3cb9374e-4e52-4142-a1af-16144ef4a27d")
                    it.roles = listOf(RoleName.ROLE_USER.role)
                }
                .run {
                    userService.save(user = this)
                }
            log.info("Seeding completed")
        }
    }
}
