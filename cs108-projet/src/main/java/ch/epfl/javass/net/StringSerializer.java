package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;

import ch.epfl.javass.jass.Player;

/**
 * The utility class StringSerializer offers methods to serialize and deserialize several arguments required for the default methods of a player. 
 * By serializing a value, the value is turned into a specific sequence of characters contained in a String. 
 * The serialized value can then be deserialized again by the corresponding method to obtain the formerly
 * serialized value.
 * 
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * @see Player
 */
public final class StringSerializer {
    
    //used as base for serialization of integer types
    private final static int HEX_BASE = 16;
    
    
    /**
     * The class StringSerializer is not instantiable
     */
    private StringSerializer() {

    }

    /**
     * Turns a number of type long into an String with the textual representation of the unsigned long in base 16
     * 
     * @param l (long): The long to serialize
     * @return the String containing the serialized value
     */
    public static String serializeLong(long l) {
        return Long.toUnsignedString(l, HEX_BASE);
    }

    /**
     * Turns an integer of type int into an String with the textual representation of the unsigned integer in base 16
     * 
     * @param i (int): The integer to serialize
     * @return the String containing the serialized value
     */
    public static String serializeInt(int i) {
        return Integer.toUnsignedString(i, HEX_BASE);
    }

    /**
     * Turns a String encoded with UTF-8 into a String encoded with Base64
     * 
     * @param s (String): The String to serialize
     * @return the String containing the serialized value
     */
    public static String serializeString(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(UTF_8));
    }

    /**
     * Turns a set of strings into a single String, delimited by the given separator
     * 
     * @param separator (String): The desired delimiter
     * @param strings (String...) The strings to be serialized
     * @return the String containing the serialized value
     */
    public static String serializeComposition(String separator, String... strings) {
        return String.join(separator, strings);
    }

    /**
     * Turns a serialized String into an unsigned long
     * 
     * @param s (String): The serialized long, must not be null
     * @return the deserialized long
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, HEX_BASE);
    }

    /**
     * Turns a serialized String into an unsigned int
     * 
     * @param s (String): The serialized int, must not be null
     * @return the deserialized int
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, HEX_BASE);
    }

    /**
     * Turns a serialized String into its deserialized version encoded in UTF-8
     * 
     * @param s (String) the String to deserialize, must not be null
     * @return the deserialized String
     */
    public static String deserializeString(String s) {
        return new String(Base64.getDecoder().decode(s.getBytes(UTF_8)), UTF_8);
    }

    /**
     * Turns a serialized composition of Strings back into a set of Strings
     * 
     * @param separator (String): The symbol to be considered as delimiter
     * @param s (String): The String representing the composition
     * @return The single words contained in the composition
     */
    public static String[] deserializeComposition(String separator, String s) {
        return s.split(separator);
    }

}
