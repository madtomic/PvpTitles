package es.jlh.pvptitles.Commands;

import es.jlh.pvptitles.Utils.Lang;
import es.jlh.pvptitles.Utils.PlayerFame;
import es.jlh.pvptitles.plugin.PvpTitles;
import static es.jlh.pvptitles.plugin.PvpTitles.PLUGIN;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LadderCommand implements CommandExecutor {    
    private final PvpTitles pvpTitles;
    
    // Todos los jugadores del servidor
    private final ArrayList<PlayerFame> rankedPlayers = new ArrayList();
            
    public LadderCommand(PvpTitles pvpTitles) {
        this.pvpTitles = pvpTitles;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(PLUGIN + Lang.COMMAND_ARGUMENTS.getText());
            return false;
        }                
        
        this.setTopPlayers();
        
        int ranking = this.pvpTitles.databaseHandler.getTop();
        
        sender.sendMessage("");
        sender.sendMessage(PLUGIN);
        sender.sendMessage(ChatColor.YELLOW + "  --------");
        sender.sendMessage(ChatColor.YELLOW + "    Top " + ranking + " ");
        sender.sendMessage(ChatColor.YELLOW + "  --------");
        
        Collections.sort(rankedPlayers);

        for (int i = 0; i < rankedPlayers.size() && i < ranking; i++) {
            sender.sendMessage("  " + (i+1) + ". " + rankedPlayers.get(i).toString());            
        }
        
        this.rankedPlayers.clear();
        
        return true;
    }

    /**
     * Metodo para recibir todos los jugadores con sus puntos
     */
    private void setTopPlayers() {
        File file = new File((new StringBuilder()).append(
                this.pvpTitles.getDataFolder()).append(
                        File.separator).append(
                                "players").toString());

        if (file.exists()) {
            File[] allFiles = file.listFiles();

            for (File item : allFiles) {
                File ladderFile = new File((new StringBuilder()).append(
                        this.pvpTitles.getDataFolder()).append(
                                File.separator).append(
                                        "players").append(
                                                File.separator).append(
                                                        item.getName()).toString());

                FileConfiguration config = YamlConfiguration.loadConfiguration(ladderFile);

                int fame = config.getInt("Fame");

                this.rankedPlayers.add(new PlayerFame(item.getName().replaceAll(".yml", ""), fame));
            }
        }
    }
}