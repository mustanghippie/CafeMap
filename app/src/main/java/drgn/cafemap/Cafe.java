package drgn.cafemap;

/**
 * Created by Nobu on 2017/06/10.
 */


public class Cafe {
    private double lat;
    private double lon;
    private String cafeName;
    private String cafeAddress;
    private String cafeTime;
    private String cafeWifi;
    private String cafeSocket;
    private String cafeTel;

    public Cafe(double lat, double lon, String cafeName, String cafeAddress, String cafeTime, String cafeTel, String cafeSocket, String cafeWifi) {
        this.lat = lat;
        this.lon = lon;
        this.cafeName = cafeName;
        this.cafeAddress = cafeAddress;
        this.cafeTime = cafeTime;
        this.cafeWifi = cafeWifi;
        this.cafeSocket = cafeSocket;
        this.cafeTel = cafeTel;
    }
}
