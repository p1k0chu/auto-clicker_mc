package com.github.p1k0chu.mcmod.auto_clicker.client

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors

class OptionsScreen(name: String? = null) : Screen(Component.nullToEmpty(name ?: "Auto Clicker Settings")) {
    private val tooltipsMap = mutableMapOf<GuiEventListener, Component>()

    protected override fun init() {
        // left mouse button
        // active toggle
        this.addRenderableWidget(
            Button.Builder(Language.ACTIVE_BUTTON.getText(AutoClicker.config.leftMouse.active)) { btn: Button ->
                AutoClicker.config.leftMouse.active = !AutoClicker.config.leftMouse.active
                btn.message = Language.ACTIVE_BUTTON.getText(AutoClicker.config.leftMouse.active)

                AutoClicker.saveConfig()
            }
                .bounds((width / 2 - 160).toInt(), height / 2 - 50, 100, 20)
                .build()
        )
        // spamming toggle
        tooltipsMap.put(this.addRenderableWidget(
            Button.Builder(Language.SPAMMING.getText(AutoClicker.config.leftMouse.spamming)) { btn: Button ->
                AutoClicker.config.leftMouse.spamming = !AutoClicker.config.leftMouse.spamming
                btn.message = Language.SPAMMING.getText(AutoClicker.config.leftMouse.spamming)

                AutoClicker.saveConfig()
            }
                .bounds((width / 2 - 160).toInt(), height / 2 - 29, 100, 20)
                .build()
        ), Language.SPAMMING_DESCRIPTION.text)

        // input field for left mouse button delay
        val leftDelayWidget = EditBox(
            font,
            (width / 2 - 160).toInt(), height / 2 - 8,
            100, 20,
            Component.nullToEmpty(AutoClicker.config.leftMouse.cooldown.toString())
        )
        leftDelayWidget.setValue(AutoClicker.config.leftMouse.cooldown.toString())
        tooltipsMap.put(this.addRenderableWidget(leftDelayWidget), Language.COOLDOWN.text)

        leftDelayWidget.setResponder { value: String ->
            // trim leading zero
            if (value.startsWith("0") && value.length > 1) {
                leftDelayWidget.setValue(value.dropWhile { ch -> ch == '0' })
                return@setResponder
            }

            try {
                AutoClicker.config.leftMouse.cooldown = value.toInt()
            } catch (_: Exception) {
                leftDelayWidget.setValue("0")
            }

            AutoClicker.saveConfig()
        }

        // ignore blocks toggle
        tooltipsMap.put(this.addRenderableWidget(Button.Builder(Language.IGNORE_BLOCKS.getText(AutoClicker.config.leftMouse.ignoreBlocks)) { btn: Button ->
            AutoClicker.config.leftMouse.ignoreBlocks = !AutoClicker.config.leftMouse.ignoreBlocks
            btn.message = Language.IGNORE_BLOCKS.getText(AutoClicker.config.leftMouse.ignoreBlocks)
        }
            .bounds(width / 2 - 160, height / 2 + 14, 100, 20)
            .build()
        ), Language.IGNORE_BLOCKS_DESC.text)

        // ignore shield toggle
        tooltipsMap.put(this.addRenderableWidget(Button.Builder(Language.IGNORE_SHIELD.getText(AutoClicker.config.leftMouse.ignoreShield)) { btn: Button ->
            AutoClicker.config.leftMouse.ignoreShield = !AutoClicker.config.leftMouse.ignoreShield
            btn.message = Language.IGNORE_SHIELD.getText(AutoClicker.config.leftMouse.ignoreShield)
        }
            .bounds(width / 2 - 160, height / 2 + 36, 100, 20)
            .build()
        ), Language.IGNORE_SHIELD_DESC.text)

        // respect weapon cooldown toggle
        tooltipsMap.put(this.addRenderableWidget(Button.Builder(Language.RESPECT_WEAPON_COOLDOWN.getText(AutoClicker.config.leftMouse.respectWeaponCooldown)) { btn: Button ->
            AutoClicker.config.leftMouse.respectWeaponCooldown = !AutoClicker.config.leftMouse.respectWeaponCooldown
            btn.message = Language.RESPECT_WEAPON_COOLDOWN.getText(AutoClicker.config.leftMouse.respectWeaponCooldown)
        }
            .bounds(width / 2 - 160, height / 2 + 58, 100, 20)
            .build()
        ), Language.RESPECT_WEAPON_COOLDOWN_DESC.text)

        // right mouse button
        // active toggle
        this.addRenderableWidget(
            Button.Builder(Language.ACTIVE_BUTTON.getText(AutoClicker.config.rightMouse.active)) { btn: Button ->
                AutoClicker.config.rightMouse.active = !AutoClicker.config.rightMouse.active
                btn.message = Language.ACTIVE_BUTTON.getText(AutoClicker.config.rightMouse.active)

                AutoClicker.saveConfig()
            }
                .bounds((width / 2 - 50).toInt(), height / 2 - 50, 100, 20)
                .build()
        )
        // spamming toggle
        tooltipsMap.put(this.addRenderableWidget(
            Button.Builder(Language.SPAMMING.getText(AutoClicker.config.rightMouse.spamming)) { btn: Button ->
                AutoClicker.config.rightMouse.spamming = !AutoClicker.config.rightMouse.spamming
                btn.message = Language.SPAMMING.getText(AutoClicker.config.rightMouse.spamming)

                AutoClicker.saveConfig()
            }
                .bounds((width / 2 - 50).toInt(), height / 2 - 29, 100, 20)
                .build()
        ), Language.SPAMMING_DESCRIPTION.text)

        // input field for right mouse button delay
        val rightDelayWidget = EditBox(
            font,
            (width / 2 - 50).toInt(), height / 2 - 8,
            100, 20,
            Component.nullToEmpty(AutoClicker.config.rightMouse.cooldown.toString())
        )
        rightDelayWidget.setValue(AutoClicker.config.rightMouse.cooldown.toString())
        tooltipsMap.put(this.addRenderableWidget(rightDelayWidget), Language.COOLDOWN.text)

        rightDelayWidget.setResponder { value: String ->
            // trim leading zero
            if (value.startsWith("0") && value.length > 1) {
                rightDelayWidget.setValue(value.dropWhile { ch -> ch == '0' })
                return@setResponder
            }

            try {
                AutoClicker.config.rightMouse.cooldown = value.toInt()
            } catch (_: Exception) {
                rightDelayWidget.setValue("0")
            }

            AutoClicker.saveConfig()
        }

        // ignore blocks toggle
        tooltipsMap.put(this.addRenderableWidget(Button.Builder(Language.IGNORE_BLOCKS.getText(AutoClicker.config.rightMouse.ignoreBlocks)) { btn: Button ->
            AutoClicker.config.rightMouse.ignoreBlocks = !AutoClicker.config.rightMouse.ignoreBlocks
            btn.message = Language.IGNORE_BLOCKS.getText(AutoClicker.config.rightMouse.ignoreBlocks)
        }
            .bounds(width / 2 - 50, height / 2 + 14, 100, 20)
            .build()
        ), Language.IGNORE_BLOCKS_DESC.text)

        // jumping
        // active toggle
        this.addRenderableWidget(
            Button.Builder(Language.ACTIVE_BUTTON.getText(AutoClicker.config.jump.active)) { btn: Button ->
                AutoClicker.config.jump.active = !AutoClicker.config.jump.active
                btn.message = Language.ACTIVE_BUTTON.getText(AutoClicker.config.jump.active)

                AutoClicker.saveConfig()
            }
                .bounds((width / 2 + 60).toInt(), height / 2 - 50, 100, 20)
                .build()
        )
        // spamming toggle
        tooltipsMap.put(this.addRenderableWidget(
            Button.Builder(Language.SPAMMING.getText(AutoClicker.config.jump.spamming)) { btn: Button ->
                AutoClicker.config.jump.spamming = !AutoClicker.config.jump.spamming
                btn.message = Language.SPAMMING.getText(AutoClicker.config.jump.spamming)

                AutoClicker.saveConfig()
            }
                .bounds((width / 2 + 60).toInt(), height / 2 - 29, 100, 20)
                .build()
        ), Language.SPAMMING_DESCRIPTION.text)

        // input field for jumping delay
        val jumpDelayWidget = EditBox(
            font,
            (width / 2 + 60).toInt(), height / 2 - 8,
            100, 20,
            Component.nullToEmpty(AutoClicker.config.jump.cooldown.toString())
        )
        jumpDelayWidget.setValue(AutoClicker.config.jump.cooldown.toString())
        tooltipsMap.put(this.addRenderableWidget(jumpDelayWidget), Language.COOLDOWN.text)

        jumpDelayWidget.setResponder { value: String ->
            // trim leading zero
            if (value.startsWith("0") && value.length > 1) {
                jumpDelayWidget.setValue(value.dropWhile { ch -> ch == '0' })
                return@setResponder
            }

            try {
                AutoClicker.config.jump.cooldown = value.toInt()
            } catch (_: Exception) {
                jumpDelayWidget.setValue("0")
            }

            AutoClicker.saveConfig()
        }

        // disable on death toggle in the corner
        addRenderableWidget(Button.Builder(Language.DISABLE_ON_DEATH.getText(AutoClicker.config.deactivateOnDeath)) { btn: Button ->
            // toggle the config
            AutoClicker.config.deactivateOnDeath = !AutoClicker.config.deactivateOnDeath
            // update the message
            btn.message = Language.DISABLE_ON_DEATH.getText(AutoClicker.config.deactivateOnDeath)
        }
            .size(150, 20)
            .pos(2, height - 22)
            .build()
        )
    }

    override fun render(
        context: GuiGraphics?,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        super.render(context, mouseX, mouseY, delta)

        context?.drawCenteredString(
            font,
            Language.LEFT_MOUSE_BUTTON.text,
            width/2 - 110, height / 2 - 60,
            CommonColors.WHITE,
        )

        context?.drawCenteredString(
            font,
            Language.RIGHT_MOUSE_BUTTON.text,
            width/2, height / 2 - 60,
            CommonColors.WHITE,
        )

        context?.drawCenteredString(
            font,
            Language.JUMP.text,
            width/2 + 110, height / 2 - 60,
            CommonColors.WHITE,
        )

        // draw tooltips
        tooltipsMap.forEach { (widget, text) ->
            if(widget.isMouseOver(mouseX.toDouble(), mouseY.toDouble())) {
                context?.setTooltipForNextFrame(font, text, mouseX, mouseY)
            }
        }
    }
}
