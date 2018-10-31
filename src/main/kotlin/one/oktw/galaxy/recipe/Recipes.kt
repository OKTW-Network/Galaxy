package one.oktw.galaxy.recipe

import net.minecraft.entity.ai.attributes.AttributeModifier
import one.oktw.galaxy.Main
import one.oktw.galaxy.block.data.FakeBlockItem
import one.oktw.galaxy.block.enums.CustomBlocks
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.MaterialType
import one.oktw.galaxy.item.enums.ToolType
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.item.type.Gun
import one.oktw.galaxy.item.type.Material
import one.oktw.galaxy.item.type.Tool
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.DyeColors
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.enchantment.Enchantment
import org.spongepowered.api.item.enchantment.EnchantmentTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.recipe.crafting.Ingredient
import org.spongepowered.api.item.recipe.crafting.Ingredient.of
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import org.spongepowered.api.text.translation.Translation
import java.util.Arrays.asList

class Recipes {
    companion object {
        private val lang = Main.translationService
        private val lapis = of(ItemStack.of(ItemTypes.DYE, 1).apply { offer(Keys.DYE_COLOR, DyeColors.BLUE) })

        enum class Type {
            ALL,
            TOOL,
            MATERIAL,
            MACHINE,
            WEAPON
        }

        private val materials: List<HiTechCraftingRecipe> = ArrayList<HiTechCraftingRecipe>()

        private val creativeMaterials: List<HiTechCraftingRecipe> = ArrayList<HiTechCraftingRecipe>().apply {
            add(HiTechCraftingRecipe.builder()
                .cost(0)
                .result(Material(MaterialType.PART_RAW_BASE).createItemStack())
                .build()
            )
            add(HiTechCraftingRecipe.builder()
                .cost(0)
                .result(Material(MaterialType.PART_BASE).createItemStack())
                .build()
            )
        }.plus(materials)

        private val tools: List<HiTechCraftingRecipe> = asList(
            HiTechCraftingRecipe.builder()
                .add(of(ItemTypes.IRON_INGOT), 3)
                .add(of(ItemTypes.STICK), 1)
                .cost(0)
                .result(Tool(ToolType.WRENCH).createItemStack())
                .build()
        )

        private val creativeTools = tools

        private val machines: List<HiTechCraftingRecipe> = asList(
            HiTechCraftingRecipe.builder()
                .add(of(ItemTypes.IRON_INGOT), 9)
                .add(of(ItemTypes.ENDER_PEARL), 1)
                .cost(0)
                .result(FakeBlockItem(CustomBlocks.ELEVATOR).createItemStack())
                .build(),

            HiTechCraftingRecipe.builder()
                .add(of(ItemTypes.REDSTONE), 2)
                .add(lapis, 2)
                .add(of(ItemTypes.IRON_INGOT), 2)
                .add(of(ItemTypes.DIAMOND), 1)
                .add(of(ItemTypes.OBSIDIAN), 1)
                .add(of(ItemTypes.CRAFTING_TABLE), 1)
                .cost(0)
                .result(FakeBlockItem(CustomBlocks.HT_CRAFTING_TABLE).createItemStack())
                .build()
        )

        private val creativeMachines = machines

        private val weapons: List<HiTechCraftingRecipe> = asList(
            // TODO, remove this after event
            HiTechCraftingRecipe.builder()
                .add(
                    Ingredient.builder().with { item: ItemStack? ->
                        item != null &&
                        item.type == ItemTypes.DIAMOND_SWORD &&
                        item.get(Keys.ITEM_DURABILITY).orElse(null) == 1561 &&
                        !item.get(Keys.DISPLAY_NAME).isPresent &&
                        !item.get(DataItemType.key).isPresent &&
                        (item.get(Keys.ITEM_ENCHANTMENTS).orElse(null)?.size ?: 0) == 0
                    }.withDisplay(ItemTypes.DIAMOND_SWORD).build(), 1
                )
                .add(of(ItemTypes.PUMPKIN), 6)
                .cost(666)
                .result(ItemStack.of(ItemTypes.DIAMOND_SWORD).apply {
                    offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(
                        TextStyles.BOLD,

                        TextColors.GOLD,
                        lang.of("item.event.halloween2018.undeadKiller"))
                    )

                    offer(Keys.ITEM_LORE, asList<Text>(
                        lang.ofPlaceHolder(TextColors.DARK_GRAY, lang.of("item.event.halloween2018.undeadKillerLore"))
                    ))

                    offer(Keys.UNBREAKABLE, true)
                    offer(Keys.HIDE_UNBREAKABLE, true)
                    offer(Keys.HIDE_MISCELLANEOUS, true)
                    offer(Keys.HIDE_ATTRIBUTES, true)
                    offer(Keys.HIDE_ENCHANTMENTS, true)

                    // id == 127
                    offer(Keys.ITEM_DURABILITY, 8)
                    offer(Keys.ITEM_ENCHANTMENTS, asList(
                        Enchantment.of(EnchantmentTypes.SMITE, 6)
                    ))

                    // TODO, vanilla, change to sponge api if possible
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val vanillaItem = this as net.minecraft.item.ItemStack

                    vanillaItem.repairCost = 40

                    vanillaItem.addAttributeModifier(
                        "generic.attackDamage",
                        AttributeModifier("generic.attackDamage", 4.0, 0),
                        null
                    )

                    vanillaItem.addAttributeModifier(
                        "generic.attackSpeed",
                        AttributeModifier("generic.attackSpeed", -2.2, 0),
                        null
                    )
                })
                .build()
        )

