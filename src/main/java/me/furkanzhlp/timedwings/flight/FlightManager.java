package me.furkanzhlp.timedwings.flight;

import me.furkanzhlp.timedwings.TimedWings;
import me.furkanzhlp.timedwings.adapter.TimedWingsAdapter;
import me.furkanzhlp.timedwings.player.PlayerData;
import me.furkanzhlp.timedwings.player.PlayerDataManager;
import me.furkanzhlp.timedwings.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class FlightManager {

    private final TimedWings plugin;
    private final PlayerDataManager playerDataManager;
    private BukkitTask flightCheckTask;
    private final HashMap<UUID, BukkitTask> wingEffects = new HashMap<>();

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
        flightCheckTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (player.isFlying()) {
                    handleFlyingPlayer(player);
                } else {
                    stopWingEffect(player);
                }
            }
        }, 0L, 20L);
    }

    public void stopFlightCheckTask(){
        if (flightCheckTask != null) flightCheckTask.cancel();
    }

    public void handleFlyingPlayer(Player player){
        if (!playerDataManager.hasPlayerData(player)) return;
        PlayerData playerData = playerDataManager.getPlayerData(player);

        if (!plugin.getAdapterManager().getRegisteredAdapters().isEmpty()) {
            for (TimedWingsAdapter adapter : plugin.getAdapterManager().getRegisteredAdapters().values()) {
                if (!adapter.canFly(player)) {
                    plugin.getMessageManager().sendLanguageMessage(player, "Adapters." + adapter.getPluginName() + ".Flight-Disabled");
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    stopWingEffect(player);
                    return;
                }
            }
        }

        if (playerData.getFlightTime() > 0) {
            playerData.addUsedFlightTime(1);
            playerData.removeFlightTime(1);
            if (plugin.getConfiguration().getBoolean("general.action-bar.enabled")) {
                String actionBarMessage = this.actionBarMessage;
                String durationFormat = this.durationFormat;

                String formattedDuration = TextUtils.formatDuration(durationFormat, playerData.getFlightTime());
                actionBarMessage = actionBarMessage.replace("{duration}", formattedDuration);

                plugin.getActionBarManager().getActionBar().sendActionBar(player, TextUtils.colorize(actionBarMessage));
            }
            startWingEffect(player);
        } else {
            player.setFlying(false);
            player.setAllowFlight(false);
            stopWingEffect(player);
        }
    }

    public void onDisable(){
        stopFlightCheckTask();
        for (BukkitTask task : wingEffects.values()) {
            task.cancel();
        }
        wingEffects.clear();
    }

    private void startWingEffect(Player player) {
        if (wingEffects.containsKey(player.getUniqueId())) return; // Zaten efekt çalışıyorsa tekrar başlatma

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isFlying()) {
                    stopWingEffect(player);
                    return;
                }

                Location loc = player.getLocation();
                double yaw = Math.toRadians(loc.getYaw() + 180); // Oyuncunun arkasına doğru yönlendirme
                double zOffset = -0.6; // Kanatları biraz daha arkaya alıyoruz!

                // ✅ Kanat Eğrisi (Mükemmel Konumlandırılmış)
                double[][] wingShape = {
                        {0.2, 0}, {0.4, 0.3}, {0.6, 0.6}, {0.5, 1.0}, {0.3, 1.4}, {0.15, 1.7}
                };

                for (double[] point : wingShape) {
                    double x = point[0];
                    double y = point[1];

                    // Kalınlaştırma için ekstra partiküller
                    for (double thickness = -0.05; thickness <= 0.05; thickness += 0.025) {
                        double rotatedX = (x + thickness) * Math.cos(yaw);
                        double rotatedZ = (x + thickness) * Math.sin(yaw) + zOffset;

                        Location leftWing = loc.clone().add(-rotatedX, y + 1, -rotatedZ); // Sol kanat
                        Location rightWing = loc.clone().add(rotatedX, y + 1, rotatedZ);  // Sağ kanat

                        player.getWorld().spawnParticle(Particle.END_ROD, leftWing, 0);
                        player.getWorld().spawnParticle(Particle.END_ROD, rightWing, 0);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);

        wingEffects.put(player.getUniqueId(), task);
    }


    // ❌ Kanat Efektini Durdur
    private void stopWingEffect(Player player) {
        if (wingEffects.containsKey(player.getUniqueId())) {
            wingEffects.get(player.getUniqueId()).cancel();
            wingEffects.remove(player.getUniqueId());
        }
    }
}
