package one.oktw.galaxy.recipe

import one.oktw.galaxy.block.data.FakeBlockItem
import one.oktw.galaxy.block.enums.CustomBlocks
import one.oktw.galaxy.item.enums.MaterialType
import one.oktw.galaxy.item.enums.ToolType
import one.oktw.galaxy.item.type.Material
import one.oktw.galaxy.item.type.Tool
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.recipe.crafting.Ingredient.of
import java.util.Arrays.asList

class Recipes {
    companion object {
        enum class Type {
            TOOL,
            MATERIAL,
            MACHINE
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
                .add(of(Material(MaterialType.PART_BASE).createItemStack()), 1)
                .cost(100)
                .result(Material(MaterialType.CPU).createItemStack())
                .build()
        )

        private val tools: List<HiTechCraftingRecipe> = asList(
            HiTechCraftingRecipe.builder()
                .add(of(ItemTypes.IRON_INGOT), 3)
                .add(of(ItemTypes.STICK), 1)
                .cost(0)
                .result(Tool(ToolType.WRENCH).createItemStack().createSnapshot())
                .build()
        )

        private val machines: List<HiTechCraftingRecipe> = asList(
            HiTechCraftingRecipe.builder()
                .add(of(ItemTypes.IRON_BLOCK), 5)
                .add(of(ItemTypes.EMERALD_BLOCK), 5)
                .add(of(ItemTypes.ENDER_PEARL), 10)
                .cost(1000)
                .result(FakeBlockItem(CustomBlocks.ELEVATOR).createItemStack())
                .build()
        )

        val catalog: Map<Type, List<HiTechCraftingRecipe>> = mapOf(
            Type.TOOL to tools,
            Type.MATERIAL to materials,
            Type.MACHINE to machines
        )

        val icons: Map<Type, ItemStackSnapshot> = mapOf(
            Type.TOOL to Tool(ToolType.WRENCH).createItemStack().createSnapshot(),
            Type.MATERIAL to Material(MaterialType.PART_RAW_BASE).createItemStack().createSnapshot(),
            Type.MACHINE to FakeBlockItem(CustomBlocks.ELEVATOR).createItemStack().createSnapshot()
        )

        val names: Map<Type, String> = mapOf(
            Type.TOOL to "工具",
            Type.MATERIAL to "材料",
            Type.MACHINE to "機器"
        )

        val types: List<Type> = asList(Type.MACHINE, Type.TOOL, Type.MATERIAL)
    }
}
