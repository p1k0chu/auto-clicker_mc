plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom") apply false
    id("net.fabricmc.fabric-loom-remap") apply false
}

stonecutter tasks {
    order("publishModrinth")
}

stonecutter parameters {
    dependencies["java"] = node.project.property("java_version") as String
    swaps["centeredText"] = when {
        current.parsed < "26.1" -> "$1.drawCenteredString"
        else -> "$1.centeredText"
    }

    replacements {
        string(current.parsed < "26.1") {
            replace("GuiGraphicsExtractor", "GuiGraphics")
            replace("net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper", "net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper")
            replace("KeyMappingHelper.registerKeyMapping", "KeyBindingHelper.registerKeyBinding")
        }
        string(current.parsed < "1.21.11") {
            replace("Identifier", "ResourceLocation")
        }
    }
}

stonecutter active "26.1.2"
