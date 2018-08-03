package one.oktw.galaxy.internal.register

import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.DyeColors
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

            val WoodList = listOf(TreeTypes.OAK, TreeTypes.SPRUCE, TreeTypes.BIRCH, TreeTypes.JUNGLE, TreeTypes.ACACIA, TreeTypes.DARK_OAK)
            val DyeList = listOf(
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

            // Carrot On a Stick
            register(
                ShapedCraftingRecipe.builder().aisle("  s", " st","sct")
                    .where('s', of(STICK))
                    .where('t', of(STRING))
                    .where('c', of(CARROT))
                    .result(CARROT_ON_A_STICK.template.createStack())
                    .build("carrot_rod", main)
            )

            // Redstone Repeater
            register(
                ShapedCraftingRecipe.builder().aisle("r r", "srs","ttt")
                    .where('r', of(REDSTONE))
                    .where('s', of(STICK))
                    .where('t', of(STONE))
                    .result(REPEATER.template.createStack())
                    .build("redstone_repeater_stick", main)
            )

            // Slab (Log)
            WoodList.forEach { type ->
                register(
                    ShapedCraftingRecipe.builder().aisle("lll")
                        .where(
                            'l',
                            Ingredient.builder().with { it.type in asList(LOG, LOG2) && it[Keys.TREE_TYPE].get() == type }.build()
                        )
                        .result(WOODEN_SLAB.template.createStack().apply { quantity = 24; offer(Keys.TREE_TYPE, type) })
                        .build("log_" + type.name.toLowerCase() + "_slab", main)
                )
            }

            // Slab To Block
            WoodList.forEach { type ->
                register(
                    ShapedCraftingRecipe.builder().aisle("s", "s")
                        .where(
                            's',
                            Ingredient.builder().with { it.type == WOODEN_SLAB }.build()
                        )
                        .result(PLANKS.template.createStack().apply { offer(Keys.TREE_TYPE, type) })
                        .build(type.name.toLowerCase() + "slab_to_block", main)
                )
            }

            // Color WOOL
            DyeList.forEach { color ->
                register(
                    ShapelessCraftingRecipe.builder()
                        .addIngredient(Ingredient.builder().with { it.type == DYE && it[Keys.DYE_COLOR].get() == color }.build())
                        .addIngredient(of(WOOL))
                        .result(WOOL.template.createStack().apply { offer(Keys.COLOR, color.getColor()) })
                        .build("dye_wool_" + color.name, main)
                )
            }

            //STAINED GLASS
            DyeList.forEach { color ->
                register(
                    ShapedCraftingRecipe.builder().aisle("sss", "sds","sss")
                        .where('s',Ingredient.builder().with { it.type in asList(STAINED_GLASS,GLASS) }.build())
                        .where('d',Ingredient.builder().with { it.type == DYE && it[Keys.DYE_COLOR].get() == color }.build())
                        .result(STAINED_GLASS.template.createStack().apply { quantity = 8 ; offer(Keys.COLOR,color.getColor()) })
                        .build("stained_glass_" + color.name, main)
                )
            }
        }
    }
}
