package net.syobonoon.plugin.miclosshop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import jp.jyn.jecon.Jecon;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

public class MicLoSShopEvent implements Listener{
	private Plugin plugin;
	private Jecon jecon;

	public MicLoSShopEvent(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		Plugin plugin_Jecon = Bukkit.getPluginManager().getPlugin("Jecon");
        if(plugin_Jecon == null || !plugin_Jecon.isEnabled()) {
            plugin.getLogger().warning("Jecon is not available.");
        }

        this.jecon = (Jecon) plugin_Jecon;
	}

	@EventHandler
    public void openshop(PlayerInteractAtEntityEvent e) {
    	if (!(e.getRightClicked() instanceof  ArmorStand)) return;
    	Player p = e.getPlayer();

    	ArmorStand t_shop = (ArmorStand) e.getRightClicked();

    	//保護ブロック購入所
    	if (t_shop.getName().equals(Config.SHOP_GUI_PRESERVEBLOCK_NAME)) {
    		Inventory inv = Bukkit.createInventory(null, Config.MAX_SHOP_GUI_NUM+1, Config.SHOP_GUI_PRESERVEBLOCK_NAME);

    		//インベントリにアイテムを埋めていく
    		int i = 0;
    		for (ItemStack itemstack : MicLoSShop.config.getPreserveBlockItemStack()) {

    			if (i > Config.MAX_SHOP_GUI_NUM) break;
    			inv.setItem(i, itemstack);
    			i++;
    		}

    		p.openInventory(inv);
    		return;
    	}

    	//通貨交換所
    	else if (t_shop.getName().equals(Config.SHOP_GUI_MONEY_NAME)) {
    		Inventory inv = Bukkit.createInventory(null, Config.MAX_SHOP_GUI_NUM+1, Config.SHOP_GUI_MONEY_NAME);

    		//インベントリにアイテムを埋めていく
    		int i = 0;
    		for (ItemStack itemstack : MicLoSShop.config.getMoneyShopMap().keySet()) {

    			if (i > Config.MAX_SHOP_GUI_NUM) break;
    			inv.setItem(i, itemstack);
    			i++;
    		}

    		p.openInventory(inv);
    		return;
    	} else {
    		return;
    	}
    }

	@EventHandler
	public void addProductInventory(InventoryClickEvent e) {
		Player p = (Player)e.getWhoClicked();

		if (e.getClick().isKeyboardClick()) e.setCancelled(true);

		if (!(e.isLeftClick() || e.isRightClick())) return;

		//保護ブロック交換所
		if (e.getView().getTitle().equals(Config.SHOP_GUI_PRESERVEBLOCK_NAME)) openPreserveBlockShop(e, p);
		//通貨交換所
		else if (e.getView().getTitle().equals(Config.SHOP_GUI_MONEY_NAME)) openMoneyShop(e, p);

		return;
	}

	//通貨交換所のショップを処理する関数
	private void openMoneyShop(InventoryClickEvent e, Player p) {
		e.setCancelled(true);//アイテムの移動を禁止する

		//GUIではないところをクリックした場合
		if (!(0 <= e.getRawSlot() && e.getRawSlot() <= Config.MAX_SHOP_GUI_NUM)) return;

		//クリックしたアイテム取得
		ItemStack clicked_item = e.getCurrentItem();
		if (clicked_item.getType().equals(Material.AIR)) return;

		//買取値を取得
		double selling_price = Double.parseDouble(clicked_item.getItemMeta().getDisplayName());

		Inventory inv_p = p.getInventory();


		int user_item_idx = 0;
		int cnt_item = 0;
		boolean shift_flag;
		if (e.isShiftClick()) {
			shift_flag = true;
		} else {
			shift_flag = false;
		}
		do {
			user_item_idx = inv_p.first(clicked_item.getType());
			if (user_item_idx == -1) break;
			ItemStack user_item = inv_p.getItem(user_item_idx); //そのインデックスのItemStackを取得
			cnt_item++;

			int user_item_amount = user_item.getAmount();
			if(user_item_amount > 1) user_item.setAmount(user_item_amount - 1);
			else user_item.setAmount(0);
		}while(shift_flag);

		if (cnt_item == 0) {
			p.sendMessage(ChatColor.RED+"アイテムを所持していないので売ることができません。");
			return;
		}

		//プレイヤーが入手したお金を含めた残高を取得
		double user_buy_balance = jecon.getRepository().getDouble(p.getUniqueId()).getAsDouble() + selling_price*cnt_item;

		//プレイヤーの残高を更新
		jecon.getRepository().set(p.getUniqueId(), user_buy_balance); //ユーザーの残高を更新
		p.sendMessage(ChatColor.AQUA+String.valueOf(selling_price*cnt_item) + "円入手しました");
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
		return;
	}


	//保護ブロック交換用のショップを処理する関数
	private void openPreserveBlockShop(InventoryClickEvent e, Player p) {
		e.setCancelled(true);//アイテムの移動を禁止する

		//GUIではないところをクリックした場合
		if (!(0 <= e.getRawSlot() && e.getRawSlot() <= Config.MAX_SHOP_GUI_NUM)) return;

		//クリックしたアイテム取得
		ItemStack clicked_item = e.getCurrentItem();
		if (clicked_item.getType().equals(Material.AIR)) return;
		int num_preserve_blocks = Integer.parseInt(clicked_item.getItemMeta().getDisplayName());
		double price_block = num_preserve_blocks*200;

		//クリックしたアイテムを購入する
		double user_balance = jecon.getRepository().getDouble(p.getUniqueId()).getAsDouble();
		double user_buy_balance = user_balance - price_block;

		if (user_buy_balance < 0) {
			p.sendMessage(ChatColor.RED+"所持金が足りません");
			return;
		}

		jecon.getRepository().set(p.getUniqueId(), user_buy_balance);
		PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(p.getUniqueId());
		playerData.setBonusClaimBlocks(playerData.getBonusClaimBlocks() + num_preserve_blocks);
		p.sendMessage(ChatColor.AQUA+String.valueOf(num_preserve_blocks) + "保護ブロックを入手しました");
		p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1F);
		return;
	}

	//ショップを破壊する
    @EventHandler
    public void removeShop(EntityDamageByEntityEvent e){
    	if(!(e.getDamager() instanceof Player)) return;
        if(!(e.getEntity() instanceof ArmorStand)) return;

        ArmorStand stand = (ArmorStand)e.getEntity();
        Player p = (Player)e.getDamager();

        //ショップじゃなかったら
        if (!stand.getName().equals(Config.SHOP_GUI_MONEY_NAME) && !stand.getName().equals(Config.SHOP_GUI_PRESERVEBLOCK_NAME)) return;

		if (!p.isOp()) return;

		//ショップを壊す処理
        stand.remove();
    }


	//防具立てに変更を加えようとしたらキャンセル
	@EventHandler
	public void detectArmorEvent(PlayerArmorStandManipulateEvent e) {
		ArmorStand stand = e.getRightClicked();
		if (stand.getCustomName() == null) return;

		if (!stand.getCustomName().equals(Config.SHOP_GUI_PRESERVEBLOCK_NAME) && !stand.getCustomName().equals(Config.SHOP_GUI_MONEY_NAME)) return;

		//防具立てに変更があった(右クリックした)場合キャンセルする
		if (!(e.getPlayerItem().getType().equals(Material.AIR)) || (!(e.getArmorStandItem().getType().equals(Material.AIR)))) {
			e.setCancelled(true);
		}
		return;
	}

}
