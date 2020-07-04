package me.lovesasuna.bot.util

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.function.Dynamic

object Logger {
    fun log(message: String, level: LogLevel) {
        val logger = Main.logger
        when (level) {
            LogLevel.INFO -> logger?.info(message)
            LogLevel.WARNING -> logger?.warning(message)
            LogLevel.ERROR -> logger?.error(message)
            LogLevel.CONSOLE -> println("[Console] $message")
        }
    }
    fun log(message: Messages, level: LogLevel) {
        log(message.message, level)
    }

    enum class Messages(val message: String) {
        DOWNLOAD_DEPEN("正在获取依赖");
    }

    enum class LogLevel {
        CONSOLE,
        INFO,
        WARNING,
        ERROR;
    }
}