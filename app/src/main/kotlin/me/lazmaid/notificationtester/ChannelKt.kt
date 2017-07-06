package me.lazmaid.notificationtester

enum class ChannelKt {
    PUBLIC,
    PRIVATE,
    DIRECT;

    val id: String
        get() = "${name.toLowerCase()}-channel"
}