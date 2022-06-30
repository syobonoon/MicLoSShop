package net.syobonoon.plugin.miclosshop;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Config {
	private final Plugin plugin;
	private FileConfiguration config = null;
	public final static int MAX_SHOP_GUI_NUM = 53;
	public final static String SHOP_GUI_PRESERVEBLOCK_NAME = "土地保護ブロック購入所";
	public final static String SHOP_GUI_MONEY_NAME = "通貨交換所";
	private Map<ItemStack, Double> moneyshop_map = new LinkedHashMap<ItemStack, Double>();
	private List<ItemStack> preserveBlock = new ArrayList<ItemStack>();
	private final static List<String> command_dshop_list = new ArrayList<String>(){
	    {
	        add("preserve");
	        add("money");
	    }
	};

	Map<Material, Double> moneyshop_material_map = new LinkedHashMap<Material, Double>() {
		{
			put(Material.COBBLESTONE, 1D);
			put(Material.DIRT, 1D);
			put(Material.SAND, 1D);
			put(Material.SANDSTONE, 1D);
			put(Material.CUT_SANDSTONE, 1D);
			put(Material.CHISELED_SANDSTONE, 1D);
			put(Material.RED_SANDSTONE, 1D);
			put(Material.GRAVEL, 1D);
			put(Material.GRANITE, 1D);
			put(Material.DIORITE, 1D);
			put(Material.ANDESITE, 1D);
			put(Material.DARK_OAK_LOG, 1D);
			put(Material.JUNGLE_LOG, 1D);
			put(Material.ACACIA_LOG, 1D);
			put(Material.BIRCH_LOG, 1D);
			put(Material.SPRUCE_LOG, 1D);
			put(Material.OAK_LOG, 1D);
			put(Material.ROTTEN_FLESH, 1D);
			put(Material.WHEAT_SEEDS, 1D);
			put(Material.WHEAT, 1D);
			put(Material.CARROT, 1D);
			put(Material.POTATO, 1D);
			put(Material.COAL, 50D);
			put(Material.IRON_ORE, 100D);
			put(Material.REDSTONE_BLOCK, 500D);
			put(Material.LAPIS_BLOCK, 500D);
			put(Material.GOLD_ORE, 2000D);
			put(Material.EMERALD_ORE, 8000D);
			put(Material.DIAMOND, 10000D);
		}
	};

	public Config(Plugin plugin) {
		this.plugin = plugin;
		load_config();
	}

	public void load_config() {
		plugin.saveDefaultConfig();
		if (config != null) {
			plugin.reloadConfig();
			plugin.getServer().broadcastMessage(ChatColor.GREEN+"MicLoSShop reload completed");
		}
		config = plugin.getConfig();

		ItemStack item_stack = null;
		ItemMeta meta_item = null;

		//保護ブロック交換所
		preserveBlock.clear();

		item_stack = new ItemStack(Material.STONE, 1);
		meta_item = item_stack.getItemMeta();
		meta_item.setDisplayName("50");
		meta_item.setCustomModelData(1);
		item_stack.setItemMeta(meta_item);
		preserveBlock.add(item_stack);

		item_stack = new ItemStack(Material.IRON_BLOCK, 1);
		meta_item = item_stack.getItemMeta();
		meta_item.setDisplayName("100");
		meta_item.setCustomModelData(1);
		item_stack.setItemMeta(meta_item);
		preserveBlock.add(item_stack);

		item_stack = new ItemStack(Material.GOLD_BLOCK, 1);
		meta_item = item_stack.getItemMeta();
		meta_item.setDisplayName("250");
		meta_item.setCustomModelData(1);
		item_stack.setItemMeta(meta_item);
		preserveBlock.add(item_stack);

		item_stack = new ItemStack(Material.DIAMOND_BLOCK, 1);
		meta_item = item_stack.getItemMeta();
		meta_item.setDisplayName("500");
		meta_item.setCustomModelData(1);
		item_stack.setItemMeta(meta_item);
		preserveBlock.add(item_stack);


		load_moneyshop();
	}

	public void load_moneyshop() {
		moneyshop_map.clear();

		for (Material item_material : moneyshop_material_map.keySet()) {

			ItemStack item_stack = new ItemStack(item_material, 1);
			ItemMeta meta_item = item_stack.getItemMeta();
			meta_item.setDisplayName(String.valueOf(moneyshop_material_map.get(item_material)));
			meta_item.setCustomModelData(2);
			item_stack.setItemMeta(meta_item);
			moneyshop_map.put(item_stack, moneyshop_material_map.get(item_material));
		}
	}

	public List<ItemStack> getPreserveBlockItemStack() {
		return this.preserveBlock;
	}

	public Map<ItemStack, Double> getMoneyShopMap() {
		return this.moneyshop_map;
	}

	public List<String> getCommandDshopList(){
		return this.command_dshop_list;
	}

	public int getInt(String key) {
		return config.getInt(key);
	}

	public double getDouble(String key) {
		return config.getDouble(key);
	}

	public boolean getBoolean(String key) {
		return config.getBoolean(key);
	}

	public String getString(String key) {
		return config.getString(key);
	}

	//テストメッセージを送る関数
    public void sendTestMessage(String test_str) {
    	plugin.getServer().broadcastMessage(ChatColor.AQUA+test_str);
    }
}