package drgn.cafemap;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nobu on 2017/06/03.
 */

public class MapControlModel {

    // マーカー情報リスト
    private Map<String,Map<String,String>> markerList = new HashMap<>();
    //private


    //private Map<int,String> markerList = new HashMap<>();

    public MapControlModel(){

        markerList.put("key0",this.makeMarkerHashMap(0));
        markerList.put("key1",this.makeMarkerHashMap(1));

//        System.out.println(markerList.get("key1"));
//        System.out.println(markerList.get("key2"));

    }

    public Map<String,String> makeMarkerHashMap(int cnt){

        Map<String,String> locationInfo = new HashMap<>();

        if (cnt == 0) {
            locationInfo.put("name","HOME");
            locationInfo.put("lat","49.238323199999996");
            locationInfo.put("lon","-123.0418275");
        }else {
            locationInfo.put("name","Chili paper house");
            locationInfo.put("lat","49.23630368960825");
            locationInfo.put("lon","-123.0415491387248");
        }

        return locationInfo;
    }


    public Map<String, Map<String, String>> getMarkerList() {
        return markerList;
    }
}
