package es.jlh.pvptitles.Utils;

import java.io.File;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Lang {
    PLUGIN_ENABLED("Plugin activado correctamente"),
    PLUGIN_DISABLED("Plugin activado correctamente"),
    PLUGIN_RELOAD("&6Plugin recargado correctamente"),
    UPDATES_DISABLED("&eActualizaciones automaticas desactivadas"),
    COMMAND_FORBIDDEN("&4No puedes ejecutar ese comando"),
    COMMAND_ARGUMENTS("&4Te sobran argumentos!"),
    PLAYER_KILLED("&aHas matado a %killed% y has recibido %fameRec% de %tag%"),
    PLAYER_NEW_RANK("&bFelicidades! Ahora eres: %newRank%"),
    VETO_STARTED("&4Has sido vetado y no conseguiras mas fama hasta dentro de %TIME% min"),
    VETO_FINISHED("&cVeto finalizado"),
    RANK_INFO("&bRankUP: &fTe falta %rankup% de %tag% para conseguir %nextRank%");

    private final String value;
    public static YamlConfiguration lang = null;
    public static File langFile = new File("plugins/PvpTitles/messages.yml");
    public static File backupFile = new File("plugins/PvpTitles/messages.backup.yml");

    private Lang(final String value) {
        this.value = value;
    }

    public String getText() {
        String valor = this.getValue();
        if (lang != null && lang.contains(this.name())) {
            valor = lang.getString(this.name());
        }
        valor = ChatColor.translateAlternateColorCodes('&', valor);
        return valor;
    }

    public String getValue() {
        return this.value;
    }

    private static boolean compLocales() {
        // Compruebo si esta completo
        for (Lang idioma : Lang.values()) {
            if (!lang.contains(idioma.name())) {                
                try {
                    lang.save(backupFile); // Guardo una copia de seguridad                    
                    createConfig();
                    lang = YamlConfiguration.loadConfiguration(langFile);
                    return false;
                } 
                catch (IOException ex) {}
            }
        }
        return true;
    }
    
    public static void load() throws Exception {
        if (!langFile.exists()) {
            createConfig();
        }
        
        lang = YamlConfiguration.loadConfiguration(langFile);
        
        if (!compLocales()) {
            throw new Exception("Error loading locales, created a new one.");
        }             
    }
        
    public static void createConfig() {
        YamlConfiguration newConfig = new YamlConfiguration();
        
        newConfig.options().header (
            "#########################################\n" + 
            "## [LOCALES] No edites las %variables% ##\n" +
            "#########################################"
        );
        newConfig.options().copyHeader(true);
        
        for (Lang idioma : Lang.values()) {
            String name = idioma.name();
            String value = idioma.getValue();
            newConfig.set(name, value);
        }
        
        try {
            newConfig.save(langFile);
        } 
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
