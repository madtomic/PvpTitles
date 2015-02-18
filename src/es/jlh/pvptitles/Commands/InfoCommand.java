/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.jlh.pvptitles.Commands;

import es.jlh.pvptitles.Utils.Lang;
import es.jlh.pvptitles.plugin.PvpTitles;
import static es.jlh.pvptitles.plugin.PvpTitles.PLUGIN;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Julian
 */
public class InfoCommand implements CommandExecutor {

    private final PvpTitles pvpTitles;
    
    public InfoCommand(PvpTitles pvpTitles) {
        this.pvpTitles = pvpTitles;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(PLUGIN + Lang.COMMAND_ARGUMENTS.getText());
            return false;
        }
        
        sender.sendMessage("");
        
        sender.sendMessage(PLUGIN + ChatColor.YELLOW + "v" + pvpTitles.getDescription().getVersion());
        
        sender.sendMessage("  " + ChatColor.AQUA + "/pvprank " + 
                ChatColor.RESET + " [Para visualizar datos sobre tu fama]");
        
        sender.sendMessage("  " + ChatColor.AQUA + "/pvpranking " + 
                ChatColor.RESET + " [Ranking de los mejores jugadores PvP]");
        
        if (sender.hasPermission("pvp.reload")) {
            sender.sendMessage("  " + ChatColor.AQUA + "/pvpreload " + 
                    ChatColor.RESET + " [Recarga el config principal]");
        }
        
        sender.sendMessage("■ " + ChatColor.GOLD + "Modificado por Julito" + 
                ChatColor.RESET + " ■");
        
        return true;
    }
}
