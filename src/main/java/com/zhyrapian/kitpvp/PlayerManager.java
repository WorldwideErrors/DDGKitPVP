package com.zhyrapian.kitpvp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.zhyrapian.kitpvp.utils.Utils;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class PlayerManager {

    private PlayerManager(){
        throw new IllegalStateException("PlayerManager class");
    }

    static MongoCollection<Document> playerstatsCollection = Kitpvp.playerstatsCollection;
    static final String KILLS = "kills";
    static final String DEATHS = "deaths";
    static final String MONEY = "money";
    static final String COLORCODE = "colorcode";
    static final String PREFIX = "prefix";

    static List<Object> getPlayerInfo(Player player){
        String uuid = player.getUniqueId().toString();
        Document playerDoc = playerstatsCollection.find(new Document("uuid", uuid)).first();

        Integer kills = (Integer) playerDoc.get(KILLS);
        Integer deaths = (Integer) playerDoc.get(DEATHS);
        Integer playerMoney = (Integer) playerDoc.get(MONEY);
        String ratio = format("%.2f", (double) kills / deaths);

        return Arrays.asList(uuid, kills, deaths, ratio, playerMoney);
    }

    static void storePlayer(Player player){
        String uuid = player.getUniqueId().toString();

        //This player has never played before and we just want to create a object for him
        Document players = new Document("uuid", uuid);
        players.put(KILLS, 0);
        players.put(DEATHS, 0);
        players.put(MONEY, 500);
        players.put(COLORCODE, "&7");
        players.put(PREFIX, "GAST");
        //Lets insert it in our collection:
        playerstatsCollection.insertOne(players);
    }

    public static void setMoney(Player player, Integer money, Integer increase) {
        String uuid = player.getUniqueId().toString();
        Integer newMoney = money + increase;

        Document filter = new Document("uuid", uuid);

        Document playerDocument = new Document(MONEY, newMoney);
        Document update = new Document("$set", playerDocument);

        UpdateOptions updateOptions = new UpdateOptions().upsert(true);

        playerstatsCollection.updateOne(filter, update, updateOptions);
    }

    public static void setKills(Player player, Integer kills, Integer increase) {
        String uuid = player.getUniqueId().toString();
        Integer newKills = kills + increase;

        Document filter = new Document("uuid", uuid);

        Document playerDocument = new Document(KILLS, newKills);
        Document update = new Document("$set", playerDocument);

        UpdateOptions updateOptions = new UpdateOptions().upsert(true);

        playerstatsCollection.updateOne(filter, update, updateOptions);
    }

    public static void setDeaths(Player player, Integer deaths, Integer increase){
        String uuid = player.getUniqueId().toString();
        Integer newDeaths = deaths + increase;

        Document filter = new Document("uuid", uuid);

        Document playerDocument = new Document(DEATHS, newDeaths);
        Document update = new Document("$set", playerDocument);

        UpdateOptions updateOptions = new UpdateOptions().upsert(true);

        playerstatsCollection.updateOne(filter, update, updateOptions);
    }

    static String getPrefix(Player sender){
        String uuid = sender.getUniqueId().toString();
        Document playerDoc = playerstatsCollection.find(new Document("uuid", uuid)).first();

        String colorcode = (String)playerDoc.get(COLORCODE);
        String prefix = (String)playerDoc.get(PREFIX);

        return Utils.textColor(colorcode) + prefix;
    }


}
