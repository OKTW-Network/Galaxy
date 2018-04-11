package one.oktw.galaxy.internal.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.*
import org.spongepowered.api.data.DataRegistration

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
            .dataName("Type").manipulatorId("type")
            .dataClass(DataType::class.java).immutableClass(DataType.Immutable::class.java)
            .builder(DataType.Builder())
            .buildAndRegister(plugin)
    }
}
