package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.util.photo.ImageUtil
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import java.util.*
import javax.imageio.ImageIO

/**
 * @author LovesAsuna
 */
class RepeatDetect : FunctionListener {
    private val maps: MutableMap<Long, MutableList<MessageChain>> = HashMap()
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val groupID = (event as GroupMessageEvent).group.id
        maps.putIfAbsent(groupID, ArrayList())
        val messageList = maps[groupID]!!

        if (messageList.size >= 3) {
            messageList.removeAt(0)
        }

        operate(event, messageList)

        if (messageList.size < 3) {
            return false
        }

        if (isRepeat(messageList)) {
            Main.scheduler.asyncTask {

                val messageChain = event.message
                when (messageChain.size) {
                    2 -> {
                        when (messageChain[1]) {
                            is PlainText -> {
                                ArrayList<Char>().apply {
                                    message.forEach {
                                        this.add(it)
                                    }
                                    this.shuffle()
                                    val builder = StringBuilder()
                                    this.forEach { builder.append(it) }
                                    event.reply(builder.toString())
                                }
                            }
                            is Image -> {
                                val bufferedImage = ImageIO.read(NetWorkUtil[image!!.queryUrl()]?.second).let {
                                    when (Random().nextInt(4)) {
                                        0 -> ImageUtil.rotateImage(it, 180)
                                        1 -> ImageUtil.mirrorImage(it)
                                        2 -> ImageUtil.reverseImage(it, 1)
                                        3 -> ImageUtil.reverseImage(it, 2)
                                        else -> it
                                    }
                                }
                                event.reply(event.uploadImage(bufferedImage))
                            }
                            else -> event.reply(messageList[2])
                        }
                    }
                    else -> event.reply(messageList[2])
                }

                messageList.clear()
                this
            }
        }
        return true
    }

    private fun operate(event: MessageEvent, messageList: MutableList<MessageChain>) {
        messageList.add(event.message)
    }

    private fun isRepeat(messageList: MutableList<MessageChain>): Boolean {
        val first = messageList.first()
        val second = messageList[1]
        val third = messageList[2]
        if (first.contentEquals(second) && second.contentEquals(third)) {
            return true
        }
        return false
    }
}