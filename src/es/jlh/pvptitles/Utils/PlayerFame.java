/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.jlh.pvptitles.Utils;

import org.bukkit.ChatColor;

/**
 *
 * @author Julian
 */
public class PlayerFame implements Comparable {
    private final String name;
    private final int fame;
    
    public PlayerFame(String name, int fame) {
        this.name = name;
        this.fame = fame;
    }
    
    public String getName() {
        return name;
    }
    
    public int getFame() {
        return fame;
    }
    
    @Override
    public String toString() {
        return this.getName() + " (" + ChatColor.AQUA + this.getFame() + ChatColor.RESET + ")";
    }

    @Override
    public int compareTo(Object o) {
        PlayerFame pf = (PlayerFame) o;
        
        if (pf.getFame() > this.getFame()) {
            return 1;
        }
        else if (pf.getFame() < this.getFame()) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
