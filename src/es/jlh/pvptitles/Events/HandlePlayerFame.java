package es.jlh.pvptitles.Events;

import es.jlh.pvptitles.Utils.Lang;
import es.jlh.pvptitles.plugin.DatabaseHandler;
import es.jlh.pvptitles.Utils.Ranks;
import es.jlh.pvptitles.plugin.PvpTitles;
import static es.jlh.pvptitles.plugin.PvpTitles.PLUGIN;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HandlePlayerFame implements Listener {
    private final DatabaseHandler databaseHandler;
    private final Ranks ranks;
    private final Map<String, Integer> map = new HashMap();

    /**
     * 
     * @param pt 
     */
    public HandlePlayerFame(PvpTitles pt) {
        this.databaseHandler = pt.databaseHandler;
        this.ranks = new Ranks(this.databaseHandler, pt);
    }

    /**
     * Metodo para crear el fichero del jugador en caso de que no exista
     * @param event Evento PlayerLoginEvent
     */
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        try {
            this.databaseHandler.firstRunPlayer(player.getName());
        }
        catch (Exception e) {}
    }

    /**
     * Metodo para reiniciar los puntos del jugador cuando deje el server
     * @param event Evento PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.map.put(player.getName(), 0);
    }

    /**
     * Metodo para ajustar los puntos pvp del jugador cuanto hace una kill
     * @param death Evento PlayerDeathEvent
     */
    @EventHandler
    public void onKill(PlayerDeathEvent death) {			
        int kills = 0 ;

        if(death.getEntity().getKiller() instanceof Player) {		
            String killed = death.getEntity().getName();
            Player player = death.getEntity().getKiller();

            // Si ya esta en el mapa guardo sus bajas
            if(this.map.containsKey(player.getName())) {
                kills = this.map.get(player.getName());
            }
            
            // Racha de bajas
            if(this.map.containsKey(killed)) {
                this.map.put(killed, 0);
            }

            int fame = this.databaseHandler.loadPlayerFame(player.getName());            

            if(!player.getName().equalsIgnoreCase(killed)) {
                this.calculateFame(killed, player, fame, kills);
            }

            kills++;
            this.map.put(player.getName(), kills);
        }                
    }

    /**
     * Metodo para calcular los puntos que ha ganado
     * @param killed Jugador eliminado
     * @param player Jugador superviviente
     * @param fame Puntos pvp
     * @param kills Racha de bajas
     */
    private void calculateFame(String killed, Player player, int fame, int kills) {
        int a = fame;
        String tag = this.databaseHandler.getTag();
        double mod = Math.abs(this.databaseHandler.getMod());
        
        int fameRec = (int) Math.ceil((kills*mod) + 1);
        
        player.sendMessage(PLUGIN + Lang.PLAYER_KILLED.getText()
                .replace("%killed%", killed)
                .replace("%fameRec%", Integer.toString(fameRec))
                .replace("%tag%", tag));

        fame += fameRec;
        
        this.databaseHandler.savePlayerFame(player.getName(), fame);        

        String currentRank = this.ranks.GetRank(a);
        String newRank = this.ranks.GetRank(fame);

        if(!currentRank.equalsIgnoreCase(newRank)) {
            player.sendMessage(PLUGIN + Lang.PLAYER_NEW_RANK.getText()
                    .replace("%newRank%", newRank));
        }
    }
}