
package models;

import java.net.MulticastSocket;

/**
 * @author JOAO
 */
public class AuctionData {
    
    public static String action;
    public static final String USER = "server";
    public static String itemName = "";
    public static String itemDescription = "";
    public static float currentPrice = 0;
    public static float bidIncrement = 0;
    public static int chronometer = 0;
    public static int nextbid = 0;
    public static int timeRemaining = 0;
    public static String higherBidCPF = "nobody";
    
    public static int multicastPort = 4446;
    public static String multicastAddress = "230.0.0.0";
    public static MulticastSocket multicastSocket;
    
}
