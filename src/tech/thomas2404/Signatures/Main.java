package tech.thomas2404.Signatures;

import tech.thomas2404.Signatures.Files.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    //All the variables that need to be used in more than one method.
    Inventory customIcon = Bukkit.getServer().createInventory(null, 9, ChatColor.YELLOW + "" + ChatColor.BOLD + "Custom Icon");
    Inventory colors = Bukkit.getServer().createInventory(null, 18, ChatColor.YELLOW + "" + ChatColor.BOLD + "Signature Color");
    Inventory customColors = Bukkit.getServer().createInventory(null, 18, ChatColor.YELLOW + "" + ChatColor.BOLD + "Signature Color");
    Inventory sig = Bukkit.getServer().createInventory(null, 9, ChatColor.YELLOW + "" + ChatColor.BOLD + "Add Signature");
    List<String> newLore = new ArrayList<String>();
    Inventory inv;
    Inventory invColor;
    String sigLore = new String();
    String sigColor = ("BLACK");
    int numberOfSigs = 0;
    public DataManager data;

    @Override
    //Sets up everything that the plugin needs to run when the plugin is reloaded.
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        createInvColor();
        createCustomColor();
        makeSignatures();
        customIcon();
        this.saveDefaultConfig();
        this.data = new DataManager(this);
        numberOfSigs = numberOfSigs = (this.data.getConfig().getInt("numberOfSigs"));
        if (numberOfSigs > 8) {
            if (numberOfSigs > 17) {
                if (numberOfSigs > 26) {
                    sig = Bukkit.getServer().createInventory(null, 36, ChatColor.YELLOW + "" + ChatColor.BOLD + "Add Signature");

                } else {
                    sig = Bukkit.getServer().createInventory(null, 27, ChatColor.YELLOW + "" + ChatColor.BOLD + "Add Signature");
                }
            } else {
                sig = Bukkit.getServer().createInventory(null, 18, ChatColor.YELLOW + "" + ChatColor.BOLD + "Add Signature");
            }
        } else {
            sig = Bukkit.getServer().createInventory(null, 9, ChatColor.YELLOW + "" + ChatColor.BOLD + "Add Signature");
        }
    }

    @Override
    //Nothing needs to be done when the plugin is disabled :).
    public void onDisable() {
    }

    @Override
    //Handles any commands that are sent.
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String newArgs = new String();
        if (label.equalsIgnoreCase("signature") || label.equalsIgnoreCase("sig")) {
            //Opens up the GUI if a player doesn't input a custom signature.
            Player player = (Player) sender;
            Material notAllowed = player.getInventory().getItemInMainHand().getType();
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console can not execute this command.");
                return true;
            } else if (player.getInventory().getItemInMainHand().getType() == (Material.AIR)) {
                player.sendMessage(ChatColor.RED + ("You must be holding an item in your hand to run this command!"));
                return true;
            } else if (args.length == 0) {
                //Player didn't input a custom signature, open up the saved signature GUI.
                makeSignatures();
                player.openInventory(sig);
                return true;
            } else {
                if (notAllowed == (Material.DIAMOND_SWORD) || notAllowed == (Material.IRON_SWORD)) {
                    //Checks if the player is holding a toy, tells them they can't sign it.
                    player.sendMessage(ChatColor.RED + "Toys can't be signed!");
                    return true;
                }
                //Player input a custom signature and isn't holding a toy, open the color GUI.
                newArgs = String.join(" ", args);
                this.data.getConfig().set("temp", newArgs);
                player.openInventory(customColors);
                return true;
            }
        }

        if (label.equalsIgnoreCase("sigremove") || (label.equalsIgnoreCase("signatureremove"))) {
            //Removes the last signature on the item that the player is holding.
            if (!(sender instanceof Player)) {
                //Console can't send this command.
                sender.sendMessage("Console can not execute this command.");
                return true;
            }
            Player player = (Player) sender;
            ItemStack is = player.getInventory().getItemInMainHand();
            ItemMeta im = is.getItemMeta();
            List<String> lore;
            if (im.hasLore()) {
                lore = im.getLore();
            } else {
                lore = new ArrayList<>();
            }
            if (lore.size() > 0) {
                //If the item has lore, remove the last line.
                int indexOfLastElement = lore.size() - 1;
                lore.remove(indexOfLastElement);
                im.setLore(lore);
                player.getInventory().getItemInMainHand().setItemMeta(im);
                player.sendMessage(ChatColor.YELLOW + "Removed last line of lore.");
            } else {
                //If the item doesn't have any lore, tell the player.
                player.sendMessage("There is no more lore to remove!");
            }
        }

        if (label.equalsIgnoreCase("sigadd")) {
            //Add the custom signature with it's color to the GUI.
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console can not execute this command.");
                return true;
            }
            Player player = (Player) sender;
            ItemStack is = player.getInventory().getItemInMainHand();
            ItemMeta im = is.getItemMeta();
            List<String> lore;
            if (im.hasLore()) {
                lore = im.getLore();
            } else {
                lore = new ArrayList<>();
            }
            newArgs = this.data.getConfig().getString("temp");
            newArgs = (ChatColor.valueOf(sigColor) + newArgs);
            lore.add(newArgs);
            im.setLore(lore);
            player.getInventory().getItemInMainHand().setItemMeta(im);
            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "You signed this item with:" + ChatColor.RESET + " " + newArgs);
        }

        if (label.equalsIgnoreCase("sigsave") || (label.equals("signaturesave"))) {
            //If the player is trying to add a signature to the GUI, run this.
            makeSignatures();
            Player player = (Player) sender;
            if (!(sender instanceof Player)) {
                //Console can't send this command.
                sender.sendMessage("Console can not execute this command.");
                return true;
            }
            if (args.length == 0) {
                //If the player didn't add what they want the signature to be, tell them.
                player.sendMessage(ChatColor.RED + "Usage is /signaturesave [signiture]");
            } else {
                //Player has input what they want the signature to be, open the color inventory.
                sigLore = String.join(" ", args);
                player.openInventory(colors);
            }
            return true;
        }
        return true;
    }

    @EventHandler
    //Handles the GUI that custom signatures are saved in.
    public void onClick(InventoryClickEvent event) {
        if (!(event.getClickedInventory().equals(sig)))
            return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        if (event.getClickedInventory().equals(InventoryType.PLAYER)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        Material notAllowed = player.getInventory().getItemInMainHand().getType();
        ItemStack is = player.getInventory().getItemInMainHand();
        ItemMeta im = is.getItemMeta();
        List<String> deleteLore = new ArrayList<String>();
        List<String> lore;
        if (im.hasLore()) {
            lore = im.getLore();
        } else {
            lore = new ArrayList<>();
        }
        im.setLore(lore);

        //Adds the signature from the custom GUI to the item that the player is holding.
        List<String> cursorMeta = (event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getLore());
        String firstElement = cursorMeta.stream()
                .findFirst()
                .map(Object::toString)
                .orElse(null);
        String newMeta = firstElement;

        if (!(notAllowed.equals(Material.DIAMOND_SWORD) || notAllowed.equals(Material.IRON_SWORD))) {
            //If the player is not holding a toy, adds the signature.
            lore.add(newMeta);
            im.setLore(lore);
            player.getInventory().getItemInMainHand().setItemMeta(im);
            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "You signed this item with: " + newMeta);

        } else {
            //If the player is holding a toy, tells them they can't sign it.
            player.sendMessage(ChatColor.RED + "Toys can't be signed!");
            player.closeInventory();
            event.setCancelled(true);
        }
    }

    @EventHandler
    //Handles the color GUI for signatures that are going to be added to the config.yml file.
    public void onClickColor(InventoryClickEvent event) {
        if (event.getClickedInventory().equals(colors) == false)
            return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        if (event.getClickedInventory().equals(InventoryType.PLAYER)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        String sigName = new String();
        //Checks what slot is clicked, then sets sigColor to the color.
        if (event.getSlot() == 0)
            sigColor = "DARK_RED";
        if (event.getSlot() == 1)
            sigColor = "RED";
        if (event.getSlot() == 2)
            sigColor = "GOLD";
        if (event.getSlot() == 3)
            sigColor = "YELLOW";
        if (event.getSlot() == 4)
            sigColor = "GREEN";
        if (event.getSlot() == 5)
            sigColor = "DARK_GREEN";
        if (event.getSlot() == 6)
            sigColor = "AQUA";
        if (event.getSlot() == 7)
            sigColor = "DARK_AQUA";
        if (event.getSlot() == 9)
            sigColor = "BLUE";
        if (event.getSlot() == 10)
            sigColor = "DARK_BLUE";
        if (event.getSlot() == 11)
            sigColor = "DARK_PURPLE";
        if (event.getSlot() == 12)
            sigColor = "LIGHT_PURPLE";
        if (event.getSlot() == 13)
            sigColor = "WHITE";
        if (event.getSlot() == 14)
            sigColor = "GRAY";
        if (event.getSlot() == 15)
            sigColor = "DARK_GRAY";
        if (event.getSlot() == 16)
            sigColor = "BLACK";

        sigName = (ChatColor.valueOf(sigColor) + "" + ChatColor.BOLD + player.getName());
        sigLore = ChatColor.valueOf(sigColor) + sigLore;
        //Checks if the GUI needs to be made bigger, has a max of 36 slots.
        if (numberOfSigs > 8) {
            if (numberOfSigs > 17) {
                if (numberOfSigs > 26) {
                    if (numberOfSigs == 36) {
                        player.sendMessage(ChatColor.RED + "You've reached the maximum number of signatures!");
                        return;
                    } else {
                        sig = Bukkit.getServer().createInventory(null, 36, ChatColor.YELLOW + "" + ChatColor.BOLD + "Add Signature");
                    }
                } else {
                    sig = Bukkit.getServer().createInventory(null, 27, ChatColor.YELLOW + "" + ChatColor.BOLD + "Add Signature");
                }
            } else {
                sig = Bukkit.getServer().createInventory(null, 18, ChatColor.YELLOW + "" + ChatColor.BOLD + "Add Signature");
            }
        } else {
            sig = Bukkit.getServer().createInventory(null, 9, ChatColor.YELLOW + "" + ChatColor.BOLD + "Add Signature");
        }
        //Adds the custom signature to the config.yml file.
        numberOfSigs = (this.data.getConfig().getInt("numberOfSigs")) + 1;
        this.data.getConfig().set("numberOfSigs", numberOfSigs);
        this.data.getConfig().set("signatures.customSig." + numberOfSigs + ".name", (sigName));
        this.data.getConfig().set("signatures.customSig." + numberOfSigs + ".lore", (sigLore));
        data.saveConfig();
        //Closes the color inventory and opens the custom icon inventory.
        player.closeInventory();
        player.openInventory(customIcon);
    }

    @EventHandler
    //Handles color selection for custom lore.
    public void onClickCustomColor(InventoryClickEvent event) {
        if (!event.getClickedInventory().equals(customColors))
            return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        if (event.getClickedInventory().equals(InventoryType.PLAYER)) return;
        if (event.getClick().equals(null)) return;


        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        //Checks what slot is clicked, then sets sigColor to the color.
        if (event.getSlot() == 0)
            sigColor = "DARK_RED";
        if (event.getSlot() == 1)
            sigColor = "RED";
        if (event.getSlot() == 2)
            sigColor = "GOLD";
        if (event.getSlot() == 3)
            sigColor = "YELLOW";
        if (event.getSlot() == 4)
            sigColor = "GREEN";
        if (event.getSlot() == 5)
            sigColor = "DARK_GREEN";
        if (event.getSlot() == 6)
            sigColor = "AQUA";
        if (event.getSlot() == 7)
            sigColor = "DARK_AQUA";
        if (event.getSlot() == 9)
            sigColor = "BLUE";
        if (event.getSlot() == 10)
            sigColor = "DARK_BLUE";
        if (event.getSlot() == 11)
            sigColor = "DARK_PURPLE";
        if (event.getSlot() == 12)
            sigColor = "LIGHT_PURPLE";
        if (event.getSlot() == 13)
            sigColor = "WHITE";
        if (event.getSlot() == 14)
            sigColor = "GRAY";
        if (event.getSlot() == 15)
            sigColor = "DARK_GRAY";
        if (event.getSlot() == 16)
            sigColor = "BLACK";
        //Closes the color GUI after the player has picked one.
        player.closeInventory();
        //Makes the player send the /sigadd command.
        player.chat("/sigadd");
    }

    @EventHandler
    //Handles custom icon selection.
    public void onClickCustomIcon(InventoryClickEvent event) {
        if (!event.getClickedInventory().equals(customIcon))
            return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getClickedInventory().equals(InventoryType.PLAYER)) return;
        if (event == null) return;

        event.setCancelled(true);

        String customBlock = new String();
        Player player = (Player) event.getWhoClicked();
        //Checks what slot is clicked, then sets customBlock to the block.
        if (event.getSlot() == 0)
            customBlock = "DIAMOND_ORE";
        if (event.getSlot() == 1)
            customBlock = "GLASS";
        if (event.getSlot() == 2)
            customBlock = "MOSSY_COBBLESTONE";
        if (event.getSlot() == 3)
            customBlock = "DIAMOND_BLOCK";
        if (event.getSlot() == 4)
            customBlock = "GRASS";
        if (event.getSlot() == 5)
            customBlock = "LOG";
        if (event.getSlot() == 6)
            customBlock = "EMERALD_ORE";
        if (event.getSlot() == 7)
            customBlock = "SPONGE";
        if (event.getSlot() == 8)
            customBlock = "GOLD_BLOCK";
        //Adds the custom icon the the config.yml file.
        this.data.getConfig().createSection("signatures.customSig." + numberOfSigs + ".block");
        this.data.getConfig().set("signatures.customSig." + numberOfSigs + ".block", (customBlock));
        data.saveConfig();
        player.closeInventory();
        reloadConfig();
        makeSignatures();
    }

    //Takes the saved signatures from the config.yml file and adds them to the custom GUI.
    public void makeSignatures() {
        reloadConfig();
        String block = new String();
        ItemStack item = new ItemStack(Material.GLASS);
        ItemMeta meta = item.getItemMeta();
        //Loops through the config.yml file for every signature.
        for (int loops = 1; loops <= numberOfSigs;) {
            if (numberOfSigs != 0) {
                List<String> invLore = new ArrayList<String>();
                block = (getConfig().getString("signatures.customSig." + loops + ".block"));
                item.setType(Material.getMaterial(block));
                meta.setDisplayName(getConfig().getString("signatures.customSig." + loops + ".name"));
                invLore.add(getConfig().getString("signatures.customSig." + loops + ".lore"));
                meta.setLore(invLore);
                item.setItemMeta(meta);
                sig.setItem((loops - 1), item);
                loops++;
            }
            //This fixes a bug of there being an empty item with no lore at the end of the GUI.
            List<String> invLore = new ArrayList<String>();
            block = (getConfig().getString("signatures.customSig." + numberOfSigs + ".block"));
            item.setType(Material.getMaterial(block));
            meta.setDisplayName(getConfig().getString("signatures.customSig." + numberOfSigs + ".name"));
            invLore.add(getConfig().getString("signatures.customSig." + numberOfSigs + ".lore"));
            meta.setLore(invLore);
            item.setItemMeta(meta);
            sig.setItem((numberOfSigs - 1), item);
        }
    }

    //Makes the color GUI for signatures that are being saved.
    public void createInvColor() {
        ItemStack item = new ItemStack(Material.CONCRETE, 1, (byte)14 );
        ItemMeta meta = item.getItemMeta();
        List<String> invLore = new ArrayList<String>();
        //Makes the color GUI.
        meta.setDisplayName(ChatColor.DARK_RED + "Dark Red");
        invLore.add(ChatColor.DARK_RED + "Example Signature");
        meta.setLore(invLore);
        item.setItemMeta(meta);

        colors.setItem(0, item);
        item = new ItemStack(Material.WOOL, 1, (byte)14 );
        meta.setDisplayName(ChatColor.RED + "Red");
        meta.setLore(Collections.singletonList(ChatColor.RED + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(1, item);

        item.setType(Material.GOLD_BLOCK);
        meta.setDisplayName(ChatColor.GOLD + "Gold");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(2, item);

        item = new ItemStack(Material.WOOL, 1, (byte)4 );
        meta.setDisplayName(ChatColor.YELLOW + "Yellow");
        meta.setLore(Collections.singletonList(ChatColor.YELLOW + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(3, item);

        item = new ItemStack(Material.WOOL, 1, (byte)5 );
        meta.setDisplayName(ChatColor.GREEN + "Green");
        meta.setLore(Collections.singletonList(ChatColor.GREEN + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(4, item);

        item = new ItemStack(Material.WOOL, 1, (byte)13 );
        meta.setDisplayName(ChatColor.DARK_GREEN + "Dark Green");
        meta.setLore(Collections.singletonList(ChatColor.DARK_GREEN + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(5, item);

        item = new ItemStack(Material.WOOL, 1, (byte)3 );
        meta.setDisplayName(ChatColor.AQUA + "Aqua");
        meta.setLore(Collections.singletonList(ChatColor.AQUA + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(6, item);

        item = new ItemStack(Material.WOOL, 1, (byte)9 );
        meta.setDisplayName(ChatColor.DARK_AQUA + "Dark Aqua");
        meta.setLore(Collections.singletonList(ChatColor.DARK_AQUA + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(7, item);

        item = new ItemStack(Material.WOOL, 1, (byte)11 );
        meta.setDisplayName(ChatColor.BLUE + "Blue");
        meta.setLore(Collections.singletonList(ChatColor.BLUE + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(9, item);

        item = new ItemStack(Material.CONCRETE, 1, (byte)11 );
        meta.setDisplayName(ChatColor.DARK_BLUE + "Dark Blue");
        meta.setLore(Collections.singletonList(ChatColor.DARK_BLUE + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(10, item);

        item = new ItemStack(Material.WOOL, 1, (byte)10 );
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Dark Purple");
        meta.setLore(Collections.singletonList(ChatColor.DARK_PURPLE + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(11, item);

        item = new ItemStack(Material.WOOL, 1, (byte)6 );
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Light Purple");
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(12, item);

        item = new ItemStack(Material.WOOL, 1, (byte)0 );
        meta.setDisplayName(ChatColor.WHITE + "White");
        meta.setLore(Collections.singletonList(ChatColor.WHITE + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(13, item);

        item = new ItemStack(Material.WOOL, 1, (byte)8 );
        meta.setDisplayName(ChatColor.GRAY + "Gray");
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(14, item);

        item = new ItemStack(Material.WOOL, 1, (byte)7 );
        meta.setDisplayName(ChatColor.DARK_GRAY + "Dark Gray");
        meta.setLore(Collections.singletonList(ChatColor.DARK_GRAY + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(15, item);

        item = new ItemStack(Material.WOOL, 1, (byte)15 );
        meta.setDisplayName(ChatColor.BLACK + "Black");
        meta.setLore(Collections.singletonList(ChatColor.BLACK + "Example Signature"));
        item.setItemMeta(meta);
        colors.setItem(16, item);
    }

    //Makes the color GUI for custom signatures that are not being saved.
    public void createCustomColor() {
        ItemStack item = new ItemStack(Material.CONCRETE, 1, (byte)14 );
        ItemMeta meta = item.getItemMeta();
        List<String> invLore = new ArrayList<String>();
        //Makes the color GUI.
        meta.setDisplayName(ChatColor.DARK_RED + "Dark Red");
        invLore.add(ChatColor.DARK_RED + "Example Signature");
        meta.setLore(invLore);
        item.setItemMeta(meta);
        customColors.setItem(0, item);

        item = new ItemStack(Material.WOOL, 1, (byte)14 );
        meta.setDisplayName(ChatColor.RED + "Red");
        meta.setLore(Collections.singletonList(ChatColor.RED + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(1, item);

        item.setType(Material.GOLD_BLOCK);
        meta.setDisplayName(ChatColor.GOLD + "Gold");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(2, item);

        item = new ItemStack(Material.WOOL, 1, (byte)4 );
        meta.setDisplayName(ChatColor.YELLOW + "Yellow");
        meta.setLore(Collections.singletonList(ChatColor.YELLOW + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(3, item);

        item = new ItemStack(Material.WOOL, 1, (byte)5 );
        meta.setDisplayName(ChatColor.GREEN + "Green");
        meta.setLore(Collections.singletonList(ChatColor.GREEN + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(4, item);

        item = new ItemStack(Material.WOOL, 1, (byte)13 );
        meta.setDisplayName(ChatColor.DARK_GREEN + "Dark Green");
        meta.setLore(Collections.singletonList(ChatColor.DARK_GREEN + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(5, item);

        item = new ItemStack(Material.WOOL, 1, (byte)3 );
        meta.setDisplayName(ChatColor.AQUA + "Aqua");
        meta.setLore(Collections.singletonList(ChatColor.AQUA + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(6, item);

        item = new ItemStack(Material.WOOL, 1, (byte)9 );
        meta.setDisplayName(ChatColor.DARK_AQUA + "Dark Aqua");
        meta.setLore(Collections.singletonList(ChatColor.DARK_AQUA + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(7, item);

        item = new ItemStack(Material.WOOL, 1, (byte)11 );
        meta.setDisplayName(ChatColor.BLUE + "Blue");
        meta.setLore(Collections.singletonList(ChatColor.BLUE + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(9, item);

        item = new ItemStack(Material.CONCRETE, 1, (byte)11 );
        meta.setDisplayName(ChatColor.DARK_BLUE + "Dark Blue");
        meta.setLore(Collections.singletonList(ChatColor.DARK_BLUE + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(10, item);

        item = new ItemStack(Material.WOOL, 1, (byte)10 );
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Dark Purple");
        meta.setLore(Collections.singletonList(ChatColor.DARK_PURPLE + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(11, item);

        item = new ItemStack(Material.WOOL, 1, (byte)6 );
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Light Purple");
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(12, item);

        item = new ItemStack(Material.WOOL, 1, (byte)0 );
        meta.setDisplayName(ChatColor.WHITE + "White");
        meta.setLore(Collections.singletonList(ChatColor.WHITE + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(13, item);

        item = new ItemStack(Material.WOOL, 1, (byte)8 );
        meta.setDisplayName(ChatColor.GRAY + "Gray");
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(14, item);

        item = new ItemStack(Material.WOOL, 1, (byte)7 );
        meta.setDisplayName(ChatColor.DARK_GRAY + "Dark Gray");
        meta.setLore(Collections.singletonList(ChatColor.DARK_GRAY + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(15, item);

        item = new ItemStack(Material.WOOL, 1, (byte)15 );
        meta.setDisplayName(ChatColor.BLACK + "Black");
        meta.setLore(Collections.singletonList(ChatColor.BLACK + "Example Signature"));
        item.setItemMeta(meta);
        customColors.setItem(16, item);
    }

    //Makes the custom icon GUI for custom signatures.
    public void customIcon() {
        ItemStack item = new ItemStack(Material.DIAMOND_ORE, 1, (byte)14 );
        ItemMeta meta = item.getItemMeta();
        List<String> invLore = new ArrayList<String>();
        //Makes the custom icon GUI.
        invLore.add(ChatColor.AQUA + "Example Signature");
        meta.setLore(invLore);
        item.setItemMeta(meta);
        customIcon.setItem(0, item);

        item = new ItemStack(Material.GLASS);
        meta.setLore(Collections.singletonList(ChatColor.AQUA + "Click this to set it as the icon for your signature!"));
        item.setItemMeta(meta);
        customIcon.setItem(1, item);

        item = new ItemStack(Material.MOSSY_COBBLESTONE);
        meta.setLore(Collections.singletonList(ChatColor.AQUA + "Click this to set it as the icon for your signature!"));
        item.setItemMeta(meta);
        customIcon.setItem(2, item);

        item = new ItemStack(Material.DIAMOND_BLOCK);
        meta.setLore(Collections.singletonList(ChatColor.AQUA + "Click this to set it as the icon for your signature!"));
        item.setItemMeta(meta);
        customIcon.setItem(3, item);

        item = new ItemStack(Material.GRASS);
        meta.setLore(Collections.singletonList(ChatColor.AQUA + "Click this to set it as the icon for your signature!"));
        item.setItemMeta(meta);
        customIcon.setItem(4, item);

        item = new ItemStack(Material.LOG);
        meta.setLore(Collections.singletonList(ChatColor.AQUA + "Click this to set it as the icon for your signature!"));
        item.setItemMeta(meta);
        customIcon.setItem(5, item);

        item = new ItemStack(Material.EMERALD_ORE);
        meta.setLore(Collections.singletonList(ChatColor.AQUA + "Click this to set it as the icon for your signature!"));
        item.setItemMeta(meta);
        customIcon.setItem(6, item);

        item = new ItemStack(Material.SPONGE);
        meta.setLore(Collections.singletonList(ChatColor.AQUA + "Click this to set it as the icon for your signature!"));
        item.setItemMeta(meta);
        customIcon.setItem(7, item);

        item = new ItemStack(Material.GOLD_BLOCK);
        meta.setLore(Collections.singletonList(ChatColor.AQUA + "Click this to set it as the icon for your signature!"));
        item.setItemMeta(meta);
        customIcon.setItem(8, item);
    }
}