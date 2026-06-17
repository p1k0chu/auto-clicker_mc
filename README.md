# Auto Clicker (fabric)

This is an auto clicker mod for minecraft, made with automatic farms
in mind.

### Keybinds

Theres two **unbound by default** keys: activate/deactivate toggle and
opening settings.

You have to open minecraft's keybind settings and
bing them to the keys you are most comfortable with.

### Settings

In enabled state, it will perform actions based on the settings you choose:

- it can spam your attack/use/jump key with a set delay (or no delay)
    > when 'Spamming' is turned **on**
- hold your attack/use/jump key.
    > when 'Spamming' is turned **off**
- each button can be enabled separately or combined.
- an option to automatically disable auto clicker on death (on by default)

Keys have individual settings:

- you can change the delay, or set to 0 to have no delay. (delay is in ticks)
- **'Spamming'**: 
    - when enabled the key will be clicked repeatedly with a set delay (or no delay)
    - when disabled the key will be held. delay, and other options below dont have an effect in this mode.
- **'ignore blocks'** mode: wont click on blocks. for example, you would attack mobs but not blocks.
- **'ignore shield'** mode: on by default. when disabled, autoclicker wont attack when your shield is up.
- **'Weapon cooldown'**: true/false': when true, will only attack when the weapon is fully charged

In disabled state, the mod doesn't do anything.  
There is a label on your screen to let you know when the autoclicker is in enabled state

### Dependencies
[Fabric API](https://modrinth.com/mod/fabric-api) and [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)

