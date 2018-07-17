package one.oktw.galaxy.internal.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.*
import org.spongepowered.api.data.DataRegistration
import org.spongepowered.api.item.ItemTypes.STICK
import org.spongepowered.api.item.inventory.ItemStack

class DataRegister {
    private val plugin = main.plugin

    init {
        // UUID
        DataRegistration.builder()
            .dataName("UUID").manipulatorId("uuid")
            .dataClass(DataUUID::class.java).immutableClass(DataUUID.Immutable::class.java)
            .builder(DataUUID.Builder())
            .buildAndRegister(plugin)

        //Overheat
        DataRegistration.builder()
            .dataName("Overheat").manipulatorId("overheat")
            .dataClass(DataOverheat::class.java).immutableClass(DataOverheat.Immutable::class.java)
            .builder(DataOverheat.Builder())
            .buildAndRegister(plugin)

        // Scoping
        DataRegistration.builder()
            .dataName("Enable").manipulatorId("enable")
            .dataClass(DataEnable::class.java).immutableClass(DataEnable.Immutable::class.java)
            .builder(DataEnable.Builder())
            .buildAndRegister(plugin)

        // Upgrade
        DataRegistration.builder()
            .dataName("Upgrade").manipulatorId("upgrade")
            .dataClass(DataUpgrade::class.java).immutableClass(DataUpgrade.Immutable::class.java)
            .builder(DataUpgrade.Builder())
            .buildAndRegister(plugin)

        // Item Type
        DataRegistration.builder()
            .dataName("ItemType").manipulatorId("itemType")
            .dataClass(DataItemType::class.java).immutableClass(DataItemType.Immutable::class.java)
            .builder(DataItemType.Builder())
            .buildAndRegister(plugin)

        // Fake Block Type
        DataRegistration.builder()
            .dataName("BlockType").manipulatorId("block_type")
            .dataClass(DataBlockType::class.java).immutableClass(DataBlockType.Immutable::class.java)
            .builder(DataBlockType.Builder())
            .buildAndRegister(plugin)

        // just load custom data
        ItemStack.builder()
            .itemType(STICK)
            .itemData(DataUUID())
            .itemData(DataOverheat())
            .itemData(DataEnable())
            .itemData(DataUpgrade())
            .itemData(DataItemType())
            .itemData(DataBlockType())
            .build()
    }
}
