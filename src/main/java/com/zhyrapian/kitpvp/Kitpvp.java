package com.zhyrapian.kitpvp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.zhyrapian.kitpvp.utils.Utils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import static com.zhyrapian.kitpvp.KitsManager.fillGUI;

public final class Kitpvp extends JavaPlugin {

    // Set variables
    static MongoCollection<Document> playerstatsCollection;
    static MongoCollection<Document> kitCollection;
    static MongoCollection<Document> spawnsCollection;
    static String host;
    static Integer port;
    static String collectionPlayerstats;
    static String collectionKits;
    static String collectionSpawns;

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfig();
        setConnection(host, port);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        setKeepInventory();
        fillGUI();
        this.getCommand("spawn").setExecutor(new SpawnCommands());
        this.getCommand("kit").setExecutor(new KitCommands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
    }

    public static void loadConfig(){
        //Get config settings
        getPlugin(Kitpvp.class).getConfig().options().copyDefaults(true);
        host = getPlugin(Kitpvp.class).getConfig().getString("host");
        port = getPlugin(Kitpvp.class).getConfig().getInt("port");
        collectionPlayerstats = getPlugin(Kitpvp.class).getConfig().getString("collections.playerstats");
        collectionKits = getPlugin(Kitpvp.class).getConfig().getString("collections.kits");
        collectionSpawns = getPlugin(Kitpvp.class).getConfig().getString("collections.spawns");
    }

    //Set variables MongoDatabase
    static MongoClient mongoClient;
    static MongoDatabase database;

    static void setConnection(String ip, Integer port){

        //connect to the server/database/collection
        try {
            mongoClient = MongoClients.create("mongodb://Zhyrapian:12345678@" + ip + ":" + port + "/?authSource=admin&readPreference=primary&appname=MongoDB%20Compass&ssl=false");
        }catch (Exception exception){
            // Server not found!
            Utils.consoleError("Could not connect to the database");
            exception.printStackTrace();
            return;
        }
        // Get Database called "ddgkitpvp"
        database = mongoClient.getDatabase("ddgkitpvp");
        Utils.consoleSuccess("Database connected");

        // Set collection variables
        kitCollection = database.getCollection(collectionKits);
        playerstatsCollection = database.getCollection(collectionPlayerstats);
        spawnsCollection = database.getCollection(collectionSpawns);
    }

    void setKeepInventory(){
        String kitPVPWorld;
        try{
            kitPVPWorld = getConfig().getString("world");
            assert kitPVPWorld != null;
            World world = Bukkit.getWorld(kitPVPWorld);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            Utils.consoleInfo("Keepinventory is changed to true");
        }catch (Exception exception){
            Utils.consoleError("Couldn't change gamerule!");
        }

    }
}
