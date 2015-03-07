package es.jlh.pvptitles.Commands;

import es.jlh.pvptitles.Events.HandlePlayerFame;
import static es.jlh.pvptitles.Events.HandlePlayerFame.racha;
import es.jlh.pvptitles.Utils.Lang;
import es.jlh.pvptitles.plugin.DatabaseHandler;
import es.jlh.pvptitles.Utils.Ranks;
import es.jlh.pvptitles.plugin.PvpTitles;
import static es.jlh.pvptitles.plugin.PvpTitles.PLUGIN;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {
    private final DatabaseHandler databaseHandler;
    private final Ranks ranks;

    public RankCommand(PvpTitles pt) {
        this.databaseHandler = pt.databaseHandler;
        this.ranks = new Ranks(this.databaseHandler, pt);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PLUGIN + Lang.COMMAND_FORBIDDEN.getText());
            return true;
        }
        
        Player player = (Player) sender;        
        
        if (args.length > 0) {
            player.sendMessage(PLUGIN + Lang.COMMAND_ARGUMENTS.getText());
            return false;
        }
        
        this.HandleRankCmd(player);
        return true;
    }
    
    /**
     * Metodo para enviar los datos del rango de un jugador
     * @param player Jugador que consulta los datos
     */
    private void HandleRankCmd(Player player) {        
        int fame = this.databaseHandler.loadPlayerFame(player.getName());
        int racha = (HandlePlayerFame.racha.containsKey(player.getName())) ? HandlePlayerFame.racha.get(player.getName()) : 0;
        String rank = this.ranks.GetRank(fame);        
        int rankup = this.ranks.FameToRankUp();
        String nextRank = this.ranks.nextRankTitle();
        String tag = this.databaseHandler.getTag();
        
        player.sendMessage("");
        player.sendMessage(PLUGIN);
        player.sendMessage("  - " + ChatColor.AQUA + "Titulo: " + ChatColor.RESET + rank);
        player.sendMessage("  - " + ChatColor.AQUA + tag + ": " + ChatColor.RESET + fame);
        player.sendMessage("  - " + ChatColor.AQUA + "Racha: " + ChatColor.RESET + racha);
        
        if(rankup < 999999) {
            player.sendMessage("  - " + Lang.RANK_INFO.getText().replace("%rankup%", String.valueOf(rankup))
                    .replace("%tag%", tag).replace("%nextRank%",nextRank));
        }
    }
}