/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.jlh.pvptitles.Commands;

import es.jlh.pvptitles.Utils.Lang;
import es.jlh.pvptitles.plugin.PvpTitles;
import static es.jlh.pvptitles.plugin.PvpTitles.PLUGIN;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Julian
 */
public class ReloadCommand implements CommandExecutor {

    private final PvpTitles pvpTitles;
    
    public ReloadCommand(PvpTitles pvpTitles) {
        this.pvpTitles = pvpTitles;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(PLUGIN + Lang.COMMAND_ARGUMENTS.getText());
            return false;
        }

        pvpTitles.databaseHandler.loadConfig();
        sender.sendMessage(PLUGIN + Lang.PLUGIN_RELOAD.getText());
                
        return true;
    }
}
