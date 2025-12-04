package com.github.p1k0chu.mcmod.auto_clicker.client

import net.minecraft.client.KeyMapping

class Holding(
    val key: KeyMapping,
    val config: Config.SharedConfig
) {
    var timeout: Int = config.cooldown
}