package me.furkanzhlp.timedwings.placeholder;

import me.furkanzhlp.timedwings.TimedWings;
import me.furkanzhlp.timedwings.manager.Manager;
import me.furkanzhlp.timedwings.player.PlayerData;
import me.furkanzhlp.timedwings.player.PlayerDataManager;
import me.furkanzhlp.timedwings.utils.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class PlaceholderManager extends Manager {

    private final boolean placeholderAPIEnabled;
    public PlaceholderManager(TimedWings plugin) {
        super(plugin);

        PluginManager pluginManager = this.plugin.getServer().getPluginManager();
        this.placeholderAPIEnabled = pluginManager.getPlugin("PlaceholderAPI") != null;

    }


    public void registerPlaceholders() {
        if (this.placeholderAPIEnabled) {
            new PlaceholderAPI(this.plugin).register();
        }
    }
    public void unregisterPlaceholders() {
        if (this.placeholderAPIEnabled) {
            new PlaceholderAPI(this.plugin).unregister();
        }
    }

    public String processPlaceholder(Player player, String placeholder){
        if (player == null || placeholder == null) {
            return "";
        }
        FileConfiguration language = plugin.getLanguage();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        PlayerData playerData = playerDataManager.getPlayerData(player);

        String returnValue = null;

        switch (placeholder){
            case "timedwings_total_flight_time":
                if(playerData != null){
                    if(playerData.getFlightTime() > 0){
                        returnValue = TextUtils.colorize(TextUtils.formatDuration(language.getString("placeholder.timedwings_total_flight_time.time","{y} {mo} {d} {h} {m} {s}"),playerData.getFlightTime()));
                    }else{
                        returnValue = TextUtils.colorize(language.getString("placeholder.timedwings_total_flight_time.no-time","No Time"));
                    }
                }else{
                    returnValue = TextUtils.colorize(language.getString("placeholder.timedwings_total_flight_time.no-time","No Time"));
                }
                break;
            case "timedwings_total_flight_time_seconds":
                if(playerData != null){
                    if(playerData.getFlightTime() > 0){
                        returnValue = TextUtils.colorize(language.getString("placeholder.timedwings_total_flight_time_seconds.time","{seconds}").replace("{seconds}",""+playerData.getFlightTime()));
                    }else{
                        returnValue = TextUtils.colorize(language.getString("placeholder.timedwings_total_flight_time_seconds.no-time","No Time"));
                    }
                }else{
                    returnValue = TextUtils.colorize(language.getString("placeholder.timedwings_total_flight_time_seconds.no-time","No Time"));
                }
                break;
        }

        return returnValue;
    }






}
