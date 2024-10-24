package com.github.p1k0chu.mcmod.auto_clicker.client

class Config(
    val leftMouse: AttackConfig = AttackConfig(),
    val rightMouse: MouseConfig = MouseConfig(),
    val jump: SharedConfig = SharedConfig(),
    var deactivateOnDeath: Boolean = true
) {
    /**
     * Adds ignoreShield to MouseConfig
     */
    class AttackConfig(
        cooldown: Int = 0,
        active: Boolean = false,
        spamming: Boolean = false,
        ignoreBlocks: Boolean = false,
        var ignoreShield: Boolean = true
    ) : MouseConfig(cooldown, active, spamming, ignoreBlocks)

    /**
     * Adds ignoreBlocks to SharedConfig
     */
    open class MouseConfig(
        cooldown: Int = 0,
        active: Boolean = false,
        spamming: Boolean = false,
        var ignoreBlocks: Boolean = false,
    ) : SharedConfig(cooldown, active, spamming)

    open class SharedConfig(
        var cooldown: Int = 0,
        var active: Boolean = false,
        var spamming: Boolean = false
    )
}