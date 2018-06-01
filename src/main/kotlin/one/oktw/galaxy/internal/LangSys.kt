package one.oktw.galaxy.internal

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.Main.Companion.main
import java.nio.file.Paths


class LangSys(lang: String = "zh_TW"){
    private val langBuild: ConfigurationLoader<CommentedConfigurationNode> = HoconConfigurationLoader.builder()
        .setPath(
            Paths.get(main.configDir.toString(), "$lang.cfg")
        ).build()
    val rootNode: ConfigurationNode = langBuild.load()
    init {
        val isChinese = (lang == "zh_TW")
        rootNode.getNode("armor").let{
            it.getNode("effect").let{
                it.getNode("night_vision").let {
                    if (it.isVirtual) {it.value= if (isChinese) "夜視鏡" else "Night Vision Goggle"}
                }
                it.getNode("jump_boost").let {
                    if (it.isVirtual) {it.value= if (isChinese) "跳躍增強" else "Jump Boost"}
                }
                it.getNode("speed_boost").let {
                    if (it.isVirtual) {it.value= if (isChinese) {it.value="速度增強"}else{it.value="Speed Boost"}}
                }
            }
            it.getNode("item","name").let {
                if (it.isVirtual) {it.value= if (isChinese) "科技裝甲" else "Technology armor"}
            }
        }
        rootNode.getNode("command").let {
            it.getNode("Sign").let {
                it.getNode("line_invalid").let {
                    if (it.isVirtual) {it.value= if (isChinese) "請輸入行數 1-4" else "Line must be between 1 and 4"}
                }
                it.getNode("too_many_words").let {
                    if (it.isVirtual) {it.value= if (isChinese) "內容超過16字元" else "Content exceeds 16 characters"}
                }
                it.getNode("success").let {
                    if (it.isVirtual) {it.value= if (isChinese) "修改成功" else "The sign has been changed"}
                }
                it.getNode("not_sign").let {
                    if (it.isVirtual) {it.value= if (isChinese) "請把準心對準告示牌" else "Please focus on a sign"}
                }
            }
            it.getNode("Unstuck").let {
                it.getNode("does_not_unstuck").let {
                    if (it.isVirtual) {it.value= if (isChinese) "覺得沒被救到嗎?" else "Doesn't seem to be saved?"}
        }
                it.getNode("failed").let {
                    if (it.isVirtual) {it.value= if (isChinese) "自救失敗，找不到安全位置" else "Failed to unstuck,there is no safe place nearby"}
                }
                it.getNode("success").let {
                    if (it.isVirtual) {it.value= if (isChinese) "已嘗試自救" else "You have tried to unstuck"}
                }
                it.getNode("get_higher").let {
                    if (it.isVirtual) {it.value= if (isChinese) "高度提高一點試試" else "Try again with higher place"}
                }
            }
        }
        rootNode.getNode("traveler").let {
            it.getNode("event","get_dust").let {
                if (it.isVirtual) {it.value= if (isChinese) "獲得 %d 個星塵" else "You got %d x star dust."}
            }
        }
        rootNode.getNode("item").let {
            it.getNode("Gun").let {
                it.getNode("PISTOL").let {
                    if (it.isVirtual) {it.value= if (isChinese) "雷射槍" else "Laser Gun"}
                }
                it.getNode("SNIPER").let {
                    if (it.isVirtual) {it.value= if (isChinese) "狙擊槍" else "Sniper"}
                }
            }
            it.getNode("Material").let {
                it.getNode("BARREL").let {
                    if (it.isVirtual) {it.value= if (isChinese) "槍管" else "Barrel"}
                }
                it.getNode("HANDLE").let {
                    if (it.isVirtual) {it.value= if (isChinese) "握把" else "Sniper"}
                }
                it.getNode("TRIGGER").let {
                    if (it.isVirtual) {it.value= if (isChinese) "觸發" else "Trigger"}
                }
                it.getNode("BUTT").let {
                    if (it.isVirtual) {it.value= if (isChinese) "槍托" else "Butt"}
                }
                it.getNode("CPU").let {
                    if (it.isVirtual) {it.value= if (isChinese) "邏輯處理元件" else "Logical processing element"}
                }
                it.getNode("COOLANT").let {
                    if (it.isVirtual) {it.value= if (isChinese) "冷卻元件" else "Cooling element"}
                }
                it.getNode("LASER").let {
                    if (it.isVirtual) {it.value= if (isChinese) "雷射元件" else "Laser components"}
                }
                it.getNode("BATTERY").let {
                    if (it.isVirtual) {it.value= if (isChinese) "蓄電元件" else "Power storage element"}
                }
                it.getNode("SCOPE").let {
                    if (it.isVirtual) {it.value= if (isChinese) "瞄準輔助裝置" else "Scope"}
                }
                it.getNode("PART_RAW_BASE").let {
                    if (it.isVirtual) {it.value= if (isChinese) "強化陶板" else "Unformed reinforced ceramic plate"}
                }
                it.getNode("PART_BASE").let {
                    if (it.isVirtual) {it.value= if (isChinese) "強化陶版" else "Reinforced ceramic plate"}
                }
            }
            it.getNode("Tool").let {
                it.getNode("WRENCH").let {
                    if (it.isVirtual) {it.value= if (isChinese) "扳手" else "Wrench"}
                }
            }
            it.getNode("Upgrade").let{
                it.getNode("RANGE").let {
                    if (it.isVirtual) {it.value= if (isChinese) "距離" else "Range"}
                }
                it.getNode("SPEED").let {
                    if (it.isVirtual) {it.value= if (isChinese) "速度" else "Speed"}
                }
                it.getNode("DAMAGE").let {
                    if (it.isVirtual) {it.value= if (isChinese) "強化" else "Damage"}
                }
                it.getNode("COOLING").let {
                    if (it.isVirtual) {it.value= if (isChinese) "冷卻" else "Cooling"}
                }
                it.getNode("HEAT").let {
                    if (it.isVirtual) {it.value= if (isChinese) "火焰" else "Heat"}
                }
                it.getNode("THROUGH").let {
                    if (it.isVirtual) {it.value= if (isChinese) "範圍" else "Through"}
                }
                it.getNode("SHIELD").let {
                    if (it.isVirtual) {it.value= if (isChinese) "護盾" else "Shield"}
                }
                it.getNode("FLEXIBLE").let {
                    if (it.isVirtual) {it.value= if (isChinese) "靈活" else "Flexible"}
                }
                it.getNode("ADAPT").let {
                    if (it.isVirtual) {it.value= if (isChinese) "環境適應" else "Adapt"}
                }
                it.getNode("FLY").let {
                    if (it.isVirtual) {it.value= if (isChinese) "飛行" else "Flying"}
                }
                it.getNode("NIGHT_VISION").let {
                    if (it.isVirtual) {it.value= if (isChinese) "夜視" else "Night Vision"}
                }
                it.getNode("GPS").let {
                    if (it.isVirtual) {it.value= if (isChinese) "GPS" else "GPS"}
                }
                it.getNode("DETECTOR").let {
                    if (it.isVirtual) {it.value= if (isChinese) "玩家探測" else "Detector"}
                }
                it.getNode("Item").let {
                    if (it.isVirtual) {it.value= if (isChinese) "%s升級" else "%s Upgrade"}
                }
            }

        }
        rootNode.getNode("ui").let{
            it.getNode("ChunkLoader").let{
                it.getNode("Title").let {
                    if (it.isVirtual) {it.value= if (isChinese) "ChunkLoader" else "ChunkLoader"}
                }
                it.getNode("Upgrade").let {
                    if (it.isVirtual) {it.value= if (isChinese) "升級" else "Upgrade"}
                }
                it.getNode("Remove").let {
                    if (it.isVirtual) {it.value= if (isChinese) "移除" else "Remove"}
                }
            }
            it.getNode("BrowserGalaxy").let{
                it.getNode("Title").let {
                    if (it.isVirtual) {it.value= if (isChinese) "瀏覽星系" else "Browse Galaxy"}
                }
                it.getNode("Details").let {
                    it.getNode("Info").let {
                        if (it.isVirtual) {it.value= if (isChinese) "資訊" else "Info"}
                    }
                    it.getNode("Owner").let {
                        if (it.isVirtual) {it.value= if (isChinese) "擁有者" else "Owner"}
                    }
                    it.getNode("Members").let {
                        if (it.isVirtual) {it.value= if (isChinese) "成員數量" else "Members"}
                    }
                    it.getNode("Planets").let {
                        if (it.isVirtual) {it.value= if (isChinese) "星球數量" else "Planets"}
                    }
                }
            }
            it.getNode("BrowserMember").let{
                it.getNode("Title").let {
                    if (it.isVirtual) {it.value= if (isChinese) "成員列表" else "Member List"}
                }
                it.getNode("Details").let {
                    it.getNode("Online").let {
                        if (it.isVirtual) {it.value= if (isChinese) "上線中" else "ONLINE"}
                    }
                    it.getNode("Offline").let {
                        if (it.isVirtual) {it.value= if (isChinese) "離線" else "OFFLINE"}
                    }
                    it.getNode("Status").let {
                        if (it.isVirtual) {it.value= if (isChinese) "目前狀態" else "Status"}
                    }
                    it.getNode("Group").let {
                        if (it.isVirtual) {it.value= if (isChinese) "身份組" else "Role"}
                    }
                }
            }
            it.getNode("BrowserPlanet").let{
                it.getNode("Title").let {
                    if (it.isVirtual) {it.value= if (isChinese) "星球列表" else "Planet List"}
                }
                it.getNode("Details").let {
                    it.getNode("Players").let {
                        if (it.isVirtual) {it.value= if (isChinese) "玩家數" else "Players"}
                    }
                    it.getNode("Security").let {
                        if (it.isVirtual) {it.value= if (isChinese) "安全等級" else "Security"}
                    }
                }
            }
            it.getNode("Conform").let {
                it.getNode("Yes").let {
                    if (it.isVirtual) {it.value= if (isChinese) "是" else "Yes"}
                }
                it.getNode("No").let {
                    if (it.isVirtual) {it.value= if (isChinese) "否" else "No"}
                }
            }
            it.getNode("GalaxyInfo").let {
                it.getNode("member_list").let {
                    if (it.isVirtual) {it.value= if (isChinese) "成員列表" else "Member List"}
                }
                it.getNode("planet_list").let {
                    if (it.isVirtual) {it.value= if (isChinese) "星球列表" else "Planet List"}
                }
                it.getNode("manage_galaxy").let {
                    if (it.isVirtual) {it.value= if (isChinese) "管理星系" else "Manage Galaxy"}
                }
                it.getNode("notice").let {
                    if (it.isVirtual) {it.value= if (isChinese) "星系通知" else "Notifications"}
                }
                it.getNode("join_req_sent").let {
                    if (it.isVirtual) {it.value= if (isChinese) "已申請加入" else "Join request sent"}
                }
                it.getNode("join_req").let {
                    if (it.isVirtual) {it.value= if (isChinese) "申請加入" else "Join request"}
                }
            }
            it.getNode("GalaxyJoinRequest").let {
                it.getNode("Title").let {
                    if (it.isVirtual) {it.value= if (isChinese) "審核加入邀請" else "Review Join request"}
                }
                it.getNode("Conform_join").let {
                    if (it.isVirtual) {it.value= if (isChinese) "是否要允許加入星系？" else "Do you allow him/her to join your galaxy?"}
                }
            }
            it.getNode("GalaxyManagement").let {
                it.getNode("new_planet").let {
                    if (it.isVirtual) {it.value= if (isChinese) "新增星球" else "Create planet"}
                }
                it.getNode("manage_member").let {
                    if (it.isVirtual) {it.value= if (isChinese) "管理成員" else "Manage member"}
                }
                it.getNode("add_member").let {
                    if (it.isVirtual) {it.value= if (isChinese) "添加成員" else "Add member"}
                }
                it.getNode("join_application").let {
                    if (it.isVirtual) {it.value= if (isChinese) "加入申請" else "Join application"}
                }
                it.getNode("rename").let {
                    if (it.isVirtual) {it.value= if (isChinese) "重新命名" else "Rename"}
                }
                it.getNode("change_info").let {
                    if (it.isVirtual) {it.value= if (isChinese) "更改簡介" else "Change info"}
                }
                it.getNode("change_notification").let {
                    if (it.isVirtual) {it.value= if (isChinese) "更改通知" else "Change Notification"}
                }
            }
            it.getNode("GroupSelect").let {
                it.getNode("Title").let {
                    if (it.isVirtual) {it.value= if (isChinese) "選擇一個身分組" else "Select a role"}
                }
            }
            it.getNode("MainMenu").let {
                it.getNode("Title").let {
                    if (it.isVirtual) {it.value= if (isChinese) "Main Menu" else "Main Menu"}
                }
                it.getNode("list_joined_galaxy").let {
                    if (it.isVirtual) {it.value= if (isChinese) "列出已加入星系" else "List joined galaxy"}
                }
                it.getNode("create_galaxy").let {
                    if (it.isVirtual) {it.value= if (isChinese) "創造星系" else "Create galaxy"}
                }
                it.getNode("list_all_galaxy").let {
                    if (it.isVirtual) {it.value= if (isChinese) "列出所有星系" else "List all galaxy"}
                }
            }
            it.getNode("ManageMember").let {
                it.getNode("remove_member").let {
                    if (it.isVirtual) {it.value= if (isChinese) "移除成員" else "Remove member"}
                }
                it.getNode("change_group").let {
                    if (it.isVirtual) {it.value= if (isChinese) "更改身份組" else "Change role"}
                }
                it.getNode("confirm_remove").let {
                    if (it.isVirtual) {it.value= if (isChinese) "確定要移除成員？" else "Are you sure you want to remove this member?"}
                }
            }
            it.getNode("Page").let {
                it.getNode("previous_page").let {
                    if (it.isVirtual) {it.value= if (isChinese) "上一頁" else "Previous"}
                }
                it.getNode("next_page").let {
                    if (it.isVirtual) {it.value= if (isChinese) "下一頁" else "Next"}
                }
            }
        }
        saveLang()
    }
    private fun saveLang() {langBuild.save(rootNode)}

}