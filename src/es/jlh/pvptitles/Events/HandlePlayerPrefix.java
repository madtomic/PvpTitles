package es.jlh.pvptitles.Events;

import es.jlh.pvptitles.plugin.DatabaseHandler;
import es.jlh.pvptitles.Utils.Ranks;
import es.jlh.pvptitles.plugin.PvpTitles;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class HandlePlayerPrefix implements Listener {
    private final DatabaseHandler databaseHandler;
    private final Ranks ranks;

    public HandlePlayerPrefix(PvpTitles pt) {
        this.databaseHandler = pt.databaseHandler;
        this.ranks = new Ranks(this.databaseHandler, pt);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String rank = null;

        if (event.getPlayer().hasPermission("pvptitles.chat")) {            
            return;
        }
        
        int fame = this.databaseHandler.loadPlayerFame(event.getPlayer().getName());
        rank = this.ranks.GetRank(fame);
        
        if(rank != null && !rank.equals("") && !rank.equals("None")) {				
            String a = String.format(ChatColor.WHITE + "[" + 
                    this.databaseHandler.prefixColor + rank + ChatColor.WHITE + "] ");
            String format = event.getFormat();
            event.setFormat(a + format);
        }
    }
}