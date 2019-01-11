package one.oktw.galaxy.internal.register

import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.DyeColors
import org.spongepowered.api.data.type.SlabTypes
import org.spongepowered.api.data.type.TreeTypes
import org.spongepowered.api.item.ItemTypes.*
import org.spongepowered.api.item.recipe.crafting.Ingredient
import org.spongepowered.api.item.recipe.crafting.Ingredient.of
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe
import org.spongepowered.api.item.recipe.crafting.ShapelessCraftingRecipe
import java.util.Arrays.asList

class EasyRecipeRegister {
    init {
        Sponge.getRegistry().craftingRecipeRegistry.apply {

            val woodList = listOf(
                TreeTypes.OAK,
                TreeTypes.SPRUCE,
                TreeTypes.BIRCH,
                TreeTypes.JUNGLE,
                TreeTypes.ACACIA,
                TreeTypes.DARK_OAK
            )

            val dyeList = listOf(
                DyeColors.BLACK,
                DyeColors.BLUE,
                DyeColors.BROWN,
                DyeColors.CYAN,
                DyeColors.GRAY,
                DyeColors.GREEN,
                DyeColors.LIGHT_BLUE,
                DyeColors.LIME,
                DyeColors.MAGENTA,
                DyeColors.ORANGE,
                DyeColors.PINK,
                DyeColors.PURPLE,
                DyeColors.RED,
                DyeColors.SILVER,
                DyeColors.WHITE,
                DyeColors.YELLOW
            )

            val straitStoneSlabList = listOf(
                SlabTypes.COBBLESTONE,
                SlabTypes.STONE,
                SlabTypes.BRICK,
                SlabTypes.NETHERBRICK
            )

            val lanscapeStoneSlabList = listOf(
                SlabTypes.QUARTZ,
                SlabTypes.SAND,
                SlabTypes.RED_SAND,
                SlabTypes.SMOOTH_BRICK
            )

            // Chest (Log)
            register(
                ShapedCraftingRecipe.builder().aisle("lll", "l l", "lll")
                    .where('l', Ingredient.builder().with { it.type in asList(LOG, LOG2) }.build())
                    .result(CHEST.template.createStack().apply { this.quantity = 4 })
                    .build("log_chest", main)
            )

            // Hopper (Log)
            register(
                ShapedCraftingRecipe.builder().aisle("ili", "ili", " i ")
                    .where('l', Ingredient.builder().with { it.type in asList(LOG, LOG2) }.build())
                    .where('i', of(IRON_INGOT))
                    .result(HOPPER.template.createStack())
                    .build("log_hopper", main)
            )

            // Stick (Log)
            register(
                ShapedCraftingRecipe.builder().aisle("l", "l")
                    .where('l', Ingredient.builder().with { it.type in asList(LOG, LOG2) }.build())
                    .result(STICK.template.createStack().apply { this.quantity = 16 })
                    .build("log_stick", main)
            )

            // Ladder (Log)
            register(
                ShapedCraftingRecipe.builder().aisle("l l", "lll", "l l")
                    .where('l', Ingredient.builder().with { it.type in asList(LOG, LOG2) }.build())
                    .result(LADDER.template.createStack().apply { this.quantity = 24 })
                    .build("log_ladder", main)
            )

            // Color Glass to Glass
            register(
                ShapedCraftingRecipe.builder().aisle("sss", "sws", "sss")
                    .where('s', Ingredient.builder().with { it.type == STAINED_GLASS }.build())
                    .where('w', of(WATER_BUCKET))
                    .result(GLASS.template.createStack().apply { quantity = 8 })
                    .build("color_glass_to_glass", main)
            )

            // Color Glass Pane to Glass Pane
            register(
                ShapedCraftingRecipe.builder().aisle("sss", "sws", "sss")
                    .where('s', Ingredient.builder().with { it.type == STAINED_GLASS_PANE }.build())
                    .where('w', of(WATER_BUCKET))
                    .result(GLASS_PANE.template.createStack().apply { quantity = 8 })
                    .build("color_glass_pane_to_glass_pane", main)
            )

            // Trapped Chest
            register(
                ShapedCraftingRecipe.builder().aisle("ccc", "ctc", "ccc")
                    .where('c', Ingredient.builder().with { it.type == PLANKS }.build())
                    .where('t', of(TRIPWIRE_HOOK))
                    .result(TRAPPED_CHEST.template.createStack())
                    .build("trapped_chest_planks", main)
            )

            // TNT Minecart
            register(
                ShapedCraftingRecipe.builder().aisle("iti", "iii")
                    .where('i', of(IRON_INGOT))
                    .where('t', of(TNT))
                    .result(TNT_MINECART.template.createStack())
                    .build("tnt_minecart_ingot", main)
            )

            // Hopper Minecart
            register(
                ShapedCraftingRecipe.builder().aisle("ihi", "iii")
                    .where('i', of(IRON_INGOT))
                    .where('h', of(HOPPER))
                    .result(HOPPER_MINECART.template.createStack())
                    .build("hopper_minecart_ingot", main)
            )

            // Chest Minecart
            register(
                ShapedCraftingRecipe.builder().aisle("ici", "iii")
                    .where('i', of(IRON_INGOT))
                    .where('c', of(CHEST))
                    .result(CHEST_MINECART.template.createStack())
                    .build("chest_minecart_ingot", main)
            )

            // Furnace Minecart
            register(
                ShapedCraftingRecipe.builder().aisle("ifi", "iii")
                    .where('i', of(IRON_INGOT))
                    .where('f', of(FURNACE))
                    .result(FURNACE.template.createStack())
                    .build("furnace_minecart_ingot", main)
            )

            // Carrot On a Stick
            register(
                ShapedCraftingRecipe.builder().aisle("  s", " st", "sct")
                    .where('s', of(STICK))
                    .where('t', of(STRING))
                    .where('c', of(CARROT))
                    .result(CARROT_ON_A_STICK.template.createStack())
                    .build("carrot_rod", main)
            )

            // 同時用書的材料加羽毛墨囊做可以寫得書
            register(
                ShapelessCraftingRecipe.builder()
                    .addIngredient(of(LEATHER))
                    .addIngredient(of(PAPER))
                    .addIngredient(of(PAPER))
                    .addIngredient(of(PAPER))
                    .addIngredient(of(FEATHER))
                    .addIngredient(Ingredient.builder().with { it.type == DYE && it[Keys.DYE_COLOR] == DyeColors.BLACK }.build())
                    .result(WRITABLE_BOOK.template.createStack())
                    .build("writable_book_made_with_book_ingredient", main)
            )

            // Dispenser made from dropper
            register(
                ShapedCraftingRecipe.builder().aisle(" ts", "tds", " ts")
                    .where('t', of(STICK))
                    .where('s', of(STRING))
                    .where('D', of(DROPPER))
                    .result(DISPENSER.template.createStack())
                    .build("dispenser_made_from_dropper", main)
            )


            // Dispenser made from dropper
            register(
                ShapedCraftingRecipe.builder().aisle("rgr", "g g", " rgr")
                    .where('r', of(REDSTONE))
                    .where('g', of(GLOWSTONE_DUST))
                    .result(REDSTONE_LAMP.template.createStack())
                    .build("redstone_lamp_made_from_dusts", main)
            )

            // Redstone Repeater
            register(
                ShapedCraftingRecipe.builder().aisle("r r", "srs", "ttt")
                    .where('r', of(REDSTONE))
                    .where('s', of(STICK))
                    .where('t', of(STONE))
                    .result(REPEATER.template.createStack())
                    .build("redstone_repeater_stick", main)
            )

            // Slab (Log)
            woodList.forEach { tree_type ->
                register(
                    ShapedCraftingRecipe.builder().aisle("lll")
                        .where(
                            'l', Ingredient.builder().with { it.type in asList(LOG, LOG2) && it[Keys.TREE_TYPE].get() == tree_type }.build()
                        )
                        .result(WOODEN_SLAB.template.createStack().apply { quantity = 24; offer(Keys.TREE_TYPE, tree_type) })
                        .build("log_" + tree_type.name.toLowerCase() + "_slab", main)
                )
            }

            // Slab To Block
            //wood
            woodList.forEach { tree_type ->
                register(
                    ShapedCraftingRecipe.builder().aisle("s", "s")
                        .where(
                            's', Ingredient.builder().with { it.type == WOODEN_SLAB && it[Keys.TREE_TYPE].get() == tree_type }.build()
                        )
                        .result(PLANKS.template.createStack().apply { offer(Keys.TREE_TYPE, tree_type) })
                        .build(tree_type.name.toLowerCase() + "_slab_to_block", main)
                )
            }

            //擺直的
            straitStoneSlabList.forEach { slabType ->
                register(
                    ShapedCraftingRecipe.builder().aisle("s", "s")
                        .where('s', Ingredient.builder().with { it.type == STONE_SLAB && it[Keys.SLAB_TYPE].get() == slabType }.build())
                        .result(
                            when (slabType) {
                                SlabTypes.COBBLESTONE -> COBBLESTONE.template.createStack()
                                SlabTypes.STONE -> STONE.template.createStack()
                                SlabTypes.BRICK -> BRICK_BLOCK.template.createStack()
                                SlabTypes.NETHERBRICK -> NETHER_BRICK.template.createStack()
                                else -> AIR.template.createStack()
                            }
                        )
                        .build("${slabType.name.toLowerCase()}_slab_to_block", main)
                )
            }

            //擺橫的
            //Stones
            lanscapeStoneSlabList.forEach { slabType ->
                register(
                    ShapedCraftingRecipe.builder().aisle("ss")
                        .where('s', Ingredient.builder().with {
                            it.type == when (slabType) {
                                SlabTypes.RED_SAND -> STONE_SLAB2
                                else -> STONE_SLAB
                            } && it[Keys.SLAB_TYPE].get() == slabType
                        }.build())
                        .result(
                            when (slabType) {
                                SlabTypes.QUARTZ -> QUARTZ_BLOCK.template.createStack()
                                SlabTypes.SAND -> SANDSTONE.template.createStack()
                                SlabTypes.RED_SAND -> RED_SANDSTONE.template.createStack()
                                SlabTypes.SMOOTH_BRICK -> STONEBRICK.template.createStack()
                                else -> AIR.template.createStack()
                            }
                        )
                        .build("${slabType.name.toLowerCase()}_slab_to_block", main)
                )
            }

            //Purpur Slab
            register(
                ShapedCraftingRecipe.builder().aisle("ss")
                    .where('s', of(PURPUR_SLAB))
                    .result(PURPUR_BLOCK.template.createStack())
                    .build("purpur_slab_block", main)
            )

            // Glasspane -> Glass
            register(
                ShapelessCraftingRecipe.builder()
                    .addIngredient(of(GLASS_PANE))
                    .addIngredient(of(GLASS_PANE))
                    .addIngredient(of(GLASS_PANE))
                    .addIngredient(of(GLASS_PANE))
                    .addIngredient(of(GLASS_PANE))
                    .addIngredient(of(GLASS_PANE))
                    .addIngredient(of(GLASS_PANE))
                    .addIngredient(of(GLASS_PANE))
                    .result(GLASS.template.createStack().apply { quantity = 3 })
                    .build("glass_pane_to_glass", main)
            )

            //STAINED GLASS PANE -> StainedGlass
            dyeList.forEach { dye_color ->
                register(
                    ShapelessCraftingRecipe.builder()
                        .addIngredient(
                            Ingredient.builder().with {
                                it.type == STAINED_GLASS_PANE && it[Keys.DYE_COLOR] == dye_color && it[Keys.COLOR] == dye_color
                            }.build()
                        )
                        .addIngredient(
                            Ingredient.builder().with {
                                it.type == STAINED_GLASS_PANE && it[Keys.DYE_COLOR] == dye_color && it[Keys.COLOR] == dye_color
                            }.build()
                        )
                        .addIngredient(
                            Ingredient.builder().with {
                                it.type == STAINED_GLASS_PANE && it[Keys.DYE_COLOR] == dye_color && it[Keys.COLOR] == dye_color
                            }.build()
                        )
                        .addIngredient(
                            Ingredient.builder().with {
                                it.type == STAINED_GLASS_PANE && it[Keys.DYE_COLOR] == dye_color && it[Keys.COLOR] == dye_color
                            }.build()
                        )
                        .addIngredient(
                            Ingredient.builder().with {
                                it.type == STAINED_GLASS_PANE && it[Keys.DYE_COLOR] == dye_color && it[Keys.COLOR] == dye_color
                            }.build()
                        )
                        .addIngredient(
                            Ingredient.builder().with {
                                it.type == STAINED_GLASS_PANE && it[Keys.DYE_COLOR] == dye_color && it[Keys.COLOR] == dye_color
                            }.build()
                        )
                        .addIngredient(
                            Ingredient.builder().with {
                                it.type == STAINED_GLASS_PANE && it[Keys.DYE_COLOR] == dye_color && it[Keys.COLOR] == dye_color
                            }.build()
                        )
                        .addIngredient(
                            Ingredient.builder().with {
                                it.type == STAINED_GLASS_PANE && it[Keys.DYE_COLOR] == dye_color && it[Keys.COLOR] == dye_color
                            }.build()
                        )
                        .result(STAINED_GLASS.template.createStack().apply {
                            quantity = 3
                            offer(Keys.COLOR, dye_color.color)
                            offer(Keys.DYE_COLOR, dye_color)
                        })
                        .build("stained_glass_pane_to_stain_glass", main)
                )
            }

            // Color WOOL
            dyeList.forEach { dye_color ->
                register(
                    ShapelessCraftingRecipe.builder()
                        .addIngredient(Ingredient.builder().with { it.type == DYE && it[Keys.DYE_COLOR].get() == dye_color }.build())
                        .addIngredient(Ingredient.builder().with { it.type == WOOL }.build())
                        .result(WOOL.template.createStack().apply { offer(Keys.DYE_COLOR, dye_color) })
                        .build("dye_wool_" + dye_color.name, main)
                )
            }

            //STAINED GLASS
            dyeList.forEach { dye_color ->
                register(
                    ShapedCraftingRecipe.builder().aisle("sss", "sds", "sss")
                        .where('s', Ingredient.builder().with { it.type == STAINED_GLASS }.build())
                        .where('d', Ingredient.builder().with { it.type == DYE && it[Keys.DYE_COLOR].get() == dye_color }.build())
                        .result(STAINED_GLASS.template.createStack().apply {
                            quantity = 8; offer(Keys.COLOR, dye_color.color); offer(
                            Keys.DYE_COLOR,
                            dye_color
                        )
                        })
                        .build("stained_glass_" + dye_color.name, main)
                )
            }

            //STAINED GLASS PANE
            dyeList.forEach { dye_color ->
                register(
                    ShapedCraftingRecipe.builder().aisle("sss", "sds", "sss")
                        .where('s', Ingredient.builder().with { it.type == STAINED_GLASS_PANE }.build())
                        .where('d', Ingredient.builder().with { it.type == DYE && it[Keys.DYE_COLOR].get() == dye_color }.build())
                        .result(STAINED_GLASS_PANE.template.createStack().apply {
                            quantity = 8; offer(Keys.COLOR, dye_color.color); offer(
                            Keys.DYE_COLOR,
                            dye_color
                        )
                        })
                        .build("stained_glass_pane_" + dye_color.name, main)
                )
            }
        }
    }
}
