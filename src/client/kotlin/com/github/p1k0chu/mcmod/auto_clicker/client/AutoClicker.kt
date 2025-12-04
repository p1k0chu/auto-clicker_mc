package com.github.p1k0chu.mcmod.auto_clicker.client

import com.google.gson.Gson
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.KeyMapping
import net.minecraft.client.DeltaTracker
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ShieldItem
import net.minecraft.world.InteractionResult
import net.minecraft.util.CommonColors
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
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
    val client: Minecraft?
        get() = Minecraft.getInstance()

    // config folder is automatically created by fabric loader
    val CONFIG_FOLDER: Path = FabricLoader.getInstance().configDir

    // the file might not exist
    val CONFIG_FILE: Path = CONFIG_FOLDER.resolve("$MOD_ID.json")

    private val openConfig: KeyMapping
    private val toggleFunction: KeyMapping

    init {
        val keyMappingCategory = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, MOD_ID))

        openConfig = KeyMapping(Language.OPEN_SETTINGS.key, GLFW.GLFW_KEY_O, keyMappingCategory)
        toggleFunction = KeyMapping(Language.TOGGLE.key, GLFW.GLFW_KEY_I, keyMappingCategory)
    }

    val config: Config = loadConfig()
    var active: Boolean = false
        private set(v) {
            field = v
            if (v) {
                // reset all timeouts
                holdingLeft.timeout = holdingLeft.config.cooldown
                holdingRight.timeout = holdingRight.config.cooldown
                holdingJump.timeout = holdingJump.config.cooldown
            } else {
                // release all the buttons
                holdingLeft.key.setDown(false)
                holdingRight.key.setDown(false)
                holdingJump.key.setDown(false)
            }
        }

    lateinit var holdingLeft: Holding
    lateinit var holdingRight: Holding
    lateinit var holdingJump: Holding

    override fun onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register { mc ->
            holdingLeft = Holding(mc.options.keyAttack, config.leftMouse)
            holdingRight = Holding(mc.options.keyUse, config.rightMouse)
            holdingJump = Holding(mc.options.keyJump, config.jump)
        }

        HudRenderCallback.EVENT.register { context: GuiGraphics, tickCounter: DeltaTracker? ->
            if (active) {
                context.drawString(client!!.font, Language.ACTIVE.text, 2, context.guiHeight() - client!!.font.lineHeight - 2, CommonColors.WHITE, true)
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

    private fun clientTick(mc: Minecraft) {
        if (active) {
            if (mc.player?.isDeadOrDying == true) {
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
        while (toggleFunction.consumeClick()) {
            active = !active
        }
        while (openConfig.consumeClick()) {
            active = false
            mc.setScreen(OptionsScreen())
        }
    }

    private fun handleJumping(holding: Holding) {
        if(holding != holdingJump) {
            return
        }

        if(holding.config.active) {
            if(holding.config.spamming) {
                if (holding.timeout-- <= 1) {
                    holding.key.setDown(holding.timeout == 0)

                    if(holding.timeout <= -1) {
                        if (holding.config.cooldown >= 1) {
                            holding.timeout = holding.config.cooldown
                        } else {
                            holding.timeout = 1
                        }
                    }
                } else {
                    holding.key.setDown(false)
                }
            } else {
                holding.key.setDown(true)
            }
        }
    }

    private fun attemptMobAttack(trace: EntityHitResult, holding: Holding) {
        if (holding != holdingLeft) {
            return
        }

        val config = holding.config as? Config.AttackConfig ?: return

        if (isShieldUp() && !config.ignoreShield) {
            return
        }

        if (config.respectWeaponCooldown && !isWeaponReady()) {
            return
        }

        client?.gameMode?.attack(
            client?.player,
            trace.entity
        )
        // cosmetic
        client?.player?.swing(InteractionHand.MAIN_HAND)

        // stop using item when you attack entities
        client?.gameMode?.releaseUsingItem(client?.player)

        // reset the timeout
        holding.timeout = holding.config.cooldown
    }

    private fun isWeaponReady(): Boolean = client?.player?.getAttackStrengthScale(0f) == 1f

    private fun attemptMobInteract(trace: EntityHitResult, holding: Holding) {
        if (holding != holdingRight) {
            return
        }

        InteractionHand.entries.forEach { hand: InteractionHand ->
            val result = client?.gameMode?.interact(
                client!!.player,
                trace.entity,
                hand
            )
            if(result is InteractionResult.Success) {
                if(result.swingSource != InteractionResult.SwingSource.NONE) {
                    client?.player?.swing(hand)
                }

                // reset the timeout
                holding.timeout = holding.config.cooldown

                return
            }
        }
    }

    private fun attemptBlockAttack(trace: BlockHitResult, holding: Holding) {
        if(holding != holdingLeft) {
            return
        }

        val config = holding.config as? Config.AttackConfig ?: return

        // if we ignore blocks or
        // if we do NOT ignore the shield, and its up - return
        if(config.ignoreBlocks || !config.ignoreShield && isShieldUp()) {
            return
        }

        val interactionManager = client?.gameMode ?: return

        if (interactionManager.startDestroyBlock(trace.blockPos, trace.direction)) {
            // stop using item when you hit a block
            interactionManager.releaseUsingItem(client?.player)
        }
        // reset the timeout
        holding.timeout = holding.config.cooldown
    }

    private fun attemptBlockInteract(trace: BlockHitResult, holding: Holding) {
        val config = holding.config as? Config.MouseConfig ?: return

        if (holding != holdingRight || config.ignoreBlocks) {
            return
        }

        InteractionHand.entries.forEach { hand: InteractionHand ->
            val result = client?.gameMode?.useItemOn(
                client!!.player,
                hand,
                trace
            )
            if(result is InteractionResult.Success) {
                if(result.swingSource != InteractionResult.SwingSource.NONE) {
                    client?.player?.swing(hand)
                }
                // reset the timeout
                holding.timeout = config.cooldown

                return
            }
        }
    }

    private fun itemUse(holding: Holding) {
        if(holding != holdingRight) {
            return
        }

        InteractionHand.entries.forEach { hand: InteractionHand ->
            val result = client?.gameMode?.useItem(client?.player, hand)

            if(result is InteractionResult.Success) {
                if(result.swingSource != InteractionResult.SwingSource.NONE) {
                    client?.player?.swing(hand)
                }

                holding.timeout = holding.config.cooldown

                return
            }
        }
    }

    // handler for attack and use
    private fun handleHolding(holding: Holding) {
        if (holding.config.active) {
            if (holding.config.spamming && (holding.config as? Config.AttackConfig)?.respectWeaponCooldown != true) {
                if (holding.timeout-- <= 0) {
                    val trace: HitResult? = client?.hitResult

                    when(trace?.type) {
                        HitResult.Type.ENTITY -> {
                            attemptMobInteract(trace as EntityHitResult, holding)
                            attemptMobAttack(trace, holding)
                        }
                        HitResult.Type.BLOCK -> {
                            attemptBlockAttack(trace as BlockHitResult, holding)
                            attemptBlockInteract(trace, holding)
                        }
                        HitResult.Type.MISS -> {
                            itemUse(holding)
                        }
                        else -> {}
                    }
                }
            } else if((holding.config as? Config.AttackConfig)?.respectWeaponCooldown == true) {
                val trace: HitResult? = client?.hitResult

                if(trace?.type == HitResult.Type.ENTITY) {
                    attemptMobAttack(trace as EntityHitResult, holding)
                } else if(trace?.type == HitResult.Type.BLOCK && (holding.config as? Config.MouseConfig)?.ignoreBlocks != true) {
                    attemptBlockAttack(trace as BlockHitResult, holding)
                }
            } else {
                // if not spamming... then just hold the button?
                // p. s. button is released when auto clicker is not active
                holding.key.setDown(true)
            }
        }
    }

    private fun isShieldUp(): Boolean {
        if(client?.player?.isUsingItem != true) {
            return false
        }

        return client?.player?.useItem?.item is ShieldItem
    }
}
