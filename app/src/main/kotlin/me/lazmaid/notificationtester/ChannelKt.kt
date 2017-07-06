package me.lazmaid.notificationtester

enum class ChannelKt(val displayName: String) {
    PUBLIC("Public"),
    PRIVATE("Private"),
    DIRECT("Direct");

    val id: String
        get() = "${name.toLowerCase()}-channel"
}