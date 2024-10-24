package com.github.p1k0chu.mcmod.auto_clicker.client

import net.minecraft.text.MutableText
import net.minecraft.text.Text

enum class Language(val key: String) {
    RESPECT_WEAPON_COOLDOWN_DESC("auto_clicker.hud.respect_weapon_cd_desc"),
    RESPECT_WEAPON_COOLDOWN("auto_clicker.hud.respect_weapon_cd"),
    IGNORE_SHIELD("auto_clicker.hud.ignore_shield"),
    IGNORE_SHIELD_DESC("auto_clicker.hud.ignore_shield_desc"),
    IGNORE_BLOCKS("auto_clicker.hud.ignore_blocks"),
    IGNORE_BLOCKS_DESC("auto_clicker.hud.ignore_blocks_desc"),
    SPAMMING_DESCRIPTION("auto_clicker.hud.spamming_desc"),
    DISABLE_ON_DEATH("auto_clicker.hud.disable_on_death"),
    ACTIVE_BUTTON("auto_clicker.hud.active_btn"),
    ACTIVE("auto_clicker.hud.active"),
    SETTINGS_CATEGORY("category.auto_clicker"),
    TOGGLE("auto_clicker.key.toggle-functionality"),
    OPEN_SETTINGS("auto_clicker.key.open-gui"),
    COOLDOWN("auto_clicker.hud.delay"),
    LEFT_MOUSE_BUTTON("auto_clicker.hud.left_mouse"),
    RIGHT_MOUSE_BUTTON("auto_clicker.hud.right_mouse"),
    JUMP("auto_clicker.hud.jump_button"),
    SPAMMING("auto_clicker.hud.spamming");

    val text: MutableText = Text.translatable(key)

    fun getText(vararg args: Any): MutableText = Text.translatable(key, *args)
}