        private val creativeWeapons = weapons

        private val all: List<HiTechCraftingRecipe> = ArrayList<HiTechCraftingRecipe>().plus(tools).plus(materials).plus(machines).plus(weapons)

        private val creativeAll: List<HiTechCraftingRecipe> = ArrayList<HiTechCraftingRecipe>().plus(creativeTools).plus(creativeMaterials).plus(
            creativeMachines).plus(creativeWeapons)

        val catalog: Map<Type, List<HiTechCraftingRecipe>> = mapOf(
            Type.ALL to all,
            Type.TOOL to tools,
            Type.MATERIAL to materials,
            Type.MACHINE to machines,
            Type.WEAPON to weapons
        )

        val creativeCatalog: Map<Type, List<HiTechCraftingRecipe>> = mapOf(
            Type.ALL to creativeAll,
            Type.TOOL to creativeTools,
            Type.MATERIAL to creativeMaterials,
            Type.MACHINE to creativeMachines,
            Type.WEAPON to creativeWeapons
        )

        val icons: Map<Type, ItemStackSnapshot> = mapOf(
            Type.ALL to Button(ButtonType.ALL).createItemStack().createSnapshot(),
            Type.TOOL to Tool(ToolType.WRENCH).createItemStack().createSnapshot(),
            Type.MATERIAL to Material(MaterialType.PART_RAW_BASE).createItemStack().createSnapshot(),
            Type.MACHINE to FakeBlockItem(CustomBlocks.ELEVATOR).createItemStack().createSnapshot(),
            Type.WEAPON to Gun().createItemStack().createSnapshot()
        )

        val names: Map<Type, Translation> = mapOf(
            Type.ALL to lang.translation("recipe.catalog.ALL"),
            Type.TOOL to lang.translation("recipe.catalog.TOOL"),
            Type.MATERIAL to lang.translation("recipe.catalog.MATERIAL"),
            Type.MACHINE to lang.translation("recipe.catalog.MACHINE"),
            Type.WEAPON to lang.translation("recipe.catalog.WEAPON")
        )

        val types: List<Type> = asList(Type.ALL, Type.MACHINE, Type.TOOL, Type.WEAPON, Type.MATERIAL)
    }
}
