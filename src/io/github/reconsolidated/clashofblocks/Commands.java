package io.github.reconsolidated.clashofblocks;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    private ClashOfBlocks plugin;

    public Commands(ClashOfBlocks plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("help").setExecutor(this);
        Bukkit.getServer().getPluginCommand("spawnzombie").setExecutor(this);
        Bukkit.getServer().getPluginCommand("spawnnpc").setExecutor(this);
        Bukkit.getServer().getPluginCommand("movezombies").setExecutor(this);
        Bukkit.getServer().getPluginCommand("clearzombiesmove").setExecutor(this);
        Bukkit.getServer().getPluginCommand("createstructure").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players may execute this command");
            return true;
        }

        String commandName = command.getName().toLowerCase();

        if (commandName.equals("help")){
            plugin.showClashPlayers();
        }

        if (commandName.equals("spawnzombie")){
            sender.sendMessage("Spawning a zombie...");
            Player player = (Player) sender;
            plugin.spawnZombie(player, player.getLocation());

        }
        if (commandName.equals("spawnnpc")){
            sender.sendMessage("Spawning an npc...");
            Player player = (Player) sender;
            plugin.spawnNpc(player.getLocation());
        }

        if (commandName.equals("movezombies")){
            sender.sendMessage("Moving all zombies to you :)");
            Player player = (Player) sender;
            plugin.moveAllZombiesToLocation(player.getLocation());
        }

        if (commandName.equals("clearzombiesmove")){
            sender.sendMessage("Stopping all zombies");
            Player player = (Player) sender;
            plugin.clearZombiesDestiny();
        }



        return false;
    }
}
