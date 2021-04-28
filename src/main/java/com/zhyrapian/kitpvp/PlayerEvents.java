package com.zhyrapian.kitpvp;

import com.zhyrapian.kitpvp.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Objects;

import static com.zhyrapian.kitpvp.KitsManager.*;
import static com.zhyrapian.kitpvp.PlayerManager.*;
import static com.zhyrapian.kitpvp.Scoreboard.getScoreboard;
import static com.zhyrapian.kitpvp.SpawnManager.spawnPrefix;
import static com.zhyrapian.kitpvp.SpawnManager.spawnTeleport;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;
public class PlayerEvents implements Listener {

    //Set variables
    List<Object> playerInfo;
    Plugin plugin = getPlugin(Kitpvp.class);

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event){
        //Let player join without items
        Player player = event.getPlayer();
        player.getInventory().clear();
        event.setJoinMessage(Utils.joinMessage(player));

        try {
            //Try to load player data
            playerInfo = PlayerManager.getPlayerInfo(player);
            Utils.consoleSuccess(player.getName() + "'s data was loaded");
            player.sendMessage(Utils.textColor("&c&l") + "[DDG] " + Utils.textColor("&f") + playerInfo.get(0) + " & " + playerInfo.get(1) + " & " + playerInfo.get(2) + " & " + playerInfo.get(3).toString());
            getScoreboard(player);
        }catch (Exception exception){
            //The player is new, store him into the database.
            Utils.consoleError(player.getName() + "'s data coudn't be found");
            try {
                //Try to store the player in the database.
                PlayerManager.storePlayer(player);
                Utils.consoleSuccess(player.getName() + " has been saved");

                //Set scoreboard data
                getScoreboard(player);
            }catch (Exception storeException){
                //Player couldn't be stored.
                Utils.consoleError(player.getName() + "'s data coudn't be saved");
            }
        }

        try {
            //Teleport player to lobby coordinates.
            spawnTeleport("lobby", player);
        }catch (Exception exception){
            //Spawn was not set
            Utils.consoleError("Lobby was not set!");
            Utils.consoleError("Use /spawn set lobby to set a lobby.");
        }
        //Give player GUI for kit selection
        Bukkit.getScheduler().runTaskLater(plugin, () -> getGUI(player), 4);
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event){

        // Set variables
        Player killer = event.getEntity().getKiller();
        Player victim = event.getEntity().getPlayer();
        event.setDeathMessage(Utils.deathMessage(Objects.requireNonNull(killer), Objects.requireNonNull(victim)));
        List<Object> killerInfo = getPlayerInfo(killer);
        List<Object> victimInfo = getPlayerInfo(victim);
        Integer increasedMoney = 50;

        //Set values killer
        int killerlevel = killer.getLevel();
        killer.setLevel(killerlevel + 10);
        setMoney(killer, (Integer) killerInfo.get(4), increasedMoney);
        setKills(killer, (Integer) killerInfo.get(1), 1);
        Utils.actionBarMessage(killer, Utils.textColor("&6") + "+ $" + increasedMoney);
        getScoreboard(killer);

        int victimlevel = victim.getLevel();
        if (victimlevel > 0) {
            victim.setLevel(victimlevel - 10);
        }
        setDeaths(victim, (Integer)victimInfo.get(2), 1);
        getScoreboard(victim);
    }

    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent event){
        //Set variables
        Player sender = event.getPlayer();
        String prefix = getPrefix(sender);

        //Set prefix for chat
        event.setFormat(prefix + Utils.textColor("&f") + " | %s: %s");
    }

    //Initialize variables for inventory events
    Boolean isClicked;
    Boolean leftClick;

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event){
        //Set variables
        isClicked = false;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        int clickSlot = event.getSlot();

        if (!(title.equals("GUI"))){
            //If inventory is not GUI, ignore event
            return;
        }

        if (clickSlot < 0){
            //If player clicks outside of the GUI, ignore it.
            return;
        }

        leftClick = event.getClick().isLeftClick();
        long slotCount = kitCollection.countDocuments() - 1;

        if (clickSlot <= slotCount && Boolean.TRUE.equals(leftClick)){
            //If player clicks on a kit, give the kit items
            isClicked = true;
            player.closeInventory();
            getKit(player, clickSlot);
        }

        //Cancel moving items
        event.setCancelled(true);
    }

    @EventHandler
    public void inventoryCloseEvent(InventoryCloseEvent event){
        String title = event.getView().getTitle();
        Player player = (Player) event.getPlayer();
        if (!(title.equals("GUI"))){
            return;
        }

        if (Boolean.FALSE.equals(isClicked)){
            //If player didn't choose a kit, return to GUI
            Bukkit.getScheduler().runTaskLater(plugin, () -> getGUI(player), 3);
        }

        leftClick = false;
        isClicked = false;
    }

    @EventHandler
    public void playerRespawnEvent(PlayerRespawnEvent event){
        //When respawning, clean inventory and give kit menu
        Player player = event.getPlayer();
        player.getInventory().clear();

        Bukkit.getScheduler().runTaskLater(plugin, () -> getGUI(player), 3);
    }

    @EventHandler
    public void playerItemDamageEvent(PlayerItemDamageEvent event){
        //Disable durability on items
        event.setCancelled(true);
    }

    @EventHandler
    public void playerDropItemEvent(PlayerDropItemEvent event){
        //If player is in survival, he can't drop his items
        GameMode gamemode = event.getPlayer().getGameMode();
        if (gamemode == GameMode.SURVIVAL){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent event){
        //If player is in survival, he can't place blocks
        GameMode gamemode = event.getPlayer().getGameMode();
        Block blockPlaced = event.getBlockPlaced();
        Location blockLocation = event.getBlockPlaced().getLocation();
        
        if (gamemode != GameMode.SURVIVAL){
            return;
        }

        //Check if block is tnt
        if (blockPlaced.getType() == Material.TNT){
            //Auto ignite TNT without any blockdamage
            blockPlaced.setType(Material.AIR);
            blockPlaced.getWorld().spawnEntity(blockLocation.add(0.5, 0.2, 0.5), EntityType.PRIMED_TNT);
        }else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event){
        //If player is in survival, he can't drop water or lava
        GameMode gamemode = event.getPlayer().getGameMode();
        if (gamemode == GameMode.SURVIVAL){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBucketEmptyEvent(PlayerBucketEmptyEvent event){
        //If player is in survival, he can't drop water or lava
        GameMode gamemode = event.getPlayer().getGameMode();
        if (gamemode == GameMode.SURVIVAL){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void vehicleCreateEvent(VehicleCreateEvent event){
        //If player tries to place minecart, he can't place it
        event.setCancelled(true);
    }

    @EventHandler
    public void entityExplodeEvent(EntityExplodeEvent event) {
        //Disable explosions
        event.setCancelled(true);
    }
    
    @EventHandler
    public void signChangeEvent(SignChangeEvent event){
        //Set custom sign for WARP
        Player player = event.getPlayer();

        String prefix = event.getLine(1).toUpperCase();
        String name = event.getLine(2);
        if(prefix.contains("[WARP]")){
            event.setLine(1, Utils.textColor("&c") + prefix);
            event.setLine(2, name);
            player.sendMessage(Utils.textColor("&6") + prefix + " " + Utils.textColor("&7") + name + " is succesvol gecreërd.");
        }
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event){
        //Set variables
        Player player = event.getPlayer();
        BlockState clickedBlock = event.getClickedBlock().getState();
        String signPrefix;
        String name;

        if (!(clickedBlock instanceof Sign)){
            //Interaction is not with sign
            return;
        }

        signPrefix = ((Sign) clickedBlock).getLine(1);
        name = ((Sign) clickedBlock).getLine(2);

        //Teleport player to warp location if player is gamemode survival
        if (!(signPrefix.equalsIgnoreCase(Utils.textColor("&c") + "[WARP]"))){
            return;
        }

        if (player.getGameMode().equals(GameMode.SURVIVAL)){
             spawnTeleport(name, player);
        }else {
            player.sendMessage(Utils.commandPrefix(spawnPrefix) + "Warpen kan alleen in Survival");
        }
    }
}
