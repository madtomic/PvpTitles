package es.jlh.pvptitles.Utils;

import es.jlh.pvptitles.plugin.DatabaseHandler;
import es.jlh.pvptitles.plugin.PvpTitles;
import java.util.Map;

public class Ranks {
    private int nextRankFame;
    private String nextRankTitle;
    
    private final DatabaseHandler databaseHandler;
    
    private final Map<Integer, String> rankList;
    private final Map<Integer, Integer> reqFame;
	
    public Ranks(DatabaseHandler databaseHandler, PvpTitles pvpTitles) {
        this.databaseHandler = databaseHandler;
        this.rankList = this.databaseHandler.rankList();
        this.reqFame = this.databaseHandler.reqFame();
    }

    /**
     * Metodo para recibir el rango segun los puntos que tenga el jugador     
     * @param fame Puntos pvp
     * @return String con el nombre del titulo
     */
    public String GetRank(int fame) {
        String rank = "";

        /*
          Caso puntual para comprobar si los puntos son mayores que todos 
          los de la lista
        */            
        if(fame >= this.reqFame.get(this.reqFame.values().size()-1)) {            
            this.nextRankFame = 999999;
            return this.rankList.get(this.reqFame.values().size()-1);            
        }
        
        for (int i = 0; i < this.reqFame.size(); i++) {
            // Voy comprobando si esta entre los puntos que va obteniendo
            if(fame >= this.reqFame.get(i) && fame < this.reqFame.get(i+1)) {               
                this.nextRankFame = this.reqFame.get(i+1) - fame;
                this.nextRankTitle = this.rankList.get(i+1);
                return this.rankList.get(i);
            }
        }
        
        return rank;
    }

    /**
     * Metodo para recibir los puntos restantes para conseguir el nuevo titulo
     * @return Entero con los puntos
     */
    public int FameToRankUp() {
        return this.nextRankFame;
    }

    /**
     * Metodo para recibir el nuevo titulo que va a conseguir el jugador
     * @return String con el titulo
     */
    public String nextRankTitle() {
        return this.nextRankTitle;
    }
}
