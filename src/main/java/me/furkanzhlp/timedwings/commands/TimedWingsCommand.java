package me.furkanzhlp.timedwings.commands;

import me.furkanzhlp.timedwings.TimedWings;
import me.furkanzhlp.timedwings.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TimedWingsCommand implements CommandExecutor {
    private final TimedWings plugin;

    public TimedWingsCommand(TimedWings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("settime")){
                if(args.length > 1){
                    String targetPlayerName = args[1];
                    Player targetPlayer = Bukkit.getPlayerExact(targetPlayerName);
                    if(targetPlayer != null){
                        if(args.length > 2){
                            Integer toSetTime = Integer.parseInt(args[2]);
                            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(targetPlayer);
                            playerData.setFlightTime(toSetTime);
                            commandSender.sendMessage("Süre belirlendi.");
                        }else{
                            commandSender.sendMessage("Lütfen geçerli bir süre giriniz.");
                        }
                    }else{
                        commandSender.sendMessage("Girdiğiniz oyuncu oyunda değil.");
                    }

                }else{
                    commandSender.sendMessage("Lütfen Oyuncu Adı Giriniz.");
                }
            }else{
                commandSender.sendMessage("Böyle bir işlem bulunamadı!");
            }
        }else{
            commandSender.sendMessage("Hatalı Komut Girdiniz.");
        }

        return true;
    }
}
