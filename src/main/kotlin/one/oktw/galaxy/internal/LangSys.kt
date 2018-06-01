package one.oktw.galaxy.internal

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.Main.Companion.main
import java.nio.file.Paths


class LangSys(lang: String = "zh_TW") {
    private val langBuild: ConfigurationLoader<CommentedConfigurationNode> = HoconConfigurationLoader.builder()
        .setPath(
            Paths.get(main.configDir.toString(), "$lang.cfg")
        ).build()
    private val rootNode: ConfigurationNode = langBuild.load()

    fun getLangString(node: Any): String? {
        return rootNode.getNode(node).string
    }

    init {
        val isChinese = (lang == "zh_TW")
        rootNode.getNode("armor.effect.night_vision").let {
            if (it.isVirtual) {it.value = if (isChinese) "夜視鏡" else "Night Vision Goggle"}
        }
        rootNode.getNode("armor.effect.jump_boost").let {
            if (it.isVirtual) {it.value= if (isChinese) "跳躍增強" else "Jump Boost"}
        }
        rootNode.getNode("armor.effect.speed_boost").let {
            if (it.isVirtual) {it.value= if (isChinese) "速度增強" else it.value="Speed Boost"}
        }
        rootNode.getNode("armor.item.name").let {
            if (it.isVirtual) {it.value= if (isChinese) "科技裝甲" else "Technology armor"}
        }
        rootNode.getNode("command.Sign.line_invalid").let {
            if (it.isVirtual) {it.value= if (isChinese) "請輸入行數 1-4" else "Line must be between 1 and 4"}
        }
        rootNode.getNode("command.Sign.too_many_words").let {
            if (it.isVirtual) {it.value= if (isChinese) "內容超過16字元" else "Content exceeds 16 characters"}
        }
        rootNode.getNode("command.Sign.success").let {
            if (it.isVirtual) {it.value= if (isChinese) "修改成功" else "The sign has been changed"}
        }
        rootNode.getNode("command.Sign.not_sign").let {
            if (it.isVirtual) {it.value= if (isChinese) "請把準心對準告示牌" else "Please focus on a sign"}
        }
        rootNode.getNode("command.Unstuck.does_not_unstuck").let {
            if (it.isVirtual) {it.value= if (isChinese) "覺得沒被救到嗎?" else "Doesn't seem to be saved?"}
        }
        rootNode.getNode("command.Unstuck.failed").let {
            if (it.isVirtual) {it.value= if (isChinese) "自救失敗，找不到安全位置" else "Failed to unstuck,there is no safe place nearby"}
        }
        rootNode.getNode("command.Unstuck.success").let {
            if (it.isVirtual) {it.value= if (isChinese) "已嘗試自救" else "You have tried to unstuck"}
        }
        rootNode.getNode("command.Unstuck.get_higher").let {
            if (it.isVirtual) {it.value= if (isChinese) "高度提高一點試試" else "Try again with higher place"}
        }
        rootNode.getNode("traveler.event.get_dust").let {
            if (it.isVirtual) {it.value= if (isChinese) "獲得 %d 個星塵" else "You got %d x star dust."}
        }
        rootNode.getNode("item.Gun.PISTOL").let {
            if (it.isVirtual) {it.value= if (isChinese) "雷射槍" else "Laser Gun"}
        }
        rootNode.getNode("item.Gun.SNIPER").let {
            if (it.isVirtual) {it.value= if (isChinese) "狙擊槍" else "Sniper"}
        }
        rootNode.getNode("item.Material.BARREL").let {
            if (it.isVirtual) {it.value= if (isChinese) "槍管" else "Barrel"}
        }
        rootNode.getNode("item.Material.HANDLE").let {
            if (it.isVirtual) {it.value= if (isChinese) "握把" else "Sniper"}
        }
        rootNode.getNode("item.Material.TRIGGER").let {
            if (it.isVirtual) {it.value= if (isChinese) "觸發" else "Trigger"}
        }
        rootNode.getNode("item.Material.BUTT").let {
            if (it.isVirtual) {it.value= if (isChinese) "槍托" else "Butt"}
        }
        rootNode.getNode("item.Material.CPU").let {
            if (it.isVirtual) {it.value= if (isChinese) "邏輯處理元件" else "Logical processing element"}
        }
        rootNode.getNode("item.Material.COOLANT").let {
            if (it.isVirtual) {it.value= if (isChinese) "冷卻元件" else "Cooling element"}
        }
        rootNode.getNode("item.Material.LASER").let {
            if (it.isVirtual) {it.value= if (isChinese) "雷射元件" else "Laser components"}
        }
        rootNode.getNode("item.Material.BATTERY").let {
            if (it.isVirtual) {it.value= if (isChinese) "蓄電元件" else "Power storage element"}
        }
        rootNode.getNode("item.Material.SCOPE").let {
            if (it.isVirtual) {it.value= if (isChinese) "瞄準輔助裝置" else "Scope"}
        }
        rootNode.getNode("item.Material.PART_RAW_BASE").let {
            if (it.isVirtual) {it.value= if (isChinese) "未成形的強化陶板" else "Unformed reinforced ceramic plate"}
        }
        rootNode.getNode("item.Material.PART_BASE").let {
            if (it.isVirtual) {it.value= if (isChinese) "強化陶版" else "Reinforced ceramic plate"}
        }
        rootNode.getNode("item.Tool.WRENCH").let {
            if (it.isVirtual) {it.value= if (isChinese) "扳手" else "Wrench"}
        }
        rootNode.getNode("item.Upgrade.RANGE").let {
            if (it.isVirtual) {it.value= if (isChinese) "距離" else "Range"}
        }
        rootNode.getNode("item.Upgrade.SPEED").let {
            if (it.isVirtual) {it.value= if (isChinese) "速度" else "Speed"}
        }
        rootNode.getNode("item.Upgrade.DAMAGE").let {
            if (it.isVirtual) {it.value= if (isChinese) "強化" else "Damage"}
        }
        rootNode.getNode("item.Upgrade.COOLING").let {
            if (it.isVirtual) {it.value= if (isChinese) "冷卻" else "Cooling"}
        }
        rootNode.getNode("item.Upgrade.HEAT").let {
            if (it.isVirtual) {it.value= if (isChinese) "火焰" else "Heat"}
        }
        rootNode.getNode("item.Upgrade.THROUGH").let {
            if (it.isVirtual) {it.value= if (isChinese) "穿透" else "Through"}
        }
        rootNode.getNode("item.Upgrade.SHIELD").let {
            if (it.isVirtual) {it.value= if (isChinese) "護盾" else "Shield"}
        }
        rootNode.getNode("item.Upgrade.FLEXIBLE").let {
            if (it.isVirtual) {it.value= if (isChinese) "靈活" else "Flexible"}
        }
        rootNode.getNode("item.Upgrade.ADAPT").let {
            if (it.isVirtual) {it.value= if (isChinese) "環境適應" else "Adapt"}
        }
        rootNode.getNode("item.Upgrade.FLY").let {
            if (it.isVirtual) {it.value= if (isChinese) "飛行" else "Flying"}
        }
        rootNode.getNode("item.Upgrade.NIGHT_VISION").let {
            if (it.isVirtual) {it.value= if (isChinese) "夜視" else "Night Vision"}
        }
        rootNode.getNode("item.Upgrade.GPS").let {
            if (it.isVirtual) {it.value= if (isChinese) "GPS" else "GPS"}
        }
        rootNode.getNode("item.Upgrade.DETECTOR").let {
            if (it.isVirtual) {it.value= if (isChinese) "玩家探測" else "Detector"}
        }
        rootNode.getNode("item.Upgrade.Item").let {
            if (it.isVirtual) {it.value= if (isChinese) "%s升級" else "%s Upgrade"}
        }
        rootNode.getNode("ui.ChunkLoader.Title").let{
            if (it.isVirtual) {it.value= if (isChinese) "ChunkLoader" else "ChunkLoader"}
        }
        rootNode.getNode("ui.ChunkLoader.Upgrade").let{
            if (it.isVirtual) {it.value= if (isChinese) "升級" else "Upgrade"}
        }
        rootNode.getNode("ui.ChunkLoader.Remove").let{
            if (it.isVirtual) {it.value= if (isChinese) "移除" else "Remove"}
        }
        rootNode.getNode("ui.BrowserGalaxy.Title").let{
            if (it.isVirtual) {it.value= if (isChinese) "瀏覽星系" else "Browse Galaxy"}
        }
        rootNode.getNode("ui.BrowserGalaxy.Details.Info").let{
            if (it.isVirtual) {it.value= if (isChinese) "資訊" else "Info"}
        }
        rootNode.getNode("ui.BrowserGalaxy.Details.Owner").let{
            if (it.isVirtual) {it.value= if (isChinese) "擁有者" else "Owner"}
        }
        rootNode.getNode("ui.BrowserGalaxy.Details.Members").let{
            if (it.isVirtual) {it.value= if (isChinese) "成員數量" else "Members"}
        }
        rootNode.getNode("ui.BrowserGalaxy.Details.Planets").let{
            if (it.isVirtual) {it.value= if (isChinese) "星球數量" else "Planets"}
        }
        rootNode.getNode("ui.BrowserMember.Title").let{
            if (it.isVirtual) {it.value= if (isChinese) "成員列表" else "Member List"}
        }
        rootNode.getNode("ui.BrowserMember.Details.Online").let{
            if (it.isVirtual) {it.value= if (isChinese) "上線中" else "ONLINE"}
        }
        rootNode.getNode("ui.BrowserMember.Details.Offline").let{
            if (it.isVirtual) {it.value= if (isChinese) "離線" else "OFFLINE"}
        }
        rootNode.getNode("ui.BrowserMember.Details.Status").let{
            if (it.isVirtual) {it.value= if (isChinese) "目前狀態" else "Status"}
        }
        rootNode.getNode("ui.BrowserMember.Details.Group").let{
            if (it.isVirtual) {it.value= if (isChinese) "身份組" else "Role"}
        }
        rootNode.getNode("ui.BrowserPlanet.Title").let{
            if (it.isVirtual) {it.value= if (isChinese) "星球列表" else "Planet List"}
        }
        rootNode.getNode("ui.BrowserPlanet.Details.Players").let{
            if (it.isVirtual) {it.value= if (isChinese) "玩家數" else "Players"}
        }
        rootNode.getNode("ui.BrowserPlanet.Details.Security").let{
            if (it.isVirtual) {it.value= if (isChinese) "安全等級" else "Security"}
        }
        rootNode.getNode("ui.Conform.Yes").let{
            if (it.isVirtual) {it.value= if (isChinese) "是" else "Yes"}
        }
        rootNode.getNode("ui.Conform.No").let{
            if (it.isVirtual) {it.value= if (isChinese) "否" else "No"}
        }
        rootNode.getNode("ui.GalaxyInfo.member_list").let{
            if (it.isVirtual) {it.value= if (isChinese) "成員列表" else "Member List"}
        }
        rootNode.getNode("ui.GalaxyInfo.planet_list").let{
            if (it.isVirtual) {it.value= if (isChinese) "星球列表" else "Planet List"}
        }
        rootNode.getNode("ui.GalaxyInfo.manage_galaxy").let{
            if (it.isVirtual) {it.value= if (isChinese) "管理星系" else "Manage Galaxy"}
        }
        rootNode.getNode("ui.GalaxyInfo.notice").let{
            if (it.isVirtual) {it.value= if (isChinese) "星系通知" else "Notifications"}
        }
        rootNode.getNode("ui.GalaxyInfo.join_req_sent").let{
            if (it.isVirtual) {it.value= if (isChinese) "已申請加入" else "Join request sent"}
        }
        rootNode.getNode("ui.GalaxyInfo.join_req").let{
            if (it.isVirtual) {it.value= if (isChinese) "申請加入" else "Join request"}
        }
        rootNode.getNode("ui.GalaxyJoinRequest.Title").let{
            if (it.isVirtual) {it.value= if (isChinese) "審核加入邀請" else "Review Join request"}
        }
        rootNode.getNode("ui.GalaxyJoinRequest.Conform_join").let{
            if (it.isVirtual) {it.value= if (isChinese) "是否要允許加入星系？" else "Do you allow him/her to join your galaxy?"}
        }
        rootNode.getNode("ui.GalaxyManagement.new_planet").let{
            if (it.isVirtual) {it.value= if (isChinese) "新增星球" else "Create planet"}
        }
        rootNode.getNode("ui.GalaxyManagement.manage_member").let{
            if (it.isVirtual) {it.value= if (isChinese) "管理成員" else "Manage member"}
        }
        rootNode.getNode("ui.GalaxyManagement.add_member").let{
            if (it.isVirtual) {it.value= if (isChinese) "添加成員" else "Add member"}
        }
        rootNode.getNode("ui.GalaxyManagement.join_application").let{
            if (it.isVirtual) {it.value= if (isChinese) "加入申請" else "Join application"}
        }
        rootNode.getNode("ui.GalaxyManagement.rename").let{
            if (it.isVirtual) {it.value= if (isChinese) "重新命名" else "Rename"}
        }
        rootNode.getNode("ui.GalaxyManagement.change_info").let{
            if (it.isVirtual) {it.value= if (isChinese) "更改簡介" else "Change info"}
        }
        rootNode.getNode("ui.GalaxyManagement.change_notification").let{
            if (it.isVirtual) {it.value= if (isChinese) "更改通知" else "Change Notification"}
        }
        rootNode.getNode("ui.GroupSelect.Title").let{
            if (it.isVirtual) {it.value= if (isChinese) "選擇一個身分組" else "Select a role"}
        }
        rootNode.getNode("ui.MainMenu.Title").let{
            if (it.isVirtual) {it.value= if (isChinese) "Main Menu" else "Main Menu"}
        }
        rootNode.getNode("ui.MainMenu.list_joined_galaxy").let{
            if (it.isVirtual) {it.value= if (isChinese) "列出已加入星系" else "List joined galaxy"}
        }
        rootNode.getNode("ui.MainMenu.create_galaxy").let{
            if (it.isVirtual) {it.value= if (isChinese) "創造星系" else "Create galaxy"}
        }
        rootNode.getNode("ui.MainMenu.list_all_galaxy").let{
            if (it.isVirtual) {it.value= if (isChinese) "列出所有星系" else "List all galaxy"}
        }
        rootNode.getNode("ui.ManageMember.remove_member").let{
            if (it.isVirtual) {it.value= if (isChinese) "移除成員" else "Remove member"}
        }
        rootNode.getNode("ui.ManageMember.change_group").let{
            if (it.isVirtual) {it.value= if (isChinese) "更改身份組" else "Change role"}
        }
        rootNode.getNode("ui.ManageMember.confirm_remove").let{
            if (it.isVirtual) {it.value= if (isChinese) "確定要移除成員？" else "Are you sure you want to remove this member?"}
        }
        rootNode.getNode("ui.Page.previous_page").let{
            if (it.isVirtual) {it.value= if (isChinese) "上一頁" else "Previous"}
        }
        rootNode.getNode("ui.Page.next_page").let{
            if (it.isVirtual) {it.value= if (isChinese) "下一頁" else "Next"}
        }

        saveLang()
    }
    private fun saveLang() {langBuild.save(rootNode)}

}