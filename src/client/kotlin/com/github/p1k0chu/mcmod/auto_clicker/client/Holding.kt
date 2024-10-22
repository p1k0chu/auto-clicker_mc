package com.github.p1k0chu.mcmod.auto_clicker.client

import net.minecraft.client.option.KeyBinding

class Holding(
    val key: KeyBinding,
    val config: Config.SharedConfig
) {
    var timeout: Int = config.cooldown
}