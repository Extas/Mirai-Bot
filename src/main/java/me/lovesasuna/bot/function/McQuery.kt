package me.lovesasuna.bot.function

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import me.lovesasuna.bot.util.Listener
import me.lovesasuna.bot.util.Mcquery
import me.lovesasuna.bot.util.SRVConvert
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.IOException

class McQuery : Listener {
    @Throws(IOException::class)
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        if (message.startsWith("/mcquery ")) {
            val strings = message.split(" ").toTypedArray()
            val ipAndport = strings[1]
            var query : Boolean
            var NewipAndport: String? = ""

            /*如果不含:则默认为srv记录*/
            if (!ipAndport.contains(":")) {
                if (qeuery(event as GroupMessageEvent, "$ipAndport:25565", false)) {
                    query = true
                } else {
                    event.reply("正在尝试SRV解析")
                    NewipAndport = SRVConvert.convert(ipAndport)
                    query = if (NewipAndport == null) {
                        false
                    } else {
                        qeuery(event, NewipAndport, true)
                    }
                }
            } else {
                query = qeuery(event as GroupMessageEvent, ipAndport, false)
            }
            if (!query) {
                if (NewipAndport != ipAndport && !NewipAndport!!.isEmpty()) {
                    event.reply("""
     无法获取Motd!
     SRV结果为:
     $NewipAndport
     """.trimIndent())
                } else {
                    event.reply("ip地址不正确!")
                }
            }
            return true
        }
        return true
    }

    private fun nodeProcess(node: JsonNode): String {
        var text: String? = ""
        val color = node["color"]
        val strikethrough = node["strikethrough"]
        val bold = node["bold"]
        if (color != null) {
            val colortext = color.asText()
            text += when (colortext) {
                "dark_gray" -> "§7"
                "gray" -> "§7"
                "aqua" -> "§5"
                "white" -> ""
                "green" -> "§20"
                "light_purple" -> ""
                "gold" -> ""
                "yellow" -> ""
                else -> ""
            }
        }
        if (strikethrough != null) {
            text += "§n"
        }
        if (bold != null) {
            text += "§l"
        }
        return node["text"].asText().let { text += it; text!! }
    }

    @Throws(IOException::class)
    private suspend fun qeuery(event: GroupMessageEvent, ipAndport: String, SRV: Boolean): Boolean {
        val host = ipAndport.split(":").toTypedArray()[0]
        val port = ipAndport.split(":").toTypedArray()[1].toInt()
        var json: String?
        json = try {
            Mcquery.query(host, port)
        } catch (e: IOException) {
            return false
        }
        val objectMapper = ObjectMapper()
        val mod = false
        val root = objectMapper.readTree(json)
        val version = root["version"]
        val players = root["players"]
        val description = root["description"]
        val modinfo = root["modinfo"]
        val text = description["text"]
        val extra = description["extra"]
        val translate = description["translate"]
        var texts = ""
        var mods = ""
        if (extra != null) {
            for (i in 0..extra.size() - 1) {
                val node = extra[i]
                texts += nodeProcess(node)
            }
        } else if (text != null) {
            texts = description["text"].asText()
        } else if (translate != null) {
            texts = description["translate"].asText()
        }
        if (modinfo != null) {
            val type = modinfo["type"]
            val modList = modinfo["modList"]
            mods += """
                
                服务器Mod类型: ${type.asText()}${modeProcess(modList)}
                """.trimIndent()
        }
        event.reply(
                """
                    服务器IP:  $host:$port
                    是否使用SRV域名解析:  $SRV
                    服务器版本:  ${version["name"].asText()}
                    是否为mod服务器: $mod
                    目标服务器协议号码:  ${version["protocol"].asText()}
                    最大在线人数:  ${players["max"].asText()}
                    当前在线人数:  ${players["online"].asText()}
                    MOTD:
                    $texts$mods
                    """.trimIndent())
        return true
    }

    private fun modeProcess(modList: JsonNode): String {
        val size = modList.size()
        var mod = "(总计共" + (size - 1) + "个Mod)"
        for (i in 0..size - 1) {
            val node = modList[i]
            val modid = node["modid"].asText()
            val version = node["version"].asText()
            mod += "\n$modid: $version"
        }
        return mod
    }
}