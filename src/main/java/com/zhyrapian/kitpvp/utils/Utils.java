package com.zhyrapian.kitpvp.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {

    private Utils(){
        throw new IllegalStateException("Utils class");
    }

    public static void consoleSuccess(String message) {
        Bukkit.getConsoleSender().sendMessage(textColor("&2") + "[SUCCESS] " + textColor("&7") + message);
    }

    public static void consoleError(String message) {
        Bukkit.getConsoleSender().sendMessage(textColor("&c") + "[ERROR] " + textColor("&7") + message);
    }

    public static void consoleInfo(String message) {
        Bukkit.getConsoleSender().sendMessage(textColor("&e") + "[INFO] " + textColor("&7") + message);
    }

    public static String textColor (String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String deathMessage(Player killer, Player victim) {
        return Utils.textColor("&6") + killer.getName()
                + Utils.textColor("&f") + ": 'Do or do not… there is no try, "
                + Utils.textColor("&c") + victim.getName() + Utils.textColor("&f") + ".'";
    }

    public static String commandPrefix(String message) {
        return Utils.textColor("&6") + message + Utils.textColor("&7") + " ";
    }

    public static String joinMessage(Player player){
        return Utils.textColor("&c&l") + "[DDG] " + Utils.textColor("&f") + "Welkom op de server, " + Utils.textColor("&6") + player.getName();
    }

    public static void actionBarMessage(Player player, String message){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(textColor("&6") + message));
    }

    public static void spawnCommandInfo(Player sender) {
        //Send player usage of command "/spawn"
        sender.sendMessage(textColor("&6&l") + "[SPAWN] " + textColor("&f")+ "Do you mean:");
        sender.sendMessage(textColor("&e") + "/spawn tp [name]"
                + textColor("&f") + " => " + textColor("&7") + "Hiermee kan je naar een spawn gaan");
        sender.sendMessage(textColor("&e") + "/spawn set [name]" + textColor("&f") + " => "
                + textColor("&7") + "Hiermee kan je een spawn toevoegen");
        sender.sendMessage(textColor("&e") + "/spawn modify [name]" + textColor("&f")
                + " => " + textColor("&7") + "Hiermee kan je een spawn wijzigen");
        sender.sendMessage(textColor("&e") + "/spawn remove [name]"
                + textColor("&f") + " => " + textColor("&7") + "Hiermee kan je een spawn verwijderen");
    }

    public static void kitCommandInfo(Player sender) {
        //Send player usage of command "/spawn"
        sender.sendMessage(textColor("&6&l") + "[KIT] " + textColor("&f")+ "Do you mean:");
        sender.sendMessage(textColor("&e") + "/kit menu"
                + textColor("&f") + " => " + textColor("&7") + "Hiermee haal je het kit menu op");
        sender.sendMessage(textColor("&e") + "/kit save [name]"
                + textColor("&f") + " => " + textColor("&7") + "Hiermee kan je een kit opslaan");
        sender.sendMessage(textColor("&e") + "/kit modify [name]" + textColor("&f") + " => "
                + textColor("&7") + "Hiermee kan je de inventory van een kit aanpassen");
        sender.sendMessage(textColor("&e") + "/kit remove [name]" + textColor("&f")
                + " => " + textColor("&7") + "Hiermee kan je een kit verwijderen");
    }
}
