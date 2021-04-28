package com.zhyrapian.kitpvp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.zhyrapian.kitpvp.utils.Utils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class KitsManager implements Listener {

    private static Inventory kitGUI;
    static MongoCollection<Document> kitCollection = Kitpvp.kitCollection;
    static String kitPrefix = "[KIT]";
    static String sHeadcolor = "headcolor";
    static String sItems = "items";

    private KitsManager(){
        throw new IllegalStateException("KitsManager class");
    }

    public static void fillGUI(){

        kitGUI = Bukkit.createInventory(null, 27, "GUI");

        for (int slot = 0; slot < kitCollection.countDocuments(); slot++) {
            Document kitsDoc = kitCollection.find(new Document("slot", slot)).first();

            //Set variables
            String icon = (String) kitsDoc.get("icon");
            String titleColor = (String) kitsDoc.get(sHeadcolor);
            String subColor = (String) kitsDoc.get("subcolor");
            String name = (String) kitsDoc.get("name");
            String description = (String) kitsDoc.get("description");

            Material materialIcon = Material.matchMaterial(icon);
            ItemStack stack = new ItemStack(materialIcon, 1);

            //Set icon meta
            ItemMeta iconMeta = stack.getItemMeta();
            iconMeta.setDisplayName(Utils.textColor(titleColor) + name);
            iconMeta.setLore(Collections.singletonList(Utils.textColor(subColor + "&o") + description));
            stack.setItemMeta(iconMeta);

            kitGUI.setItem(slot, new ItemStack(stack));
        }
    }

    public static void getGUI(Player player){
        player.openInventory(kitGUI);
    }

    static void getKit(Player player, Integer slot){

        Document kitsDoc = kitCollection.find(new Document("slot", slot)).first();
        String kitname = (String) kitsDoc.get("name");
        String color = (String) kitsDoc.get(sHeadcolor);
        Inventory inventory = player.getInventory();

        try {
            String serializedItems = (String) kitsDoc.get(sItems);
            byte[] kitItems = Base64.getDecoder().decode(serializedItems);
            ByteArrayInputStream itemInput = new ByteArrayInputStream(kitItems);
            BukkitObjectInputStream inputStream = new BukkitObjectInputStream(itemInput);

            ItemStack[] itemStack = (ItemStack[]) inputStream.readObject();

            inventory.setContents(itemStack);
        }catch (Exception exception){
            player.sendMessage(Utils.commandPrefix(kitPrefix) + "Kit kon niet opgeslagen worden!");
        }

        Bukkit.getConsoleSender().sendMessage("DIT IS DAT ENE " + Arrays.toString(inventory.getContents()));
        player.sendMessage(Utils.commandPrefix(kitPrefix) +  "Je hebt de kit " + Utils.textColor(color) + kitname + Utils.textColor("&7") + " geselecteerd.");
    }

    public static void storeKit(Player player, ItemStack[] items, String kitname, String kitID){
        try {
            //Get slotcount
            Integer slot = Math.toIntExact(kitCollection.countDocuments());

            //Deserialize Itemstack
            ByteArrayOutputStream itemOutput = new ByteArrayOutputStream();
            BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(itemOutput);

            outputStream.writeObject(items);
            outputStream.flush();

            byte[] serializedObject = itemOutput.toByteArray();

            String encodedObject = Base64.getEncoder().encodeToString(serializedObject);


            //Store kit to DB
            Document kit = new Document("_id", kitID);
            kit.put("name", kitname);
            kit.put("description", "default");
            kit.put("icon", "DIAMOND_CHESTPLATE");
            kit.put("slot", slot);
            kit.put(sHeadcolor, "&6");
            kit.put("subcolor", "&7");
            kit.put(sItems, encodedObject);

            //Insert kit into collection
            kitCollection.insertOne(kit);
            player.sendMessage(Utils.commandPrefix(kitPrefix) + Utils.textColor("&2") + kitname + Utils.textColor("&7") + " is succesvol opgeslagen op slot " + slot);
            fillGUI();
        } catch (Exception exception) {
            //Kit already exists
            player.sendMessage(Utils.commandPrefix(kitPrefix) + Utils.textColor("&c") + kitname + Utils.textColor("&7") + " bestaat al!");
            Utils.kitCommandInfo(player);
        }
    }

    public static void setKitValue(Player player, String name) {

        //Fill itemstack with user's inventory
        ItemStack[] items = player.getInventory().getContents();

        String value = null;
        try {
            //Serialize itemstack to encoded string
            ByteArrayOutputStream itemOutput = new ByteArrayOutputStream();
            BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(itemOutput);

            outputStream.writeObject(items);
            outputStream.flush();

            byte[] serializedObject = itemOutput.toByteArray();

            //Fill variable with encoded string
            value = Base64.getEncoder().encodeToString(serializedObject);
        }catch (Exception exception){
            player.sendMessage(Utils.commandPrefix("[KIT]") + "Er ging iets fout met het krijgen van je kit!");
        }

        //Set items kit
        String kitID = "kit_" + name;
        Document filter = new Document("_id", kitID);

        Document kitDocument = new Document(sItems, value);
        Document update = new Document("$set", kitDocument);

        UpdateOptions updateOptions = new UpdateOptions().upsert(true);

        //Update value X of Document
        kitCollection.updateOne(filter, update, updateOptions);
        fillGUI();
    }

    static void removeKit(String name){
        //Remove kit
        kitCollection.findOneAndDelete(new Document("name", name));
        fillGUI();
    }
}
