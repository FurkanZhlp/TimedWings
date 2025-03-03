package me.furkanzhlp.timedwings.player;

import me.furkanzhlp.timedwings.TimedWings;
import me.furkanzhlp.timedwings.config.ConfigFile;
import me.furkanzhlp.timedwings.config.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerData {
    private final TimedWings plugin;
    private final UUID playerUUID;
    private int usedFlightTime;
    private int remainingFlightTime;
    private boolean isFlying;
    public PlayerData(UUID playerUUID) {
        this.plugin = TimedWings.getInstance();
        this.playerUUID = playerUUID;


        File playerDataFolder = new File(this.plugin.getDataFolder()+"/player-data");
        if(!playerDataFolder.exists()) playerDataFolder.mkdir();

        ConfigManager configManager = this.plugin.getConfigManager();

        if(configManager.isFileExist(new File(playerDataFolder,this.playerUUID + ".yml"))){
            ConfigFile dataFile = plugin.getConfigManager().getConfig(new File(playerDataFolder,this.playerUUID + ".yml"));
            FileConfiguration playerDataConfiguration = dataFile.getFileConfiguration();

            this.usedFlightTime = playerDataConfiguration.getInt("data.used-flight-time",0);
            this.remainingFlightTime = playerDataConfiguration.getInt("data.remaining-flight-time",0);


        }else{
            this.usedFlightTime = 0;
            this.remainingFlightTime = 0;
            save();
        }


    }
    public void removeFlightTime(int toRemoveTime){
        this.remainingFlightTime = Math.max(remainingFlightTime-toRemoveTime,0);
    }
    public void addFlightTime(int toAddTime){
        this.remainingFlightTime += toAddTime;
    }
    public void setFlightTime(int remainingFlightTime) {
        this.remainingFlightTime = remainingFlightTime;
    }

    public int getFlightTime() {
        return remainingFlightTime;
    }


    public void addUsedFlightTime(int toAdd) {
        this.usedFlightTime += toAdd;
    }
    public void removeUsedFlightTime(int toRemove) {
        this.usedFlightTime = Math.max(usedFlightTime-toRemove,0);
    }
    public void setUsedFlightTime(int usedFlightTime) {
        this.usedFlightTime = usedFlightTime;
    }

    public int getUsedFlightTime() {
        return usedFlightTime;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setFlying(boolean flying) {
        isFlying = flying;
    }

    public boolean isFlying() {
        return isFlying;
    }


    public void save(){
        ConfigManager configManager = this.plugin.getConfigManager();
        ConfigFile configFile = configManager.getConfig(new File(this.plugin.getDataFolder() + "/player-data", this.playerUUID.toString() + ".yml"));

        configFile.getFileConfiguration().set("data.used-flight-time",this.usedFlightTime);
        configFile.getFileConfiguration().set("data.remaining-flight-time",this.remainingFlightTime);

        try {
            configFile.getFileConfiguration().save(configFile.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
