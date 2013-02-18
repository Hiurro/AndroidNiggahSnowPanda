package com.eventxiagency.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import android.util.Log;

public class MyTools {
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	public static Object JSONDecode(String encodedJSON)
	{
		Object obj = JSONValue.parse(encodedJSON);
		return obj;
	}
	
	/** Exemple d'implémentation d'un singleton dans le cas du multithreading.<p>
	  * Cet exemple ne fait rien.
	  */
}
