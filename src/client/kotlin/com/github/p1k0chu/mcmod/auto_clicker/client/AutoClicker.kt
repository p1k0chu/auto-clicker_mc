package com.github.p1k0chu.mcmod.auto_clicker.client

import com.google.gson.Gson
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Colors
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writer

object AutoClicker : ClientModInitializer {
    const val MOD_ID: String = "auto_clicker"
    val logger = LoggerFactory.getLogger(MOD_ID)
    val client: MinecraftClient?
        get() = MinecraftClient.getInstance()

    // config folder is automatically created by fabric loader
    val CONFIG_FOLDER: Path = FabricLoader.getInstance().configDir

    // the file might not exist
    val CONFIG_FILE: Path = CONFIG_FOLDER.resolve("$MOD_ID.json")

    private val openConfig = KeyBinding(Language.OPEN_SETTINGS.key, GLFW.GLFW_KEY_O, Language.SETTINGS_CATEGORY.key)
    private val toggleFunction = KeyBinding(Language.TOGGLE.key, GLFW.GLFW_KEY_I, Language.SETTINGS_CATEGORY.key)

    val config: Config = loadConfig()
    var active: Boolean = false
        private set(v) {
            field = v
            // release all the buttons
            if (!v) {
                holdingLeft.key.isPressed = false
                holdingRight.key.isPressed = false
                holdingJump.key.isPressed = false
            }
        }

    lateinit var holdingLeft: Holding
    lateinit var holdingRight: Holding
    lateinit var holdingJump: Holding

    override fun onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register { mc ->
            holdingLeft = Holding(mc.options.attackKey, config.leftMouse)
            holdingRight = Holding(mc.options.useKey, config.rightMouse)
            holdingJump = Holding(mc.options.jumpKey, config.jump)
        }

        HudRenderCallback.EVENT.register { context: DrawContext, tickCounter: RenderTickCounter? ->
            if (active) {
                context.drawText(client!!.textRenderer, Language.ACTIVE.text, 2, context.scaledWindowHeight - client!!.textRenderer.fontHeight - 2, Colors.WHITE, true)
            }
        }

        // key binds
        KeyBindingHelper.registerKeyBinding(openConfig)
        KeyBindingHelper.registerKeyBinding(toggleFunction)

        ClientTickEvents.END_CLIENT_TICK.register(::clientTick)
    }

    private fun loadConfig(): Config {
        if (CONFIG_FILE.exists()) {
            try {
                return CONFIG_FILE.reader().use { Gson().fromJson<Config>(it, Config::class.java) }
            } catch (e: IOException) {
                logger.error("reading config file", e)
            }
        }

        return Config()
    }

    fun saveConfig() {
        try {
            CONFIG_FILE.writer().use { Gson().toJson(config, Config::class.java, it) }
        } catch (e: IOException) {
            logger.error("saving config", e)
        }
    }

    private fun clientTick(mc: MinecraftClient) {
        if (active) {
            if (mc.player?.isDead == true) {
                if (config.deactivateOnDeath) {
                    active = false
                }
            } else {
                handleHolding(holdingLeft)
                handleHolding(holdingRight)
                handleJumping(holdingJump)
            }
        }

        // key presses
        while (toggleFunction.wasPressed()) {
            active = !active
        }
        while (openConfig.wasPressed()) {
            active = false
            mc.setScreen(OptionsScreen())
        }
    }

    private fun handleJumping(holding: Holding) {
        if(holding.config.active) {
            if(holding.config.spamming) {
                if (holding.timeout-- <= 1) {
                    holding.key.isPressed = holding.timeout == 0

                    if(holding.timeout <= -1) {
                        if (holding.config.cooldown >= 1) {
                            holding.timeout = holding.config.cooldown
                        } else {
                            holding.timeout = 1
                        }
                    }
                } else {
                    holding.key.isPressed = false
                }
            } else {
                holding.key.isPressed = true
            }
        }
    }

    // handler for attack and use
    private fun handleHolding(holding: Holding) {
        if (holding.config.active) {
            if (holding.config.spamming) {
                if (holding.timeout-- <= 0) {
                    // reset the timeout
                    holding.timeout = holding.config.cooldown

                    val trace: HitResult? = client?.crosshairTarget

                    if (trace?.type == HitResult.Type.ENTITY) {
                        trace as EntityHitResult

                        when (holding) {
                            // attack entity
                            holdingLeft -> {
                                client?.interactionManager?.attackEntity(
                                    client?.player,
                                    trace.entity
                                )
                                // cosmetic
                                client?.player?.swingHand(Hand.MAIN_HAND)
                                // stop using item when you attack entities
                                client?.interactionManager?.stopUsingItem(client?.player)
                            }
                            // interact with entity
                            holdingRight -> {
                                val result = client?.interactionManager?.interactEntity(
                                    client!!.player,
                                    trace.entity,
                                    Hand.MAIN_HAND
                                )

                                if (result?.shouldSwingHand() == true)
                                    client?.player?.swingHand(Hand.MAIN_HAND)

                                if (result?.isAccepted == false) {
                                    // attempt again with offhand
                                    val result1 = client?.interactionManager?.interactEntity(
                                        client!!.player,
                                        trace.entity,
                                        Hand.OFF_HAND
                                    )

                                    if (result1?.shouldSwingHand() == true)
                                        client?.player?.swingHand(Hand.OFF_HAND)
                                }
                            }
                        }
                    } else if ((holding.config as? Config.AttackConfig)?.noBlocks != true && trace?.type == HitResult.Type.BLOCK) {
                        trace as BlockHitResult

                        when (holding.key) {
                            // attack block (left click)
                            client?.options?.attackKey -> {
                                client?.interactionManager?.attackBlock(trace.blockPos, trace.side)
                                // stop using item when you hit a block
                                client?.interactionManager?.stopUsingItem(client?.player)
                            }
                            // interact with block (right click)
                            client?.options?.useKey -> {
                                val result = client?.interactionManager?.interactBlock(
                                    client!!.player,
                                    Hand.MAIN_HAND,
                                    trace
                                )
                                if (result?.shouldSwingHand() == true)
                                    client?.player?.swingHand(Hand.MAIN_HAND)

                                if (result?.isAccepted == false) {
                                    // attempt again with offhand
                                    val result1 = client?.interactionManager?.interactBlock(
                                        client!!.player,
                                        Hand.OFF_HAND,
                                        trace
                                    )

                                    if (result1?.shouldSwingHand() == true)
                                        client?.player?.swingHand(Hand.OFF_HAND)

                                    if (result1?.isAccepted == false) {
                                        client?.run {
                                            val result2 = interactionManager?.interactItem(player, Hand.MAIN_HAND)

                                            if (result2?.isAccepted == false) {
                                                interactionManager?.interactItem(player, Hand.OFF_HAND)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (holding == holdingRight) client?.run {
                            val result2 = interactionManager?.interactItem(player, Hand.MAIN_HAND)

                            if (result2?.isAccepted == false) {
                                interactionManager?.interactItem(player, Hand.OFF_HAND)
                            }
                        }
                    }
                }
            } else {
                // if not spamming... then just hold the button?
                // p. s. button is released when auto clicker is not active
                holding.key.isPressed = true
            }
        }
    }
}