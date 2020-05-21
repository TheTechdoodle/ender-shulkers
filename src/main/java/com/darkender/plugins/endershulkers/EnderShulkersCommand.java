package com.darkender.plugins.endershulkers;

import org.bukkit.ChatColor;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;

public class EnderShulkersCommand implements CommandExecutor, TabCompleter
{
    private EnderShulkers enderShulkers;
    
    public EnderShulkersCommand(EnderShulkers enderShulkers)
    {
        this.enderShulkers = enderShulkers;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "You must be a player!");
            return true;
        }
        
        Player player = (Player) sender;
        List<ItemStack> shulkerList = getShulkers(player);
        if(shulkerList.isEmpty())
        {
            sender.sendMessage(ChatColor.RED + "There aren't any shulker boxes in your enderchest!");
            return true;
        }
        
        if(args.length == 0)
        {
            enderShulkers.openShulker(player, shulkerList.get(0));
            return true;
        }
        else
        {
            for(ItemStack shulkerItem : shulkerList)
            {
                BlockStateMeta meta = (BlockStateMeta) shulkerItem.getItemMeta();
                if(meta.hasDisplayName())
                {
                    if(args[0].equals(ChatColor.stripColor(meta.getDisplayName()).replaceAll("\\s", "_")))
                    {
                        enderShulkers.openShulker(player, shulkerItem);
                        return true;
                    }
                }
                ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
                try
                {
                    if(args[0].equals(shulkerBox.getColor().name().toLowerCase()))
                    {
                        enderShulkers.openShulker(player, shulkerItem);
                        return true;
                    }
                }
                catch(Exception ignored) {}
            }
            
            try
            {
                int index = Integer.parseInt(args[0]) - 1;
                if(index < 0 || index >= shulkerList.size())
                {
                    sender.sendMessage(ChatColor.RED + "Invalid index! Must be between 1 and " + shulkerList.size());
                }
                else
                {
                    enderShulkers.openShulker(player, shulkerList.get(index));
                }
                return true;
            }
            catch(Exception e)
            {
                sender.sendMessage(ChatColor.RED + "Shulker not found!");
                return true;
            }
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            List<ItemStack> shulkers = getShulkers(player);
            List<String> names = new ArrayList<>();
            
            for(ItemStack shulker : shulkers)
            {
                BlockStateMeta meta = (BlockStateMeta) shulker.getItemMeta();
                if(meta.hasDisplayName())
                {
                    names.add(ChatColor.stripColor(meta.getDisplayName()).replaceAll("\\s", "_"));
                }
                ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
                try
                {
                    names.add(shulkerBox.getColor().name().toLowerCase());
                }
                catch(Exception ignored) {}
            }
            
            return names;
        }
        return null;
    }
    
    public List<ItemStack> getShulkers(Player player)
    {
        List<ItemStack> shulkers = new ArrayList<>();
        for(ItemStack item : player.getEnderChest())
        {
            if(item != null && item.hasItemMeta() && (item.getItemMeta() instanceof BlockStateMeta))
            {
                BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                if(meta.hasBlockState() && meta.getBlockState() instanceof ShulkerBox)
                {
                    shulkers.add(item);
                }
            }
        }
        return shulkers;
    }
}
