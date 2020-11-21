package network.kngsgaming.minecraft;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantEvents implements Listener {
    private VanillaEnchants plugin;
    private Map<String, Object> limits;
    private boolean debug;


    public EnchantEvents( VanillaEnchants plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        ConfigurationSection section = plugin.config.getConfigurationSection("limits");
        try {
            limits = section.getValues(false);
        } catch(Error e) {
            e.printStackTrace();
            plugin.printToConsole(ChatColor.RED + "ERROR: could not get limits from config");
        }
        try {
            debug = plugin.config.getString("debug").toLowerCase().equals("true");
        } catch (Error e){
            debug = false;
            plugin.printToConsole("debug: not 'true', set to false");
        }

    }

    @EventHandler
    public void onPrepareAnvilEvent(PrepareAnvilEvent event) {
        AnvilInventory inventory = (AnvilInventory) event.getInventory();
        Map<String, Integer> limits = new HashMap<String, Integer>();
        inventory.setMaximumRepairCost(Integer.MAX_VALUE);

        ItemStack leftItem = inventory.getItem(0);
        ItemStack rightItem = inventory.getItem(1);
        ItemStack resultItem;
        //check if both item slots are filled
        if (leftItem != null && rightItem != null) {
            //check to see if there is a book
            boolean leftIsBook = leftItem.getItemMeta() instanceof EnchantmentStorageMeta;
            boolean rightIsBook = rightItem.getItemMeta() instanceof EnchantmentStorageMeta;

            //if the items are the same type OR one of the items is a book
            if (leftItem.getType() == rightItem.getType() || leftIsBook || rightIsBook) {
                //use the enchants on the left item as a base
                Map<Enchantment, Integer> resultingEnchantments;
                Map<Enchantment, Integer> addedEnchantments;

                //get enchantment map for left item
                if (leftIsBook) {
                    resultingEnchantments = new HashMap<Enchantment, Integer>(((EnchantmentStorageMeta)leftItem.getItemMeta()).getStoredEnchants());
                } else {
                    resultingEnchantments = new HashMap(leftItem.getItemMeta().getEnchants());
                }

                //get enchantment map for right item
                if (rightIsBook) {
                    addedEnchantments = new HashMap<Enchantment, Integer>(((EnchantmentStorageMeta)rightItem.getItemMeta()).getStoredEnchants());
                } else {
                    addedEnchantments = rightItem.getItemMeta().getEnchants();
                }

                //iterate over all enchants on the right item
                for (Map.Entry<Enchantment, Integer> entry : addedEnchantments.entrySet()) {
                    Enchantment rightEnchantment = entry.getKey();
                    int rightEnchantmentLevel = entry.getValue();
                    //check if the left item does not have this enchantment yet
                    if (!resultingEnchantments.containsKey(rightEnchantment)) {
                        //add the enchantment!
                        resultingEnchantments.put(rightEnchantment, rightEnchantmentLevel);
                    } else {  //both items have this enchantment
                        //get the enchantment level for this enchant on the left item
                        int leftEnchantmentLevel = resultingEnchantments.get(rightEnchantment);
                        //check if right item has a higher level for this enchantment
                        if (leftEnchantmentLevel < rightEnchantmentLevel) {
                            //update the result to the level of the enchantment on the right
                            resultingEnchantments.put(rightEnchantment, rightEnchantmentLevel);
                        }
                        //both items have the same level
                        else if (leftEnchantmentLevel == rightEnchantmentLevel) {
                            int newLevel = rightEnchantmentLevel;
                            if ( isValidEnchantLevel(rightEnchantment, newLevel+1)) {
                                newLevel++;
                            }
                            resultingEnchantments.put(rightEnchantment, newLevel);
                        }
                    }
                }
                
                //check for repairability
                boolean leftDamageable = leftItem.getItemMeta() instanceof Damageable;
                boolean rightDamageable = rightItem.getItemMeta() instanceof Damageable;
                if ( leftDamageable && rightDamageable ) {
                    int damage0 = ((Damageable) leftItem.getItemMeta()).getDamage();
                    int damage1 = ((Damageable) rightItem.getItemMeta()).getDamage();
                    int maxdamage = (leftItem.getType()).getMaxDurability();
                    //damage is calculated as "hits taken", not "life"
                    int durability = (maxdamage - damage0) + (maxdamage - damage1) + (int) Math.floor(0.12 * maxdamage);
                    // create undamaged item state
                    int damage = 0;
                    // if not enough durability to create undamaged item, set damage accordingly
                    if (durability < maxdamage) { 
                        damage = maxdamage - durability; 
                    }
                    resultItem = leftItem.clone();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    ((Damageable) resultMeta).setDamage(damage);
                    resultItem.setItemMeta(resultMeta);
                }
                //nothing to repair
                else {
                    resultItem = leftItem.clone();
                }

                //debug for ops
                if (debug && event.getView().getPlayer().isOp()) {
                    plugin.printToConsole(ChatColor.AQUA + "DEBUG INFO FOR " + event.getView().getPlayer().getName());
                    for (Map.Entry<Enchantment, Integer> entry : resultingEnchantments.entrySet()) {
                        plugin.printToConsole("Enchantment found: " + ChatColor.GREEN + entry.getKey().getKey().getKey());
                    }
                }

                if (leftIsBook) {
                    //original enchantments
                    EnchantmentStorageMeta resultItemMeta = (EnchantmentStorageMeta)resultItem.getItemMeta();
                    Map<Enchantment, Integer> originalEnchantments = resultItemMeta.getStoredEnchants();
                    //clear enchantments from the temporary result item
                    for (Map.Entry<Enchantment, Integer> entry : originalEnchantments.entrySet()) {
                        resultItemMeta.removeStoredEnchant(entry.getKey());
                    }
                    //set new enchants
                    for (Map.Entry<Enchantment, Integer> entry : resultingEnchantments.entrySet()) {
                        resultItemMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                    }
                    resultItem.setItemMeta(resultItemMeta);

                } else {
                    //clear enchantments from the temporary result item
                    for (Map.Entry<Enchantment, Integer> entry : resultItem.getItemMeta().getEnchants().entrySet()) {
                        resultItem.removeEnchantment(entry.getKey());
                    }

                    //Add the enchantments based on the config options!
                    resultItem.addUnsafeEnchantments(resultingEnchantments);

                }

                //Cost is almost always 1 if you are putting things on a book, so let's change that!
                if (leftIsBook) {
                    //repair cost will be the sum total of all enchant levels on the resulting book!
                    int repairCost = 0;
                    for (Map.Entry<Enchantment, Integer> entry : resultingEnchantments.entrySet()) {
                        repairCost += entry.getValue();
                    }
                    inventory.setRepairCost(repairCost);
                }

                event.setResult(resultItem);

                int finalRepairCost = inventory.getRepairCost();
                if (finalRepairCost > 40) {
                    event.getView().getPlayer().sendMessage(plugin.chatPrepend() + ChatColor.RED + "This repair costs: " + ChatColor.GREEN + finalRepairCost + ChatColor.RED + " levels.");
                }
                inventory.setRepairCost(finalRepairCost);
                event.getView().setProperty(InventoryView.Property.REPAIR_COST, finalRepairCost);

            }
        }

    }

    //Detect a player has repaired/combined an item!
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        // check whether the event has been cancelled by another plugin
        if(!event.isCancelled()){
            HumanEntity humanEntity = event.getWhoClicked();

            if(humanEntity instanceof Player){
                Player player = (Player)humanEntity;
                // Check if this event fired inside an anvil.
                if(event.getInventory() instanceof AnvilInventory){
                    final AnvilInventory anvilInventory = (AnvilInventory) event.getInventory();
                    InventoryView view = event.getView();
                    int rawSlot = event.getRawSlot();

                    // check if we are in the upper inventory of the anvil
                    if(rawSlot == view.convertSlot(rawSlot)){
                        // check if we are talking about the result slot
                        if(rawSlot == 2){
                            // get all 3 items in the anvil
                            ItemStack[] items = anvilInventory.getContents();

                            // Make sure there are items in the first two anvil slots
                            if(items[0] != null && items[1] != null) {
                                // if the player clicked an empty result slot, the material will be AIR, so ignore that!
                                // Also ignore if the player clicked the items in the first two slots!
                                if (event.getCurrentItem().getType() != Material.AIR && event.getCurrentItem() != items[0] && event.getCurrentItem() != items[1]) {
                                    // We now know the player has attempted to combine two items!
                                    // Now we make sure that the player has the levels required!
                                    if (player.getLevel() >= anvilInventory.getRepairCost()) {
                                        // Store these values before doing anything with the anvilInventory....
                                        int repairCost = anvilInventory.getRepairCost();
                                        int playerLevel = player.getLevel();
                                        int resultantLevel = playerLevel - repairCost;

                                        // clone the result item
                                        ItemStack itemToGive = event.getCurrentItem().clone();

                                        // let's make SURE that the item given is only 1!
                                        if (itemToGive.getAmount() > 1) {
                                            itemToGive.setAmount(1);
                                        }

                                        // If the first item is a stack, we should give it back (-1)
                                        if (items[0].getAmount() > 1) {
                                            ItemStack returnedStack = items[0].clone();
                                            returnedStack.setAmount(returnedStack.getAmount() - 1);
                                            if (player.getInventory().addItem(returnedStack).size() != 0) {
                                                player.getWorld().dropItem(player.getLocation(), returnedStack);
                                            }
                                        }

                                        // delete the 3 items in the anvil!
                                        anvilInventory.remove(anvilInventory.getItem(0));
                                        anvilInventory.remove(anvilInventory.getItem(1));
                                        anvilInventory.remove(anvilInventory.getItem(2));

                                        // give the player the clone of the result! (drop it on them if their inventory is full)
                                        if (player.getInventory().addItem(itemToGive).size() != 0) {
                                            player.getWorld().dropItem(player.getLocation(), itemToGive);
                                        }

                                        // Play the anvil sound on the player.
                                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);

                                        //let's set players exp levels to what they should be after this repair
                                        player.giveExpLevels(resultantLevel - playerLevel);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //Check to see if this enchantment and level are within the config defined limits
    private boolean isValidEnchantLevel(Enchantment enchantment, int level) {
        //assume no limit!
        int limit = Integer.MAX_VALUE;
        String enchanmentName = enchantment.getKey().getKey();
        if (limits.containsKey(enchanmentName)) {
            limit  = 0;
            try {
                //try to cast the object to an Integer
                limit = (Integer) limits.get(enchanmentName);
            } catch(Exception e) {
                plugin.printToConsole(ChatColor.RED + "ERROR: could not get limit for: " + enchanmentName);
                plugin.printToConsole(ChatColor.BLUE + "Defaulting to limit 0 for enchantment: " + enchanmentName);
            }
        }
        return level <= limit;
    }

}
