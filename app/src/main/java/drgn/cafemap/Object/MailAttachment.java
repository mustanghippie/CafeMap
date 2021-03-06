package drgn.cafemap.Object;

/**
 * Created by Nobu on 2017/06/10.
 */


public class MailAttachment {
    private String lat;
    private String lon;
    private String cafeName;
    private String cafeAddress;
    private String cafeTime;
    private String cafeWifi;
    private String cafeSocket;
    private String cafeTel;
    private String sql;

    public MailAttachment(String lat, String lon, String cafeName, String cafeAddress, String cafeTime, String cafeTel, String cafeSocket, String cafeWifi) {
        this.lat = lat;
        this.lon = lon;
        this.cafeName = cafeName;
        this.cafeAddress = cafeAddress;
        this.cafeTime = cafeTime;
        this.cafeWifi = cafeWifi;
        this.cafeSocket = cafeSocket;
        this.cafeTel = cafeTel;
        this.sql = makeInsertSQL();
    }

    private String makeInsertSQL() {
        sql = "INSERT INTO cafe_master_tbl(lat, lon, name, address, time, tel, wifi, socket) " +
                "VALUES('" + lat + "', '" + lon + "', '" + cafeName + "', '" + cafeAddress + "', " +
                "'" + cafeTime + "', '" + cafeTel + "', '" + cafeSocket + "', '" + cafeWifi + "');";

        return sql;
    }
}
