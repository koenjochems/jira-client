package net.rcarz.jiraclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

/**
 * Abstract field class that provides helper methods to convert field data
 * 
 * @author Koen Jochems
 *
 */
public abstract class AField {
	/**
     * Gets a boolean value from the given object.
     *
     * @param b a Boolean instance
     *
     * @return a boolean primitive or false if b isn't a Boolean instance
     */
    public static boolean getBoolean(Object b) {
        boolean result = false;

        if (b instanceof Boolean)
            result = ((Boolean)b).booleanValue();

        return result;
    }
    
    /**
     * Gets an floating-point number from the given object.
     *
     * @param i an Double instance
     *
     * @return an floating-point number or null if i isn't a Double instance
     */
    public static Double getDouble(Object i) {
        Double result = null;

        if (i instanceof Double)
            result = (Double)i;

        return result;
    }

    /**
     * Gets an integer from the given object.
     *
     * @param i an Integer instance
     *
     * @return an integer primitive or 0 if i isn't an Integer instance
     */
    public static int getInteger(Object i) {
        int result = 0;

        if (i instanceof Integer)
            result = ((Integer)i).intValue();

        return result;
    }
    
    /**
     * Gets a list of integers from the given object.
     *
     * @param ia a JSONArray instance
     *
     * @return a list of integers
     */
    public static List<Integer> getIntegerArray(Object ia) {
        List<Integer> results = new ArrayList<Integer>();

        if (ia instanceof JSONArray) {
            for (Object v : (JSONArray)ia)
                results.add((Integer)v);
        }

        return results;
    }

    /**
     * Gets a generic map from the given object.
     *
     * @param keytype Map key data type
     * @param valtype Map value data type
     * @param m a JSONObject instance
     *
     * @return a Map instance with all entries found in m
     */
    public static Map<String, Object> getMap(Object data) {
    	Map<String, Object> result = null;
    	
        if (data instanceof Map) {
        	result = (Map<String, Object>) data;
        }

        return result;
    }
    
    public static List<Object> getList(Object data) {
    	List<Object> result = null;
    	
        if (data instanceof List) {
        	result = (List<Object>) data;
        } else if (data instanceof Map) {
        	Map<String, Object> map = getMap(data);
       		result = getList(map.get("data"));
        }
        
        return result;
    }
    
    /**
     * Gets a string from the given object.
     *
     * @param s a String instance
     *
     * @return a String or null if s isn't a String instance
     */
    public static String getString(Object s) {
        String result = null;

        if (s instanceof String)
            result = (String)s;

        return result;
    }

    /**
     * Gets a list of strings from the given object.
     *
     * @param sa a JSONArray instance
     *
     * @return a list of strings found in sa
     */
    public static List<String> getStringArray(Object data) {
        List<String> results = new ArrayList<String>();

        if (data instanceof List) {
            for (Object s : getList(data)) {
                if (s instanceof String)
                    results.add((String) s);
            }
        }

        return results;
    }
}
