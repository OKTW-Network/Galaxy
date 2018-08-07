package one.oktw.galaxy.recipe

import one.oktw.galaxy.Main
import one.oktw.galaxy.block.data.FakeBlockItem
import one.oktw.galaxy.block.enums.CustomBlocks
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.MaterialType
import one.oktw.galaxy.item.enums.ToolType
import one.oktw.galaxy.item.enums.UpgradeType
import one.oktw.galaxy.item.type.*
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.DyeColors
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.recipe.crafting.Ingredient
import org.spongepowered.api.item.recipe.crafting.Ingredient.of
import java.util.Arrays.asList

class Recipes {
    companion object {
        private val lang = Main.languageService.getDefaultLanguage()
        private val lapis = of(ItemStack.of(ItemTypes.DYE, 1).apply { offer(Keys.DYE_COLOR, DyeColors.BLUE) })

        enum class Type {
            ALL,
            TOOL,
            MATERIAL,
            MACHINE,
            WEAPON
        }

        private val materials: List<HiTechCraftingRecipe> = ArrayList<HiTechCraftingRecipe>().apply {
            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.CLAY), 8)
                    .add(of(ItemTypes.OBSIDIAN), 8)
                    .cost(100)
                    .result(Material(MaterialType.PART_RAW_BASE).createItemStack())
                    .build()
            )

            // Upgrade base
            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.GOLD_INGOT), 1)
                    .add(of(ItemTypes.REDSTONE), 2)
                    .add(of(ItemTypes.GLASS_PANE), 1)
                    .add(of(ItemTypes.QUARTZ), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.BASE).createItemStack())
                    .build()
            )

            // Range Upgrade
            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.IRON_INGOT), 4)
                    .add(of(ItemTypes.GOLD_INGOT), 2)
                    .add(of(ItemTypes.DIAMOND), 1)
                    .add(lapis, 1)
                    .add(Upgrade(UpgradeType.BASE), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.RANGE, 1).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.GOLD_INGOT), 4)
                    .add(of(ItemTypes.EMERALD), 2)
                    .add(of(ItemTypes.QUARTZ), 1)
                    .add(lapis, 1)
                    .add(Upgrade(UpgradeType.RANGE, 1), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.RANGE, 2).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.QUARTZ), 4)
                    .add(of(ItemTypes.BLAZE_ROD), 2)
                    .add(of(ItemTypes.ENDER_PEARL), 1)
                    .add(lapis, 1)
                    .add(Upgrade(UpgradeType.RANGE, 2), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.RANGE, 3).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.BLAZE_ROD), 4)
                    .add(of(ItemTypes.DRAGON_BREATH), 2)
                    .add(of(ItemTypes.ENDER_EYE), 1)
                    .add(lapis, 1)
                    .add(Upgrade(UpgradeType.RANGE, 3), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.RANGE, 4).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.DRAGON_BREATH), 4)
                    .add(of(ItemTypes.DRAGON_EGG), 2)
                    .add(of(ItemTypes.NETHER_STAR), 1)
                    .add(lapis, 1)
                    .add(Upgrade(UpgradeType.RANGE, 4), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.RANGE, 5).createItemStack())
                    .build()
            )

            // Flexible upgrade
            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.FEATHER), 4)
                    .add(of(ItemTypes.SUGAR), 4)
                    .add(Upgrade(UpgradeType.BASE), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.FLEXIBLE, 1).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.FIREWORKS), 4)
                    .add(of(ItemStack.of(ItemTypes.DYE, 1).apply { offer(Keys.DYE_COLOR, DyeColors.WHITE) }), 4)
                    .add(Upgrade(UpgradeType.FLEXIBLE, 1), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.FLEXIBLE, 2).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(
                        Ingredient.builder()
                            .with {
                                it.type == ItemTypes.POTION && it[Keys.POTION_EFFECTS].orElse(null)?.first()?.type == PotionEffectTypes.SPEED
                            }
                            .withDisplay(ItemStack.of(ItemTypes.POTION, 1).apply {
                                offer(
                                    Keys.POTION_EFFECTS,
                                    asList(PotionEffect.builder().potionType(PotionEffectTypes.SPEED).duration(10).build())
                                )
                            })
                            .build(),
                        4
                    )
                    .add(of(ItemTypes.GLOWSTONE_DUST), 4)
                    .add(Upgrade(UpgradeType.FLEXIBLE, 2), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.FLEXIBLE, 3).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(
                        Ingredient.builder()
                            .with {
                                it.type == ItemTypes.SPLASH_POTION && it[Keys.POTION_EFFECTS].orElse(null)?.first()?.type == PotionEffectTypes.SPEED
                            }
                            .withDisplay(ItemStack.of(ItemTypes.SPLASH_POTION, 1).apply {
                                offer(
                                    Keys.POTION_EFFECTS,
                                    asList(PotionEffect.builder().potionType(PotionEffectTypes.SPEED).duration(10).build())
                                )
                            })
                            .build(),
                        4
                    )
                    .add(of(ItemTypes.NETHER_WART), 4)
                    .add(Upgrade(UpgradeType.FLEXIBLE, 3), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.FLEXIBLE, 4).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(
                        Ingredient.builder()
                            .with {
                                it.type == ItemTypes.LINGERING_POTION && it[Keys.POTION_EFFECTS].orElse(null)?.first()?.type == PotionEffectTypes.SPEED
                            }
                            .withDisplay(ItemStack.of(ItemTypes.LINGERING_POTION, 1).apply {
                                offer(
                                    Keys.POTION_EFFECTS,
                                    asList(PotionEffect.builder().potionType(PotionEffectTypes.SPEED).duration(10).build())
                                )
                            })
                            .build(),
                        4
                    )
                    .add(of(ItemTypes.CHORUS_FRUIT_POPPED), 4)
                    .add(Upgrade(UpgradeType.FLEXIBLE, 4), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.FLEXIBLE, 5).createItemStack())
                    .build()
            )

            // Cooling upgrade
            add(
                HiTechCraftingRecipe.builder()
                    .add(Ingredient.builder().with { it.type == ItemTypes.POTION && !it[Keys.POTION_EFFECTS].isPresent }
                        .withDisplay(ItemStack.of(ItemTypes.POTION, 1)).build(), 4)
                    .add(Ingredient.builder().with { it.type in asList(ItemTypes.LEAVES, ItemTypes.LEAVES2) }
                        .withDisplay(ItemStack.of(ItemTypes.LEAVES, 1)).build(), 4)
                    .add(Upgrade(UpgradeType.BASE), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.COOLING, 1).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.WATER_BUCKET), 4)
                    .add(of(ItemTypes.REDSTONE_BLOCK), 4)
                    .add(Upgrade(UpgradeType.COOLING, 1), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.COOLING, 2).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.SNOW), 4)
                    .add(of(ItemTypes.GLOWSTONE), 4)
                    .add(Upgrade(UpgradeType.COOLING, 2), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.COOLING, 3).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.PACKED_ICE), 4)
                    .add(of(ItemTypes.DIAMOND_BLOCK), 4)
                    .add(Upgrade(UpgradeType.COOLING, 3), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.COOLING, 4).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.NETHER_STAR), 4)
                    .add(of(ItemTypes.EMERALD_BLOCK), 4)
                    .add(Upgrade(UpgradeType.COOLING, 4), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.COOLING, 5).createItemStack())
                    .build()
            )

            // Damage upgrade
            add(
                HiTechCraftingRecipe.builder()
                    .add(Ingredient.builder().with { it.type == ItemTypes.PLANKS }
                        .withDisplay(ItemStack.of(ItemTypes.PLANKS, 1)).build(), 4)
                    .add(of(ItemTypes.CLAY), 4)
                    .add(Upgrade(UpgradeType.BASE), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.DAMAGE, 1).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.OBSIDIAN), 4)
                    .add(Ingredient.builder().with { it.type == ItemTypes.CONCRETE }
                        .withDisplay(ItemStack.of(ItemTypes.CONCRETE, 1)).build(), 4)
                    .add(Upgrade(UpgradeType.DAMAGE, 1), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.DAMAGE, 2).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.PRISMARINE), 4)
                    .add(of(ItemTypes.SEA_LANTERN), 4)
                    .add(Upgrade(UpgradeType.DAMAGE, 2), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.DAMAGE, 3).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.RED_NETHER_BRICK), 4)
                    .add(of(ItemTypes.NETHER_WART_BLOCK), 4)
                    .add(Upgrade(UpgradeType.DAMAGE, 3), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.DAMAGE, 4).createItemStack())
                    .build()
            )

            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.PURPUR_BLOCK), 4)
                    .add(of(ItemTypes.END_BRICKS), 4)
                    .add(Upgrade(UpgradeType.DAMAGE, 4), 1)
                    .cost(0)
                    .result(Upgrade(UpgradeType.DAMAGE, 5).createItemStack())
                    .build()
            )

            // cpu
            add(
                HiTechCraftingRecipe.builder()
                    .add(of(ItemTypes.REDSTONE), 20)
                    .add(of(ItemTypes.GOLD_INGOT), 8)
                    .add(of(ItemTypes.QUARTZ), 4)
                    .add(Material(MaterialType.PART_BASE), 1)
                    .cost(100)
                    .result(Material(MaterialType.CPU).createItemStack())
                    .build()
            )
        }

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
