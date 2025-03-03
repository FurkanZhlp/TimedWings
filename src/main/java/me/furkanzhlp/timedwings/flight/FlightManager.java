package me.furkanzhlp.timedwings.flight;

import me.furkanzhlp.timedwings.TimedWings;
import me.furkanzhlp.timedwings.adapter.TimedWingsAdapter;
import me.furkanzhlp.timedwings.message.MessagePlaceholder;
import me.furkanzhlp.timedwings.player.PlayerData;
import me.furkanzhlp.timedwings.player.PlayerDataManager;
import me.furkanzhlp.timedwings.utils.TextUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class FlightManager {

    private final TimedWings plugin;
    private final PlayerDataManager playerDataManager;
    private BukkitTask flightCheckTask;

    private String durationFormat;
    private String actionBarMessage;

    public FlightManager(TimedWings plugin) {
        this.plugin = plugin;
        playerDataManager = plugin.getPlayerDataManager();

        initializeData();
        startFlightCheckTask();
    }

    public void initializeData(){
        this.actionBarMessage = plugin.getConfiguration().getString("general.action-bar.message");
        this.durationFormat = plugin.getConfiguration().getString("general.action-bar.duration-format");
    }

    public void startFlightCheckTask(){
        flightCheckTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin,() ->{
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (player.isFlying()) {
                    handleFlyingPlayer(player);
                }
            }
        },0L, 20L);
    }

    public void stopFlightCheckTask(){
        if(flightCheckTask != null) flightCheckTask.cancel();
    }

    public void handleFlyingPlayer(Player player){
        if(!playerDataManager.hasPlayerData(player)) return;
        PlayerData playerData = playerDataManager.getPlayerData(player);

//        if(!player.isFlying()) return;

        if(!plugin.getAdapterManager().getRegisteredAdapters().isEmpty()){
            for (TimedWingsAdapter adapter : plugin.getAdapterManager().getRegisteredAdapters().values()) {
                if(!adapter.canFly(player)){
                    plugin.getMessageManager().sendLanguageMessage(player,"Adapters."+adapter.getPluginName()+".Flight-Disabled");
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    return;
                }
            }
        }


        if(playerData.getFlightTime() > 0){
            playerData.addUsedFlightTime(1);
            playerData.removeFlightTime(1);
            if(plugin.getConfiguration().getBoolean("general.action-bar.enabled")){
                String actionBarMessage = this.actionBarMessage;
                String durationFormat = this.durationFormat;

                String formattedDuration = TextUtils.formatDuration(durationFormat,playerData.getFlightTime());
                actionBarMessage = actionBarMessage.replace("{duration}",formattedDuration);

                plugin.getActionBarManager().getActionBar().sendActionBar(player,TextUtils.colorize(actionBarMessage));
            }
        }else{
            player.setFlying(false);
            player.setAllowFlight(false);
        }

    }

    public void onDisable(){
        stopFlightCheckTask();
    }

}
