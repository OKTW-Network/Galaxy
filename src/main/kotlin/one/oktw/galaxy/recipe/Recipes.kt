package one.oktw.galaxy.recipe

import one.oktw.galaxy.Main
import one.oktw.galaxy.block.data.FakeBlockItem
import one.oktw.galaxy.block.enums.CustomBlocks
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.MaterialType
import one.oktw.galaxy.item.enums.ToolType
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.item.type.Material
import one.oktw.galaxy.item.type.Gun
import one.oktw.galaxy.item.type.Tool
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.recipe.crafting.Ingredient.of
import java.util.Arrays.asList

class Recipes {
    companion object {
        private val lang = Main.languageService.getDefaultLanguage()

        enum class Type {
            ALL,
            TOOL,
            MATERIAL,
            MACHINE,
            WEAPON
        }

        private val materials: List<HiTechCraftingRecipe> = asList(
            HiTechCraftingRecipe.builder()
                .add(of(ItemTypes.CLAY), 8)
                .add(of(ItemTypes.OBSIDIAN), 8)
                .cost(100)
                .result(Material(MaterialType.PART_RAW_BASE).createItemStack())
                .build(),

            HiTechCraftingRecipe.builder()
                .add(of(ItemTypes.REDSTONE), 20)
                .add(of(ItemTypes.GOLD_INGOT), 8)
                .add(of(ItemTypes.QUARTZ), 4)
                .add(Material(MaterialType.PART_BASE), 1)
                .cost(100)
                .result(Material(MaterialType.CPU).createItemStack())
                .build()
        )

        private val tools: List<HiTechCraftingRecipe> = asList(
            HiTechCraftingRecipe.builder()
                .add(of(ItemTypes.IRON_INGOT), 3)
                .add(of(ItemTypes.STICK), 1)
                .cost(0)
                .result(Tool(ToolType.WRENCH).createItemStack())
                .build()
        )

        private val machines: List<HiTechCraftingRecipe> = asList(
            HiTechCraftingRecipe.builder()
                .add(of(ItemTypes.EMERALD_BLOCK), 1)
                .add(of(ItemTypes.ENDER_PEARL), 1)
                .cost(0)
                .result(FakeBlockItem(CustomBlocks.ELEVATOR).createItemStack())
                .build()
        )

        private val weapons: List<HiTechCraftingRecipe> = asList()

        private val all: List<HiTechCraftingRecipe> = ArrayList<HiTechCraftingRecipe>().plus(tools).plus(materials).plus(machines).plus(weapons)

        val catalog: Map<Type, List<HiTechCraftingRecipe>> = mapOf(
            Type.ALL to all,
            Type.TOOL to tools,
            Type.MATERIAL to materials,
            Type.MACHINE to machines,
            Type.WEAPON to weapons
        )

        val icons: Map<Type, ItemStackSnapshot> = mapOf(
            Type.ALL to Button(ButtonType.PLUS).createItemStack().createSnapshot(),
            Type.TOOL to Tool(ToolType.WRENCH).createItemStack().createSnapshot(),
            Type.MATERIAL to Material(MaterialType.PART_RAW_BASE).createItemStack().createSnapshot(),
            Type.MACHINE to FakeBlockItem(CustomBlocks.ELEVATOR).createItemStack().createSnapshot(),
            Type.WEAPON to Gun().createItemStack().createSnapshot()
        )

        val names: Map<Type, String> = mapOf(
            Type.ALL to lang["recipe.catalog.ALL"],
            Type.TOOL to lang["recipe.catalog.TOOL"],
            Type.MATERIAL to lang["recipe.catalog.MATERIAL"],
            Type.MACHINE to lang["recipe.catalog.MACHINE"],
            Type.WEAPON to lang["recipe.catalog.WEAPON"]
        )

        val types: List<Type> = asList(Type.ALL, Type.MACHINE, Type.TOOL, Type.WEAPON, Type.MATERIAL)
    }
}
