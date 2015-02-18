package es.jlh.pvptitles.plugin;

import static es.jlh.pvptitles.plugin.PvpTitles.PLUGIN;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class DatabaseHandler {
    // Variable que almacena el plugin
    private PvpTitles pvpTitles;
    // Configuracion del config principal
    private FileConfiguration config;
    
    // Objetos para guardar los Titulos con sus respectivos puntos    
    private final Map<Integer,String> rankList = new HashMap();
    private final Map<Integer, Integer> reqFame = new HashMap();
    
    /* Resto de valores a configurar */
    
    // Color del titulo en el chat
    public ChatColor prefixColor;
    // Nombre de los puntos
    private String tag;
    // Cantidad de jugadores que aparecen en el ranking
    private int top;
    // Modificador de los puntos ganados en una racha de bajas
    private double mod;
    // Variable para guardar si se va a actualizar o no
    private boolean update;
    // Variable para guardar si se va a avisar de las actualizaciones o no
    private boolean alert;

    // Instancia de la clase
    public static DatabaseHandler instance = new DatabaseHandler();
    
    /**
     * Contructor de la clase
     */
    private DatabaseHandler() {}

    /**
     * Instancia de la clase
     * <p>De esta forma evito que se creen diferentes objetos de la clase</p>
     * @return Instancia de la clase
     */
    public static DatabaseHandler getInstance() {
        return instance;    
    }
    
    /**
     * Metodo para cargar el config principal
     * @param plugin Objeto de la clase principal
     */
    public void setup(PvpTitles plugin) {
        this.pvpTitles = plugin;
        this.loadConfig();
        this.cargaDatos();
    }

    /**
     * Metodo para devolver una lista de titulos
     * @return Objeto que almacena la lista de titulos
     */ 
    public Map<Integer, String> rankList() {
        return this.rankList;
    }

    /**
     * Metodo para devolver una lista de puntos equivalentes a cada titulo
     * @return Objeto que almacena la lista de puntos de fama
     */ 
    public Map<Integer, Integer> reqFame() {
        return this.reqFame;
    }

    /**
     * Metodo para recibir el nombre de los puntos
     * @return String con el nombre para los puntos pvp
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * Metodo para recibir el numero de jugadores que se mostrara en el ranking
     * @return Entero con el numero de jugadores
     */
    public int getTop() {
        return this.top;
    }

    /**
     * Metodo para recibir el modificador de los puntos que obtienen con las rachas
     * @return Valor decimal
     */
    public double getMod() {
        return this.mod;
    }
    
    /**
     * Metodo para recibir si se desea actualizar automaticamente el plugin
     * @return 
     */
    public boolean update() {
        return this.update;
    }

    /**
     * Metodo para recibir si se mostraran nuevas actualizaciones del plugin
     * @return 
     */
    public boolean alert() {
        return this.alert;
    }
    
    // ############################################################## \\
	
    /**
     * Metodo para guardar la fama obtenida por un jugador
     * <p>Guarda los puntos en un fichero con el nombre del jugador</p>
     * @param playername Nombre del jugador
     * @param fame Puntos pvp
     */
    public void savePlayerFame(String playername, int fame) {
        File file = new File((new StringBuilder()).append(
                this.pvpTitles.getDataFolder()).append( // Ruta
                        File.separator).append( // Separador
                                "players").append( // Players
                                        File.separator).append( // Separador
                                                playername).append( // nJugador
                                                        ".yml").toString()); // .yml
        FileConfiguration confPlayer;
        
        confPlayer = YamlConfiguration.loadConfiguration(file);
        confPlayer.set("Fame", fame);
        
        try {
            confPlayer.save(file);
        }
        catch(IOException e) {}
    }
	
    /**
     * Metodo para cargar los puntos pvp de un jugador
     * @param playername Nombre del jugador
     */
    public int loadPlayerFame(String playername) {
        File file = new File((new StringBuilder()).append(
                this.pvpTitles.getDataFolder()).append(
                        File.separator).append("players").append(
                                File.separator).append(
                                        playername).append(
                                                ".yml").toString());

        FileConfiguration confPlayer = YamlConfiguration.loadConfiguration(file);

        return confPlayer.getInt("Fame");
    } 
	
    /**
     * Metodo para la primera conexion de los jugadores en el server
     * <p>Crea un archivo con el nombre del jugador y establece a cero sus 
     * puntos pvp</p>
     * @param playername Nombre del jugador
     */
    public void firstRunPlayer(String playername) {
        File file = new File((new StringBuilder()).append(
                this.pvpTitles.getDataFolder()).append(
                        File.separator).append(
                                "players").append(
                                File.separator).append(
                                        playername).append(
                                                ".yml").toString());

        if(!file.exists()) {            
            FileConfiguration confPlayer = new YamlConfiguration();
            confPlayer.set("Fame", 0);

            try {
                confPlayer.save(file);
            }
            catch (IOException e) {}
        }
    }
    
    // ############################################################## \\
    
    /**
     * Metodo para crear el config en caso de que no exista
     */
    public void loadConfig() {                
        if (!new File(new StringBuilder().append(
                pvpTitles.getDataFolder()).append(
                        File.separator).append(
                                "config.yml").toString()).exists()) {            
            pvpTitles.saveDefaultConfig();
        }
        
        pvpTitles.reloadConfig();
        config = pvpTitles.getConfig();        
    }
    
    /**
     * Metodo para guardar los datos del config en variables
     */
    private void cargaDatos() {        
        List<String> configList;
        configList = (List<String>)config.getList("RankNames");

        for (int i = 0; i < configList.size(); i++) {
            this.rankList.put(i, configList.get(i));
        }
        
        List<Integer> requFame;        
        requFame = (List<Integer>) config.getList("ReqFame");

        for (int i = 0; i < requFame.size(); i++) {
            this.reqFame.put(i, requFame.get(i));
        }

        this.GetPrefixColor(config.getString("PrefixColor"));

        this.tag = config.getString("Tag");
        
        try {
            this.top = config.getInt("Top");
        }
        catch (Exception ex) {
            pvpTitles.log.log(Level.INFO, "{0}Se ha establecido el ranking a 5 jugadores", PLUGIN);
            this.top = 5;
        }
        
        try {
            this.mod = config.getDouble("Mod");
        }
        catch (Exception ex) {
            pvpTitles.log.log(Level.INFO, "{0}Se ha establecido el modificador a 0.25", PLUGIN);
            this.mod = 0.25;
        }
        
        this.update = config.getBoolean("Update");
        this.alert = config.getBoolean("Alert");

        if(configList.size() != requFame.size()) {
            this.pvpTitles.log.info("WARNING - RankNames and ReqFame are not equal in their numbers.");
            this.pvpTitles.log.info("WARNING - RankNames and ReqFame are not equal in their numbers.");
            this.pvpTitles.log.info("WARNING - RankNames and ReqFame are not equal in their numbers.");
        }        
    }
    
    /**
     * Metodo para convertir el nombre del color a un valor valido
     * @param color String con el nombre del color
     */
    private void GetPrefixColor(String color) {
        this.prefixColor = ChatColor.valueOf(color.toUpperCase());
    }
}
