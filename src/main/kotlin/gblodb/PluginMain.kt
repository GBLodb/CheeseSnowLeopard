package gblodb

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.plexpt.chatgpt.Chatbot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.info
import java.io.File
import java.io.FileNotFoundException

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "gblodb.cheeseSnowLeopard",
        name = "芝士雪豹",
        version = "0.1.0"
    ) {
        author("Gary Bryson Luis Jr.")
        info(
            """
            ChatGPT 接入插件
        """.trimIndent()
        )
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded." }
        val mapper = jacksonObjectMapper()
        val propJson =  File("props.json")
        if (!propJson.exists()) {
            propJson.createNewFile()
            propJson.writeText(mapper.writeValueAsString(Props("", "", "")))
            println("Please fill in props.json")
            throw FileNotFoundException()
        }
        val prop: Props = mapper.readValue(propJson)
        val chatbot = Chatbot(prop.sessionToken, prop.cfClearance, prop.ua)
        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.subscribeAlways<GroupMessageEvent> {
            if (message.contentToString().startsWith("@\$" + bot.nick)) {
                val chatResponse: MutableMap<String, Any>? = chatbot.getChatResponse(message.contentToString().substring(StringBuilder("@\$" + bot.nick).length))
                group.sendMessage(chatResponse?.get("message").toString())
            }
        }
    }
}
