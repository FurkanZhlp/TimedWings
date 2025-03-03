package me.furkanzhlp.timedwings;

import me.furkanzhlp.timedwings.actionbar.ActionBarManager;
import me.furkanzhlp.timedwings.adapter.AdapterManager;
import me.furkanzhlp.timedwings.commands.TimedWingsCommand;
import me.furkanzhlp.timedwings.config.ConfigManager;
import me.furkanzhlp.timedwings.flight.FlightManager;
import me.furkanzhlp.timedwings.listeners.PlayerListeners;
import me.furkanzhlp.timedwings.logger.Logger;
import me.furkanzhlp.timedwings.message.MessageManager;
import me.furkanzhlp.timedwings.placeholder.PlaceholderManager;
import me.furkanzhlp.timedwings.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class TimedWings extends JavaPlugin {
    private static TimedWings instance;
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private FlightManager flightManager;
    private PlaceholderManager placeholderManager;
    private ActionBarManager actionBarManager;
    private AdapterManager adapterManager;
    private MessageManager messageManager;
    private Logger logger;
    private FileConfiguration config;
    private FileConfiguration language;


    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        if (!loadConfigs()) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        logger = new Logger("TimedWings",false);

        messageManager = new MessageManager(this);
        actionBarManager = new ActionBarManager(this);

        //
        playerDataManager = new PlayerDataManager(this);
        flightManager = new FlightManager(this);

        placeholderManager = new PlaceholderManager(this);
        placeholderManager.registerPlaceholders();

        adapterManager = new AdapterManager(this);


        Bukkit.getPluginManager().registerEvents(new PlayerListeners(this),this);

        getCommand("timedwings").setExecutor(new TimedWingsCommand(this));

        logger.log("TimedWings plugin has been successfully enabled on version "+getDescription().getVersion());

        logger.log("HATA", Logger.Level.ERROR);
    }


    @Override
    public void onDisable(){
        configManager.onDisable();
        playerDataManager.onDisable();
        flightManager.onDisable();
        adapterManager.onDisable();
        placeholderManager.unregisterPlaceholders();
    }

    public boolean loadConfigs(){
        try {
            this.config = this.getConfigManager().getConfig(new File(this.getDataFolder(), "config.yml")).getFileConfiguration();
            this.language = this.getConfigManager().getConfig(new File(this.getDataFolder(), "language.yml")).getFileConfiguration();
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }




    public FileConfiguration getConfiguration() {
        return config;
    }

    public FileConfiguration getLanguage() {
        return language;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public FlightManager getFlightManager() {
        return flightManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    public AdapterManager getAdapterManager() {
        return adapterManager;
    }

    public Logger logger() {
        return logger;
    }

    public static TimedWings getInstance() {
        return instance;
    }
}
