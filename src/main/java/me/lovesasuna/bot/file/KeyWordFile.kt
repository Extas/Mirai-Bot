package me.lovesasuna.bot.file

import me.lovesasuna.bot.entity.BotData
import me.lovesasuna.bot.controller.KeyWord
import me.lovesasuna.bot.util.BasicUtil

object KeyWordFile : AbstractFile() {
    override val file = BasicUtil.getLocation("keyword.json")
    lateinit var data: KeyWord.KeyWordChain
    override fun writeDefault() {
        val data = KeyWord.KeyWordChain()
        data.list.apply {
            add(KeyWord.KeyWord("啊这", "这啊", 10))
        }
        if (!file.exists()) {
            BotData.objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data)
        }
        readValue()
    }

    override fun writeValue() {
        BotData.objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data)
    }

    override fun readValue() {
        data = BotData.objectMapper.readValue(file, KeyWord.KeyWordChain::class.java)
    }
}