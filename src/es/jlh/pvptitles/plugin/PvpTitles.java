package es.jlh.pvptitles.plugin;

import es.jlh.pvptitles.Commands.InfoCommand;
import java.util.logging.Logger;
import es.jlh.pvptitles.Events.HandlePlayerPrefix;
import es.jlh.pvptitles.Commands.LadderCommand;
import es.jlh.pvptitles.Commands.RankCommand;
import es.jlh.pvptitles.Commands.ReloadCommand;
import es.jlh.pvptitles.Events.HandlePlayerFame;
import es.jlh.pvptitles.Utils.Lang;
import es.jlh.pvptitles.Utils.Updater;
import es.jlh.pvptitles.Utils.Updater.UpdateResult;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class PvpTitles extends JavaPlugin {
    public static final String PLUGIN = ChatColor.WHITE + "["+ChatColor.GOLD + "PvPTitles" + ChatColor.WHITE + "] ";
    
    public Logger log;    
    public DatabaseHandler databaseHandler;
    
    @Override
    public void onEnable() {			
        log = getLogger();               
                        
        this.databaseHandler = DatabaseHandler.getInstance();
        this.databaseHandler.setup(this);
        
        // Registro los eventos
        getServer().getPluginManager().registerEvents(new HandlePlayerPrefix(this), this);
        getServer().getPluginManager().registerEvents(new HandlePlayerFame(this), this);

        // Registro los comandos
        getCommand("pvpRank").setExecutor(new RankCommand(this));
        getCommand("pvpLadder").setExecutor(new LadderCommand(this));
        getCommand("pvpReload").setExecutor(new ReloadCommand(this));
        getCommand("pvpTitles").setExecutor(new InfoCommand(this));       
        
        // Cargo los locales
        try {            
            Lang.load();
        } 
        catch (Exception ex) {
            this.getServer().getConsoleSender().sendMessage(PLUGIN + 
                        ChatColor.RED + ex.getMessage());
        }

        if (this.databaseHandler.update()) {                
            Updater updater = new Updater(this, 89518, this.getFile(), Updater.UpdateType.DEFAULT, this.databaseHandler.alert());
            if (updater.getResult() == UpdateResult.SUCCESS) {
                this.getServer().getConsoleSender().sendMessage(PLUGIN + 
                        ChatColor.AQUA + "Plugin updated. Do reload to load it");
            }
        }
        else {
            Updater updater = new Updater(this, 89518, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, this.databaseHandler.alert());
            if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
                this.getServer().getConsoleSender().sendMessage(PLUGIN + 
                        ChatColor.YELLOW + "A new update has been found: " + 
                        ChatColor.GREEN + updater.getLatestName());
                this.getServer().getConsoleSender().sendMessage(PLUGIN + 
                        ChatColor.YELLOW + "http://dev.bukkit.org/bukkit-plugins/pvptitles");
            }
        }
        
        log.info(Lang.PLUGIN_ENABLED.getText());                
    }
	 
    @Override
    public void onDisable() { 
        log.info(Lang.PLUGIN_DISABLED.getText());
    }
}
