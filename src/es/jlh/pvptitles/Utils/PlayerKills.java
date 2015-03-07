/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.jlh.pvptitles.Utils;

/**
 *
 * @author Julian
 */
public class PlayerKills {
    
    private String nombre;
    private int kills;

    public PlayerKills(String nombre, int kills) {
        this.nombre = nombre;
        this.kills = kills;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }        
    
    @Override
    public String toString() {
        return this.getNombre() + " (" + this.getKills() + ")";
    }
}
