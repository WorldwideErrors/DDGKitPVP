package com.zhyrapian.kitpvp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.zhyrapian.kitpvp.utils.Utils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class SpawnManager {

    //Set variables
    private SpawnManager(){
        throw new IllegalStateException("SpawnManager class");
    }
    static MongoCollection<Document> spawnCollection = Kitpvp.spawnsCollection;
    static String sLocation = "location";
    static String spawnPrefix = "[SPAWN]";

    static void storeSpawn(String name, Location location){
        String encodedObject = null;
        try {
            //Encode location to string
            ByteArrayOutputStream locationOutput = new ByteArrayOutputStream();
            BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(locationOutput);

            outputStream.writeObject(location);
            outputStream.flush();

            byte[] serializedObject = locationOutput.toByteArray();

            encodedObject = Base64.getEncoder().encodeToString(serializedObject);
        }catch (Exception exception){
            Bukkit.getConsoleSender().sendMessage("Er gaat iets flink fout");
        }

        //Add a new spawnlocation
        Document spawn = new Document("_id", name);
        spawn.put(sLocation, encodedObject);
        //Lets insert it in our collection:
        spawnCollection.insertOne(spawn);
    }

    static void spawnTeleport(String name, Player player){

        //Get spawnlocation
        Document spawnDoc = spawnCollection.find(new Document("_id", name)).first();

        try {
            //Decode string to spawnlocation
            String serializedLocation = (String) spawnDoc.get(sLocation);
            byte[] spawnLocation = Base64.getDecoder().decode(serializedLocation);
            ByteArrayInputStream locationOutput = new ByteArrayInputStream(spawnLocation);
            BukkitObjectInputStream inputStream = new BukkitObjectInputStream(locationOutput);

            //Teleport player to spawnlocation
            Location worldspawn = (Location) inputStream.readObject();
            player.teleport(worldspawn);

            //Tell player he was teleported
            player.sendMessage(Utils.commandPrefix(spawnPrefix) + Utils.textColor("&6") + player.getName()
                    + Utils.textColor("&f") + ", je bent naar " + Utils.textColor("&2") + name + Utils.textColor("&7")
                    + " geteleporteerd.");
        }catch (Exception exception){
            //Spawnlocation doens't exist
            player.sendMessage(Utils.commandPrefix(spawnPrefix) + "Spawn bestaat niet!");
        }
    }

    public static void setLocation(String name, Location location) {
        //Modify spawnlocation
        Document filter = new Document("_id", name);

        String encodedObject = null;

        try {
            //Encode spawnlocation to string
            ByteArrayOutputStream locationOutput = new ByteArrayOutputStream();
            BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(locationOutput);

            outputStream.writeObject(location);
            outputStream.flush();

            byte[] serializedObject = locationOutput.toByteArray();

            encodedObject = Base64.getEncoder().encodeToString(serializedObject);
        }catch (Exception exception){
            Bukkit.getConsoleSender().sendMessage("Er gaat iets flink fout");
        }

        //Replace string of location
        Document spawnDocument = new Document(sLocation, encodedObject);
        Document update = new Document("$set", spawnDocument);

        UpdateOptions updateOptions = new UpdateOptions().upsert(true);

        //Update value of Document
        spawnCollection.updateOne(filter, update, updateOptions);
    }

    static void removeSpawn(String name){
        //Remove spawn
        spawnCollection.findOneAndDelete(new Document("_id", name));
    }

}
