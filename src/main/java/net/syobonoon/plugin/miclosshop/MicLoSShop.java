package net.syobonoon.plugin.miclosshop;

import org.bukkit.plugin.java.JavaPlugin;

public class MicLoSShop extends JavaPlugin {
	public static Config config;

    @Override
    public void onEnable() {
    	config = new Config(this);
    	new MicLoSShopEvent(this);
    	getCommand("dshop").setExecutor(new ShopCommand());
    	getCommand("dshopreload").setExecutor(new ShopCommand());
    	getLogger().info("onEnable");

    }
}
