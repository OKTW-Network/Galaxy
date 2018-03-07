package one.oktw.galaxy.types.item

import one.oktw.galaxy.enums.ArmorPart
import org.spongepowered.api.text.Text

data class Armor(val part: ArmorPart, var name: Text, var lore: List<Text>) : Item