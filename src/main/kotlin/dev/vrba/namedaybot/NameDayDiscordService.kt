package dev.vrba.namedaybot

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.LocalDate

@Component
class NameDayDiscordService(@Value("\${DISCORD_WEBHOOKS}") webhooks: String) {

    private val client: WebClient = WebClient.builder()
        .baseUrl("https://svatky.adresa.info")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    private val webhooks: List<WebhookClient> = webhooks
        .split(";")
        .map { WebhookClient.withUrl(it) }

    @Scheduled(cron = "0 0 8 * * *")
    fun postNameDayUpdates() {
        updateNameDay("cz", ":flag_cz: Svátek má dnes")
        updateNameDay("sk", ":flag_sk: Meniny má dnes")
    }

    private fun updateNameDay(lang: String, title: String) {
        client.get()
            .uri("/txt?lang=${lang}")
            .retrieve()
            .bodyToMono<String>()
            .subscribe {
                val names = it.lines()
                    .filter { line -> line.isNotBlank() }
                    .joinToString("\n") { line -> line.split(";")[1] }

                val today = LocalDate.now()
                val embed = WebhookEmbedBuilder()
                    .setTitle(WebhookEmbed.EmbedTitle(title, null))
                    .setDescription(names)
                    .setFooter(WebhookEmbed.EmbedFooter("${today.dayOfMonth}. ${today.monthValue}.", null))
                    .build()

                webhooks.forEach { webhook -> webhook.send(embed) }
            }
    }
}