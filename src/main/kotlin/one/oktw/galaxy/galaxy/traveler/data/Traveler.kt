package one.oktw.galaxy.galaxy.traveler.data

import one.oktw.galaxy.Main.Companion.dummyUUID
import one.oktw.galaxy.economy.StarDustKeeper
import one.oktw.galaxy.galaxy.enums.Group
import one.oktw.galaxy.galaxy.enums.Group.VISITOR
import one.oktw.galaxy.item.type.Item
import one.oktw.galaxy.item.type.Upgrade
import org.spongepowered.api.item.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList

data class Traveler(
    val uuid: UUID = dummyUUID,
    var group: Group = VISITOR,
    var armor: ArrayList<Upgrade> = ArrayList(),
    var item: ArrayList<Item> = ArrayList(),
    var inventory: List<ItemStack> = ArrayList(),
    var experience: Int = 0
) : StarDustKeeper()
