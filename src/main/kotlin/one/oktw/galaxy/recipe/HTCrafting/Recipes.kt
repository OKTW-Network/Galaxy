/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.recipe.HTCrafting

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import one.oktw.galaxy.item.*
import one.oktw.galaxy.recipe.utils.HTCraftingBuilder
import one.oktw.galaxy.recipe.utils.HTCraftingRecipe
import one.oktw.galaxy.recipe.utils.Ingredient

class Recipes {
    companion object {
        enum class Type {
            ALL,
            TOOL,
            MATERIAL,
            MACHINE,
            WEAPON
        }

        private val materials: List<HTCraftingRecipe> = ArrayList<HTCraftingRecipe>().apply {
            // part raw base
            add(
                HTCraftingBuilder()
                    .add(Ingredient(items = listOf(Items.CLAY)), 8)
                    .add(Ingredient(items = listOf(Items.OBSIDIAN)), 8)
                    .cost(20)
                    .result(Material.RAW_BASE_PLATE.createItemStack())
                    .build()
            )

            // cpu
            add(
                HTCraftingBuilder()
                    .add(Ingredient(items = listOf(Items.REDSTONE)), 20)
                    .add(Ingredient(items = listOf(Items.GOLD_INGOT)), 8)
                    .add(Ingredient(items = listOf(Items.QUARTZ)), 4)
                    .add(Ingredient(customItem = listOf(Material.BASE_PLATE)), 1)
                    .cost(45)
                    .result(Material.CPU.createItemStack())
                    .build()
            )

            // cooling
//            add(
//                HTCraftingBuilder()
//                    .add(of(Upgrade(UpgradeType.COOLING, 1).createItemStack()), 1)
//                    .add(Ingredient(items = listOf(Items.BLUE_DYE)), 27)
//                    .add(Ingredient(items = listOf(Items.IRON_INGOT)), 6)
//                    .add(Ingredient(customItem = listOf(Material.BASE_PLATE), 1)
//                    .cost(20)
//                    .result(Material.COOLANT.createItemStack())
//                    .build()
//            )

            // Upgrade base
            add(
                HTCraftingBuilder()
                    .add(Ingredient(items = listOf(Items.REDSTONE)), 4)
                    .add(Ingredient(items = listOf(Items.QUARTZ)), 2)
                    .add(Ingredient(items = listOf(Items.GOLD_INGOT)), 2)
                    .add(Ingredient(items = listOf(Items.GLASS_PANE)), 1)
                    .cost(5)
                    .result(Upgrade.BASE.createItemStack())
                    .build()
            )

            // Cooling upgrade
//            add(
//                HTCraftingBuilder()
//                    .add(Upgrade(UpgradeType.BASE), 1)
//                    .add(Ingredient.builder().with { it.type == ItemTypes.POTION && !it[Keys.POTION_EFFECTS].isPresent }
//                        .withDisplay(ItemStack.Ingredient(items = listOf(Items.POTION, 1).apply {
//                            @Suppress("CAST_NEVER_SUCCEEDS") val native = this as net.minecraft.item.ItemStack
//                            PotionUtils.addPotionToItemStack(native, PotionTypes.WATER)
//                            offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(TextColors.WHITE, lang.translationUnscoped("potion.effect.water")))
//                        }).build(), 1)
//                    .add(Ingredient.builder().with { it.type in asList(ItemTypes.LEAVES, ItemTypes.LEAVES2) }
//                        .withDisplay(ItemStack.Ingredient(items = listOf(Items.LEAVES, 1).apply {
//                            offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(TextColors.WHITE, lang.of("item.unspecified.anyLeaves")))
//                        }).build(), 8)
//                    .cost(10)
//                    .result(Upgrade(UpgradeType.COOLING, 1).createItemStack())
//                    .build()
//            )
        }

        private val creativeMaterials: List<HTCraftingRecipe> = ArrayList<HTCraftingRecipe>().apply {
            add(HTCraftingBuilder()
                .cost(0)
                .result(Material.BASE_PLATE.createItemStack())
                .build()
            )

            // laser
            add(
                HTCraftingBuilder()
                    .add(Ingredient(items = listOf(Items.REDSTONE_LAMP)), 1)
                    .add(Ingredient(items = listOf(Items.EMERALD)), 1)
                    .add(Ingredient(items = listOf(Items.GOLD_INGOT)), 1)
                    .add(Ingredient(items = listOf(Items.IRON_INGOT)), 1)
                    .add(Ingredient(items = listOf(Items.OBSIDIAN)), 1)
                    .cost(30)
                    .result(Material.LASER.createItemStack())
                    .build()
            )

            // Battery
            add(
                HTCraftingBuilder()
                    .add(Ingredient(items = listOf(Items.REDSTONE)), 36)
                    .add(Ingredient(items = listOf(Items.IRON_INGOT)), 4)
                    .add(Ingredient(items = listOf(Items.OBSIDIAN)), 8)
                    .cost(25)
                    .result(Material.BATTERY.createItemStack())
                    .build()
            )


            // Scope
//            add(
//                HTCraftingBuilder()
//                    .add(Ingredient(items = listOf(Items.IRON_INGOT)), 4)
//                    .add(Ingredient(items = listOf(Items.OBSIDIAN)), 1)
//                    .add(Ingredient(items = listOf(Items.GLASS_PANE)), 1)
//                    .add(Ingredient(items = listOf(Items.STRING)), 4)
//                    .add(of(ItemStack.Ingredient(items = listOf(Items.DYE, 1).apply { offer(Keys.DYE_COLOR, DyeColors.RED) }), 4)
//                    .cost(30)
//                    .result(Material.SCOPE.createItemStack())
//                    .build()
//            )

            // barrel
            add(
                HTCraftingBuilder()
                    .add(Ingredient(items = listOf(Items.IRON_INGOT)), 18)
                    .add(Ingredient(items = listOf(Items.GOLD_INGOT)), 4)
                    .add(Ingredient(items = listOf(Items.OBSIDIAN)), 1)
                    .cost(70)
                    .result(Material.BARREL.createItemStack())
                    .build()
            )

            // handle
//            add(
//                HTCraftingBuilder()
//                    .add(Ingredient(items = listOf(Items.IRON_INGOT)), 18)
//                    .add(Ingredient(items = listOf(Items.GOLD_INGOT)), 4)
//                    .add(Ingredient.builder().with { it.type == ItemTypes.WOOL }
//                        .withDisplay(ItemStack.Ingredient(items = listOf(Items.WOOL, 1)).build(), 1)
//                    .cost(60)
//                    .result(Ingredient(customItem = listOf(Material.HANDLE).createItemStack())
//                    .build()
//            )

            // trigger
            add(
                HTCraftingBuilder()
                    .add(Ingredient(items = listOf(Items.IRON_INGOT)), 4)
                    .add(Ingredient(items = listOf(Items.GOLD_INGOT)), 4)
                    .add(Ingredient(items = listOf(Items.REDSTONE)), 2)
                    .cost(75)
                    .result(Material.TRIGGER.createItemStack())
                    .build()
            )
        }.plus(materials)

        private val tools: List<HTCraftingRecipe> = listOf(
            HTCraftingBuilder()
                .add(Ingredient(items = listOf(Items.IRON_INGOT)), 3)
                .add(Ingredient(items = listOf(Items.STICK)), 1)
                .cost(0)
                .result(Tool.WRENCH.createItemStack())
                .build()
        )

        private val creativeTools = tools

        private val machines: List<HTCraftingRecipe> = listOf(
            HTCraftingBuilder()
                .add(Ingredient(items = listOf(Items.IRON_INGOT)), 9)
                .add(Ingredient(items = listOf(Items.ENDER_PEARL)), 1)
                .cost(0)
                .result(CustomBlockItem.ELEVATOR.createItemStack())
                .build(),

            HTCraftingBuilder()
                .add(Ingredient(items = listOf(Items.REDSTONE)), 2)
                .add(Ingredient(items = listOf(Items.BLUE_DYE)), 2)
                .add(Ingredient(items = listOf(Items.IRON_INGOT)), 2)
                .add(Ingredient(items = listOf(Items.DIAMOND)), 1)
                .add(Ingredient(items = listOf(Items.OBSIDIAN)), 1)
                .add(Ingredient(items = listOf(Items.CRAFTING_TABLE)), 1)
                .cost(0)
                .result(CustomBlockItem.HT_CRAFTING_TABLE.createItemStack())
                .build(),

            HTCraftingBuilder()
                .add(Ingredient(customItem = listOf(CustomBlockItem.ELEVATOR)), 1)
                .add(Ingredient(customItem = listOf(Material.CPU)), 1)
                .add(Ingredient(customItem = listOf(Material.COOLANT)), 1)
                .add(Ingredient(items = listOf(Items.GOLD_INGOT)), 8)
                .add(Ingredient(items = listOf(Items.IRON_INGOT)), 12)
                .add(Ingredient(items = listOf(Items.OBSIDIAN)), 6)
                .cost(15)
                .result(CustomBlockItem.TELEPORTER_CORE_BASIC.createItemStack())
                .build(),

            HTCraftingBuilder()
                .add(Ingredient(customItem = listOf(CustomBlockItem.TELEPORTER_CORE_BASIC)), 2)
                .add(Ingredient(customItem = listOf(Material.CPU)), 1)
                .add(Ingredient(customItem = listOf(Material.COOLANT)), 1)
                .add(Ingredient(items = listOf(Items.GOLD_INGOT)), 12)
                .add(Ingredient(items = listOf(Items.IRON_INGOT)), 16)
                .add(Ingredient(items = listOf(Items.OBSIDIAN)), 8)
                .add(Ingredient(items = listOf(Items.DIAMOND)), 2)
                .cost(25)
                .result(CustomBlockItem.TELEPORTER_CORE_ADVANCE.createItemStack())
                .build(),

            HTCraftingBuilder()
                .add(Ingredient(items = listOf(Items.IRON_INGOT)), 6)
                .add(Ingredient(items = listOf(Items.GOLD_INGOT)), 4)
                .add(Ingredient(items = listOf(Items.GLASS)), 1)
                .cost(5)
                .result(CustomBlockItem.TELEPORTER_FRAME.createItemStack())
                .build()
        )

        private val creativeMachines = ArrayList<HTCraftingRecipe>().apply {  }.plus(machines)

        private val weapons: List<HTCraftingRecipe> = ArrayList()

        private val creativeWeapons = ArrayList<HTCraftingRecipe>().apply {
            // gun
            add(
                HTCraftingBuilder()
                    .add(Ingredient(customItem = listOf(Material.BARREL)), 1)
                    .add(Ingredient(customItem = listOf(Material.HANDLE)), 1)
                    .add(Ingredient(customItem = listOf(Material.TRIGGER)), 1)
                    .add(Ingredient(customItem = listOf(Material.COOLANT)), 1)
                    .add(Ingredient(customItem = listOf(Material.LASER)), 1)
                    .add(Ingredient(customItem = listOf(Material.BATTERY)), 1)
                    .add(Ingredient(customItem = listOf(Material.CPU)), 1)
                    .cost(250)
                    .result(Weapon.PISTOL_LASOR.createItemStack())
                    .customItemResult(Weapon.PISTOL_LASOR)
                    .build()
            )
        }.plus(weapons)

        private val all: List<HTCraftingRecipe> = ArrayList<HTCraftingRecipe>().plus(machines).plus(tools).plus(weapons).plus(materials)

        private val creativeAll: List<HTCraftingRecipe> =
            ArrayList<HTCraftingRecipe>().plus(creativeMachines).plus(creativeTools).plus(creativeWeapons).plus(creativeMaterials)

        val catalog: Map<Type, List<HTCraftingRecipe>> = mapOf(
            Type.ALL to all,
            Type.TOOL to tools,
            Type.MATERIAL to materials,
            Type.MACHINE to machines,
            Type.WEAPON to weapons
        )

        val creativeCatalog: Map<Type, List<HTCraftingRecipe>> = mapOf(
            Type.ALL to creativeAll,
            Type.TOOL to creativeTools,
            Type.MATERIAL to creativeMaterials,
            Type.MACHINE to creativeMachines,
            Type.WEAPON to creativeWeapons
        )

        val icons: Map<Type, ItemStack> = mapOf(
            Type.ALL to Button.ALL.createItemStack(),
            Type.TOOL to Tool.WRENCH.createItemStack(),
            Type.MATERIAL to Material.RAW_BASE_PLATE.createItemStack(),
            Type.MACHINE to CustomBlockItem.ELEVATOR.createItemStack(),
            Type.WEAPON to Weapon.PISTOL_LASOR.createItemStack()
        )

        val names: Map<Type, Text> = mapOf(
            Type.ALL to Text.translatable("recipe.catalog.ALL"),
            Type.TOOL to Text.translatable("recipe.catalog.TOOL"),
            Type.MATERIAL to Text.translatable("recipe.catalog.MATERIAL"),
            Type.MACHINE to Text.translatable("recipe.catalog.MACHINE"),
            Type.WEAPON to Text.translatable("recipe.catalog.WEAPON")
        )

        val types: List<Type> = listOf(Type.ALL, Type.MACHINE, Type.TOOL, Type.WEAPON, Type.MATERIAL)
    }
}
