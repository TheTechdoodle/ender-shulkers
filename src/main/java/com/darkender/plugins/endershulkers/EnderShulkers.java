package com.darkender.plugins.endershulkers;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class EnderShulkers extends JavaPlugin implements Listener
{
    private HashMap<UUID, ItemStack> openShulkers;
    
    @Override
    public void onEnable()
    {
        openShulkers = new HashMap<>();
        
        EnderShulkersCommand enderShulkersCommand = new EnderShulkersCommand(this);
        getCommand("endershulker").setExecutor(enderShulkersCommand);
        getCommand("endershulker").setTabCompleter(enderShulkersCommand);
        
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    public void openShulker(Player player, ItemStack shulkerBox)
    {
        // Ensure no other inventories are open (default is crafting)
        if(player.getOpenInventory().getType() != InventoryType.CRAFTING)
        {
            player.closeInventory();
        }
    
        if(shulkerBox.getItemMeta() instanceof BlockStateMeta)
        {
            BlockStateMeta blockStateMeta = (BlockStateMeta) shulkerBox.getItemMeta();
            if(blockStateMeta.getBlockState() instanceof ShulkerBox)
            {
                // Load the inventory contents from the shulker
                ShulkerBox shulker = (ShulkerBox) blockStateMeta.getBlockState();
                String name = blockStateMeta.hasDisplayName() ? blockStateMeta.getDisplayName() : "Shulker Box";
                Inventory inv = Bukkit.createInventory(null, InventoryType.SHULKER_BOX, name);
                inv.setContents(shulker.getInventory().getContents());
                player.openInventory(inv);
                
                openShulkers.put(player.getUniqueId(), shulkerBox);
                player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if(openShulkers.containsKey(event.getPlayer().getUniqueId()))
        {
            // Save the shulker state
            ItemStack item = openShulkers.get(event.getPlayer().getUniqueId());
            BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
            ShulkerBox shulker = (ShulkerBox) blockStateMeta.getBlockState();
    
            shulker.getInventory().setContents(event.getInventory().getContents());
            blockStateMeta.setBlockState(shulker);
            item.setItemMeta(blockStateMeta);
            
            openShulkers.remove(event.getPlayer().getUniqueId());
            
            // Because getPlayer()... doesn't return a Player...
            if(event.getPlayer() instanceof Player)
            {
                ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }
}
