package com.github.p1k0chu.mcmod.auto_clicker.client

class Config(
    val leftMouse: SharedConfig = SharedConfig(),
    val rightMouse: SharedConfig = SharedConfig(),
    val jump: SharedConfig = SharedConfig(),
    var deactivateOnDeath: Boolean = true
) {
    open class SharedConfig(
        var cooldown: Int = 0,
        var active: Boolean = false,
        var spamming: Boolean = false
    )
}