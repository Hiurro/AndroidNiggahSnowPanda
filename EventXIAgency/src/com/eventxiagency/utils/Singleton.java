package com.eventxiagency.utils;

import java.util.List;

public class Singleton {

    /** Récupère l'instance unique de la class Singleton.<p>
    * Remarque : le constructeur est rendu inaccessible
    */
    public static Singleton getInstance() {
        if (null == instance) { // Premier appel
            instance = new Singleton();
        }
        return instance;
    }

    /** Constructeur redéfini comme étant privé pour interdire
    * son appel et forcer à passer par la méthode <link
    */
    private Singleton() {
    }

    /** L'instance statique */
    private static Singleton instance;

    public static String _url = "10.176.238.82";
    public static String _port = "8000";
    
    public String partie_code;
    public String nom_groupe;
    public String[] coordinateur;
    public String[][] animateurs;
    public String[] enquete;
    public String[] equipes;

}