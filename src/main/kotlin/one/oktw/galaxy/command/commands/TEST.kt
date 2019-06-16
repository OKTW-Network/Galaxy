/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.arguments.EntityArgumentType
import net.minecraft.entity.Entity
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import one.oktw.galaxy.command.Command

class TEST : Command {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            // string_1:指令名稱
            CommandManager.literal("test")
                /* 權限 (先猜是OP Permission Level）
                 * 1: 允許變動出生點方塊
                 * 2: 允許變動出生點方塊、使用作弊類指令（/gamemode、/give之類的）
                 * 3: 允許變動出生點方塊、使用作弊＋管理類指令（/ban、/op之類的）
                 * 4: 允許變動出生點方塊、使用所有指令（/stop、/save-all之類的）
                 */
                .requires { commandSource -> commandSource.hasPermissionLevel(2) }
                .then(
                    // 參數 string_1:參數顯示名稱 後面那個則是類別（EntityArgumentType.entities(): Selector)
                    CommandManager.argument("targets", EntityArgumentType.entities())
                        // 指令執行動作在這
                        .executes { context ->
                            // 使用另一個 fun 完持
                            execute(
                                // 指令執行者
                                context.source,
                                // 傳入參數
                                EntityArgumentType.getEntities(context, "targets")
                            )
                        }
                )
        )
    }

    private fun execute(source: ServerCommandSource, collections: Collection<Entity>): Int {
        val entities = collections.iterator()

        while (entities.hasNext()) {
            val entity = entities.next()
            entity.kill()
        }

        if (collections.size == 1) {
            // boolean_1: 是否進行OP全域廣播 ex: 「[Server: 殺死了 Steve]」
            source.sendFeedback(TextComponent("殺死了 ${collections.iterator().next().displayName.formattedText}"), true)
        } else {
            source.sendFeedback(TextComponent("殺死了 ${collections.size} 個實體"), true)
        }

        return collections.size
    }
}
