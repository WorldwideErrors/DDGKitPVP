package com.zhyrapian.kitpvp;

import com.zhyrapian.kitpvp.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.zhyrapian.kitpvp.KitsManager.*;

public class KitCommands implements CommandExecutor {

    static String kitPrefix = "[KIT]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            //Command is used by console
            Utils.consoleError("You should be a player to use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            //The arguments are less or more than 1
            Utils.kitCommandInfo(player);
            return true;
        }

        if(args[0].equalsIgnoreCase("menu")){
            getGUI(player);
            player.sendMessage(Utils.commandPrefix(kitPrefix) + "Kit menu is aan het laden..");
            return true;
        }

        if (!(args[0].equalsIgnoreCase("menu")) && args.length < 2) {
            //The arguments are less or more than 1
            Utils.kitCommandInfo(player);
            return true;
        }

        if(args[0].equalsIgnoreCase("save")){
            //Set variables
            ItemStack[] items = player.getInventory().getContents();
            String kitID = "kit_" + args[1];
            String kitname = args[1];

            storeKit(player, items, kitname, kitID);

            return true;
        }
        
        if (args[0].equalsIgnoreCase("modify")){
            String name = args[1];

            try {
                setKitValue(player, name);
                player.sendMessage(Utils.commandPrefix(kitPrefix) + Utils.textColor("&2") + name + Utils.textColor("&7")
                        + " is successvol aangepast!");
            }catch (Exception exception){
                player.sendMessage(Utils.commandPrefix(kitPrefix) + "Kan de kit niet aanpassen!");
                return true;
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")){
            String kitname = args[1];
            try {
                removeKit(kitname);
                player.sendMessage(Utils.commandPrefix("[KIT]") + Utils.textColor("&c") + kitname + Utils.textColor("&7")
                        + " is successvol verwijderd!");
            }catch (Exception exception){
                player.sendMessage(Utils.commandPrefix(kitPrefix) + "Kit bestaat niet");
                return true;
            }
            return true;
        }

        Utils.kitCommandInfo(player);
        return false;
    }
}
