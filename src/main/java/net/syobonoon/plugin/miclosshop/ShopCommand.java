package net.syobonoon.plugin.miclosshop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopCommand implements TabExecutor {
	private List<String> command_dshop_list = new ArrayList<String>(MicLoSShop.config.getCommandDshopList());

	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean isSuccess = false;
		if (command.getName().equalsIgnoreCase("dshop")) {
			isSuccess = dshop(sender, args);
		}
		else if (command.getName().equalsIgnoreCase("dshopreload")) {
			isSuccess = dshopreload(sender, args);
		}
		return isSuccess;
	}

	@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (command.getName().equalsIgnoreCase("dshop") && args.length == 1) return command_dshop_list;
		return null;
    }

	private boolean dshop(CommandSender sender, String[] args) {
		if (!sender.hasPermission("miclosshop.dshop")) return false;
		if (!(sender instanceof Player)) return false;
		if (args.length != 2) {
	        sender.sendMessage(ChatColor.RED + "parameter error");
	        return false;
	    }

		if (args[0].equals("preserve")) {
			Player p = (Player) sender;
			Location loc = p.getLocation();

			int custom_num = Integer.parseInt(args[1]);

			ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class);
			stand.setVisible(false);
			stand.setGravity(false);
			stand.setCustomNameVisible(true);
			stand.setCustomName(Config.SHOP_GUI_PRESERVEBLOCK_NAME);

			EntityEquipment stand_equipment = stand.getEquipment();
			ItemStack item = new ItemStack(Material.SLIME_BALL, 1);
			ItemMeta item_meta = item.getItemMeta();
			item_meta.setCustomModelData(custom_num);
			item.setItemMeta(item_meta);
			stand_equipment.setHelmet(item);

			return true;
		}

		else if(args[0].equals("money")) {
			Player p = (Player) sender;
			Location loc = p.getLocation();

			int custom_num = Integer.parseInt(args[1]);

			ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class);
			stand.setVisible(false);
			stand.setGravity(false);
			stand.setCustomNameVisible(true);
			stand.setCustomName(Config.SHOP_GUI_MONEY_NAME);

			EntityEquipment stand_equipment = stand.getEquipment();
			ItemStack item = new ItemStack(Material.SLIME_BALL, 1);
			ItemMeta item_meta = item.getItemMeta();
			item_meta.setCustomModelData(custom_num);
			item.setItemMeta(item_meta);
			stand_equipment.setHelmet(item);

			return true;
		} else {
			sender.sendMessage(ChatColor.RED + args[0] + " is not exist.");
	        return false;
		}
	}

	private boolean dshopreload(CommandSender sender, String[] args) {
		if (!sender.hasPermission("miclosshop.reload")) return false;
		if (args.length != 0) return false;
		MicLoSShop.config.load_config();
		return true;
	}

}
