package es.jlh.pvptitles.Events;

import es.jlh.pvptitles.Utils.Lang;
import es.jlh.pvptitles.Utils.PlayerKills;
import es.jlh.pvptitles.plugin.DatabaseHandler;
import es.jlh.pvptitles.Utils.Ranks;
import es.jlh.pvptitles.plugin.PvpTitles;
import static es.jlh.pvptitles.plugin.PvpTitles.PLUGIN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HandlePlayerFame implements Listener {
    
    public static final int TICKS = 20;
    
    private final DatabaseHandler databaseHandler;
    private final Ranks ranks;
    
    public static final Map<String, Integer> racha = new HashMap();
    
    // Jugador mas un arraylist con los nombres de sus victimas y sus respectivas bajas
    private final Map<String, ArrayList<PlayerKills>> antiFarm = new HashMap();
    // Nombre de jugador eliminado y la tarea para resetear el valor
    private final Map<String, Integer> quitaKills = new HashMap();
    // Jugadores que no conseguiran fama por abuso de kills
    private final ArrayList<String> vetados = new ArrayList();
    
    private final PvpTitles pvpTitles;

    /**
     * Contructor de la clase
     * @param pt Plugin
     */
    public HandlePlayerFame(PvpTitles pt) {
        this.databaseHandler = pt.databaseHandler;
        this.ranks = new Ranks(this.databaseHandler, pt);
        this.pvpTitles = pt;
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
        this.racha.put(player.getName(), 0);
    }

    /**
     * Metodo para ajustar los puntos pvp del jugador cuanto hace una kill
     * @param death Evento PlayerDeathEvent
     */
    @EventHandler
    public void onKill(PlayerDeathEvent death) {
        // Compruebo si el mundo esta en la lista de los vetados
        if (this.databaseHandler.getMundos().contains(death.getEntity().getWorld().getName().toLowerCase())) {
            return;            
        }
        
        if(death.getEntity().getKiller() instanceof Player) {
            int kills = 0;
            
            //death.getEntity().setHealth(10.0);
            
            String killed = death.getEntity().getName();
            Player player = death.getEntity().getKiller();            
            
            // Compruebo primero si el jugador esta vetado o se mato a si mismo
            if (vetados.contains(player.getName()) || player.getName().equalsIgnoreCase(killed)) {
                return;
            }
            
            // Modulo anti farm
            antiFarm(player, killed);
            
            // Lo compruebo de nuevo por si le acaban de vetar
            if (vetados.contains(player.getName())) {
               return;
            }
            
            // Si ya esta en el mapa guardo sus bajas
            if(HandlePlayerFame.racha.containsKey(player.getName())) {
                kills = HandlePlayerFame.racha.get(player.getName());
            }
            
            // Final de la racha de bajas
            if(HandlePlayerFame.racha.containsKey(killed)) {
                HandlePlayerFame.racha.put(killed, 0);
            }

            // Aniado el nuevo valor de kills
            int fame = this.databaseHandler.loadPlayerFame(player.getName());         

            this.calculateFame(killed, player, fame, kills);                        
            kills++;
            HandlePlayerFame.racha.put(player.getName(), kills);
        }                
    }

    /**
     * Metodo para evitar que farmen puntos para los titulos pvp
     * @param player String con el nombre del asesino
     * @param killed String con el nombre del asesinado
     */
    private void antiFarm(Player player, String killed) {
        // AntiFarm
        if (antiFarm.containsKey(player.getName())) {
            // En caso de que el jugador ya haya matado entra           
            for (PlayerKills pk : antiFarm.get(player.getName())) {
                // Si el jugador ya habia matado antes a ese jugador se registra
                if (pk.getNombre().equals(killed)) {                    
                    // Cancelo la tarea de limpieza de bajas si le vuelve a matar
                    Bukkit.getServer().getScheduler().cancelTask(quitaKills.get(player.getName() + pk.getNombre()));
                    quitaKills.remove(player.getName() + pk.getNombre());
                    
                    // Le sumamos uno a sus bajas                            
                    pk.setKills(pk.getKills()+1);
                    
                    // En caso de que haya superado la muertes limite se le veta
                    if (pk.getKills() > this.databaseHandler.getKills()) { 
                        quitaKills.remove(player.getName()+killed);
                        antiFarm.get(player.getName()).remove(pk);
                        vetados.add(player.getName());
                        
                        player.sendMessage(PLUGIN + Lang.VETO_STARTED.getText().replace("%TIME%", 
                                String.valueOf(this.databaseHandler.getTimeV()/60)));
                        
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pvpTitles, () -> {
                            vetados.remove(player.getName());                            
                            player.sendMessage(PLUGIN + Lang.VETO_FINISHED.getText());
                        }, this.databaseHandler.getTimeV() * TICKS);
                    }
                    else {
                        aniadeQuitaKills(player.getName(), killed, pk);
                    }
                    
                    return;
                }
            }            
            // En caso de que sea uno nuevo
            PlayerKills pk = new PlayerKills(killed, 1);
            antiFarm.get(player.getName()).add(pk);

            aniadeQuitaKills(player.getName(), killed, pk);            
        }
        else {            
            // En caso de que no contenga al jugador todavia
            ArrayList<PlayerKills> pks = new ArrayList();
            PlayerKills pk = new PlayerKills(killed, 1);
            pks.add(pk);
            antiFarm.put(player.getName(), pks);
            
            aniadeQuitaKills(player.getName(), killed, pk);
        }
    }
    
    /**
     * Metodo para establecer el tiempo de limpieza de kills
     * @param asesino String con el nombre del asesino
     * @param killed String con el nombre del asesinado
     * @param pk Objeto de tipo PlayerKills
     */
    public void aniadeQuitaKills(String asesino, String killed, PlayerKills pk) {
        quitaKills.put(asesino+killed, Bukkit.getServer().getScheduler().
            scheduleSyncDelayedTask(pvpTitles, () -> {
                antiFarm.get(asesino).remove(pk);
                quitaKills.remove(asesino+killed);
            }, 30 * TICKS)
        );
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