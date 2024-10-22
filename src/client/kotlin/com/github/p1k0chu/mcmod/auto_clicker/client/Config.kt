package com.github.p1k0chu.mcmod.auto_clicker.client

class Config(
    val leftMouse: SharedConfig = AttackConfig(),
    val rightMouse: SharedConfig = AttackConfig(),
    val jump: SharedConfig = SharedConfig(),
    var deactivateOnDeath: Boolean = true
) {
    class AttackConfig(
        cooldown: Int = 0,
        active: Boolean = false,
        spamming: Boolean = false,
        var noBlocks: Boolean = false
    ) : SharedConfig(cooldown, active, spamming)

    open class SharedConfig(
        var cooldown: Int = 0,
        var active: Boolean = false,
        var spamming: Boolean = false,
    )
}