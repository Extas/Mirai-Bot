package me.lovesasuna.bot.function

import com.fasterxml.jackson.databind.ObjectMapper
import me.lovesasuna.bot.Bot
import me.lovesasuna.bot.MyCustomPermission
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import kotlin.jvm.Throws

object Hitokoto : SimpleCommand(
        Bot, "一言",
        description = "一言",
        permission = MyCustomPermission
) {
    @Handler
    suspend fun CommandSender.handle() { // 函数名随意, 但参数需要按顺序放置.
        var reader: BufferedReader
        val mapper = ObjectMapper()
        /*如果不带参数,默认全部获取*/

        val inputStream = NetWorkUtil.get("https://v1.hitokoto.cn/")?.second ?: return
        reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        var string: String?
        var text: String? = ""
        while (reader.readLine().also { string = it } != null) {
            text += string
        }
        val `object` = mapper.readTree(text)
        val hitokoto = `object`["hitokoto"].asText()
        val from = `object`["from"].asText()
        sendMessage("『 $hitokoto 』- 「$from」")
    }

    @Handler
    suspend fun CommandSender.handle2(parm: String) {
        /*如果长度为2*/
        if ("help".equals(parm, ignoreCase = true)) {
            sendMessage("""
     一言参数: 
     a	Anime - 动画
     b	Comic – 漫画
     c	Game – 游戏
     d	Novel – 小说
     e	Myself – 原创
     f	Internet – 来自网络
     g	Other – 其他
     不填 - 随机
     """.trimIndent())
        } else {
            var reader: BufferedReader
            val mapper = ObjectMapper()
            val inputStream = NetWorkUtil.get("https://v1.hitokoto.cn/?c=$parm")?.second ?: return
            reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
            var string: String?
            var text: String? = ""
            while (reader.readLine().also { string = it } != null) {
                text += string
            }
            val `object` = mapper.readTree(text)
            val hitokoto = `object`["hitokoto"].asText()
            val from = `object`["from"].asText()
            sendMessage("『 $hitokoto 』- 「$from」")
        }
    }

}