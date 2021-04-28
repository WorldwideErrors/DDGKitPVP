package com.zhyrapian.kitpvp;

import com.zhyrapian.kitpvp.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.zhyrapian.kitpvp.SpawnManager.*;

public class SpawnCommands implements CommandExecutor {

    static String spawnPrefix = "[SPAWN]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)){
            //Command is used by console
            Utils.consoleError("You should be a player to use this command.");
            return true;
        }

        Player player = (Player) sender;

        if(args.length < 2){
            //The arguments are less or more than 2
            Utils.spawnCommandInfo(player);
            return true;
        }

        //Set variables
        String name = args[1];
        Location playerLocation = player.getLocation();

        if(args[0].equalsIgnoreCase("set")){
            try{
                //Store spawn, if not existing
                storeSpawn(name, playerLocation);
                player.sendMessage(Utils.commandPrefix(spawnPrefix) + Utils.textColor("&6") + name + Utils.textColor("&7")
                        + " is opgeslagen als een spawnlocatie");
                Utils.consoleSuccess(player.getName() + " has saved " + name + " as a new spawnlocation");
            }catch (Exception exception){
                //Spawn with name of arguments already exists
                player.sendMessage(Utils.commandPrefix(spawnPrefix) + Utils.textColor("&c") + name + Utils.textColor("&f") + " bestaat al!");
                Utils.spawnCommandInfo(player);
                Utils.consoleError(player.getName() + " tried to save an existing spawnlocation");
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("modify")){
            //Change coordinates of spawn
            setLocation(name, playerLocation);
            player.sendMessage(Utils.commandPrefix(spawnPrefix) + Utils.textColor("&2") + name + Utils.textColor("&f")
                    + " succesvol naar de coordinaten verplaatst!");
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            //Remove spawn with name of arguments
            removeSpawn(name);
            player.sendMessage(Utils.commandPrefix(spawnPrefix) + Utils.textColor("&2") + name + Utils.textColor("&7")
                    + " is successvol verwijderd!");
            return true;
        }

        if(args[0].equalsIgnoreCase("tp")){
            //Teleport player to spawnlocation
            spawnTeleport(name, player);
            return true;
        }

        Utils.spawnCommandInfo(player);
        return false;
    }
}
