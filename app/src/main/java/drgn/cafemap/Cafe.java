package drgn.cafemap;

/**
 * Created by musta on 2017/06/10.
 */

public class Cafe {

    private String cafeName;
    private String cafeAddress;
    private String cafeWifi;
    private String cafeSocket;
    private String cafeTel;

    public Cafe(String cafeName, String cafeAddress, String cafeWifi, String cafeSocket, String cafeTel) {
        this.cafeName = cafeName;
        this.cafeAddress = cafeAddress;
        this.cafeWifi = cafeWifi;
        this.cafeSocket = cafeSocket;
        this.cafeTel = cafeTel;
    }

    public String getCafeName() {
        return cafeName;
    }

    public String getCafeAddress() {
        return cafeAddress;
    }

    public String getCafeWifi() {
        return cafeWifi;
    }

    public String getCafeSocket() {
        return cafeSocket;
    }

    public String getCafeTel() {
        return cafeTel;
    }
}
