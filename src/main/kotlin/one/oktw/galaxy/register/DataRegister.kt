package one.oktw.galaxy.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataOverheat
import one.oktw.galaxy.data.DataScope
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.data.DataUpgrade
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
            .dataName("Scoping").manipulatorId("scoping")
            .dataClass(DataScope::class.java).immutableClass(DataScope.Immutable::class.java)
            .builder(DataScope.Builder())
            .buildAndRegister(plugin)

        // Upgrade
        DataRegistration.builder()
            .dataName("Upgrade").manipulatorId("upgrade")
            .dataClass(DataUpgrade::class.java).immutableClass(DataUpgrade.Immutable::class.java)
            .builder(DataUpgrade.Builder())
            .buildAndRegister(plugin)
    }
}