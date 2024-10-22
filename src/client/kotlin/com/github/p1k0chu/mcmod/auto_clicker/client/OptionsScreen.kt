package com.github.p1k0chu.mcmod.auto_clicker.client

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import net.minecraft.util.Colors

class OptionsScreen(name: String? = null) : Screen(Text.of(name ?: "Auto Clicker Settings")) {
    protected override fun init() {
        val w = textRenderer.getWidth(Language.LEFT_MOUSE_BUTTON.text) + 20

        // left mouse button
        // active toggle
        this.addDrawableChild(
            ButtonWidget.Builder(Language.ACTIVE_BUTTON.getText(AutoClicker.config.leftMouse.active)) { btn: ButtonWidget ->
                AutoClicker.config.leftMouse.active = !AutoClicker.config.leftMouse.active
                btn.message = Language.ACTIVE_BUTTON.getText(AutoClicker.config.leftMouse.active)

                AutoClicker.saveConfig()
            }
                .dimensions((width / 2 - w * 1.5).toInt(), height / 2, w, 30)
                .build()
        )
        // spamming toggle
        this.addDrawableChild(
            ButtonWidget.Builder(Language.SPAMMING.getText(AutoClicker.config.leftMouse.spamming)) { btn: ButtonWidget ->
                AutoClicker.config.leftMouse.spamming = !AutoClicker.config.leftMouse.spamming
                btn.message = Language.SPAMMING.getText(AutoClicker.config.leftMouse.spamming)

                AutoClicker.saveConfig()
            }
                .dimensions((width / 2 - w * 1.5).toInt(), height / 2 + 30, w, 30)
                .build()
        )

        // input field for left mouse button delay
        val leftDelayWidget = TextFieldWidget(
            textRenderer,
            (width / 2 - w * 1.5).toInt(), height / 2 + 60,
            w, 30,
            Text.of(AutoClicker.config.leftMouse.cooldown.toString())
        )
        leftDelayWidget.text = AutoClicker.config.leftMouse.cooldown.toString()
        this.addDrawableChild(leftDelayWidget)

        leftDelayWidget.setChangedListener { value: String ->
            // trim leading zero
            if (value.startsWith("0") && value.length > 1) {
                leftDelayWidget.text = value.dropWhile { ch -> ch == '0' }
                return@setChangedListener
            }

            try {
                AutoClicker.config.leftMouse.cooldown = value.toInt()
            } catch (_: Exception) {
                leftDelayWidget.text = "0"
            }

            AutoClicker.saveConfig()
        }

        // right mouse button
        // active toggle
        this.addDrawableChild(
            ButtonWidget.Builder(Language.ACTIVE_BUTTON.getText(AutoClicker.config.rightMouse.active)) { btn: ButtonWidget ->
                AutoClicker.config.rightMouse.active = !AutoClicker.config.rightMouse.active
                btn.message = Language.ACTIVE_BUTTON.getText(AutoClicker.config.rightMouse.active)

                AutoClicker.saveConfig()
            }
                .dimensions((width / 2 - w * 0.5).toInt(), height / 2, w, 30)
                .build()
        )
        // spamming toggle
        this.addDrawableChild(
            ButtonWidget.Builder(Language.SPAMMING.getText(AutoClicker.config.rightMouse.spamming)) { btn: ButtonWidget ->
                AutoClicker.config.rightMouse.spamming = !AutoClicker.config.rightMouse.spamming
                btn.message = Language.SPAMMING.getText(AutoClicker.config.rightMouse.spamming)

                AutoClicker.saveConfig()
            }
                .dimensions((width / 2 - w * 0.5).toInt(), height / 2 + 30, w, 30)
                .build()
        )

        // input field for right mouse button delay
        val rightDelayWidget = TextFieldWidget(
            textRenderer,
            (width / 2 - w * 0.5).toInt(), height / 2 + 60,
            w, 30,
            Text.of(AutoClicker.config.rightMouse.cooldown.toString())
        )
        rightDelayWidget.text = AutoClicker.config.rightMouse.cooldown.toString()
        this.addDrawableChild(rightDelayWidget)

        rightDelayWidget.setChangedListener { value: String ->
            // trim leading zero
            if (value.startsWith("0") && value.length > 1) {
                rightDelayWidget.text = value.dropWhile { ch -> ch == '0' }
                return@setChangedListener
            }

            try {
                AutoClicker.config.rightMouse.cooldown = value.toInt()
            } catch (_: Exception) {
                rightDelayWidget.text = "0"
            }

            AutoClicker.saveConfig()
        }

        // jumping
        // active toggle
        this.addDrawableChild(
            ButtonWidget.Builder(Language.ACTIVE_BUTTON.getText(AutoClicker.config.jump.active)) { btn: ButtonWidget ->
                AutoClicker.config.jump.active = !AutoClicker.config.jump.active
                btn.message = Language.ACTIVE_BUTTON.getText(AutoClicker.config.jump.active)

                AutoClicker.saveConfig()
            }
                .dimensions((width / 2 + w * 0.5).toInt(), height / 2, w, 30)
                .build()
        )
        // spamming toggle
        this.addDrawableChild(
            ButtonWidget.Builder(Language.SPAMMING.getText(AutoClicker.config.jump.spamming)) { btn: ButtonWidget ->
                AutoClicker.config.jump.spamming = !AutoClicker.config.jump.spamming
                btn.message = Language.SPAMMING.getText(AutoClicker.config.jump.spamming)

                AutoClicker.saveConfig()
            }
                .dimensions((width / 2 + w * 0.5).toInt(), height / 2 + 30, w, 30)
                .build()
        )

        // input field for right mouse button delay
        val jumpDelayWidget = TextFieldWidget(
            textRenderer,
            (width / 2 + w * 0.5).toInt(), height / 2 + 60,
            w, 30,
            Text.of(AutoClicker.config.jump.cooldown.toString())
        )
        jumpDelayWidget.text = AutoClicker.config.jump.cooldown.toString()
        this.addDrawableChild(jumpDelayWidget)

        jumpDelayWidget.setChangedListener { value: String ->
            // trim leading zero
            if (value.startsWith("0") && value.length > 1) {
                jumpDelayWidget.text = value.dropWhile { ch -> ch == '0' }
                return@setChangedListener
            }

            try {
                AutoClicker.config.jump.cooldown = value.toInt()
            } catch (_: Exception) {
                jumpDelayWidget.text = "0"
            }

            AutoClicker.saveConfig()
        }
    }

    override fun render(
        context: DrawContext?,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        this.renderBackground(context, mouseX, mouseY, delta)
        super.render(context, mouseX, mouseY, delta)

        val w = textRenderer.getWidth(Language.LEFT_MOUSE_BUTTON.text) + 20

        context?.drawCenteredTextWithShadow(
            textRenderer,
            Language.LEFT_MOUSE_BUTTON.text,
            width/2 - w, height / 2 - 20,
            Colors.WHITE,
        )

        context?.drawCenteredTextWithShadow(
            textRenderer,
            Language.RIGHT_MOUSE_BUTTON.text,
            width/2, height / 2 - 20,
            Colors.WHITE,
        )

        context?.drawCenteredTextWithShadow(
            textRenderer,
            Language.JUMP.text,
            width/2 + w, height / 2 - 20,
            Colors.WHITE,
        )
    }
}