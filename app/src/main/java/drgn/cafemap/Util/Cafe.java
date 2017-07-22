package drgn.cafemap.Util;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;

import drgn.cafemap.BR;

/**
 * Created by Nobu on 2017/07/19.
 */

public class Cafe extends BaseObservable implements Serializable {

    private String cafeName;
    private String cafeAddress;
    private String cafeTime;
    private String cafeTel;
    private String cafeSocket;
    private String cafeWifi;

    public Cafe(String cafeName, String cafeAddress, String cafeTime, String cafeTel, String cafeSocket, String cafeWifi) {
        this.cafeName = cafeName;
        this.cafeAddress = cafeAddress;
        this.cafeTime = cafeTime;
        this.cafeTel = cafeTel;
        this.cafeSocket = cafeSocket;
        this.cafeWifi = cafeWifi;
    }

    @Bindable
    public String getCafeName() {
        return cafeName;
    }

    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
        notifyPropertyChanged(BR.cafeName);
    }

    @Bindable
    public String getCafeTel() {
        return cafeTel;
    }

    public void setCafeTel(String cafeTel) {
        this.cafeTel = cafeTel;
        notifyPropertyChanged(BR.cafeTel);
    }

    @Bindable
    public String getCafeAddress() {
        return cafeAddress;
    }

    public void setCafeAddress(String cafeAddress) {
        this.cafeAddress = cafeAddress;
        notifyPropertyChanged(BR.cafeAddress);
    }

    @Bindable
    public String getCafeTime() {
        return cafeTime;
    }

    public void setCafeTime(String cafeTime) {
        this.cafeTime = cafeTime;
        notifyPropertyChanged(BR.cafeTime);
    }

    @Bindable
    public String getCafeSocket() {
        return cafeSocket;
    }

    public void setCafeSocket(String cafeSocket) {
        this.cafeSocket = cafeSocket;
        notifyPropertyChanged(BR.cafeSocket);
    }

    @Bindable
    public String getCafeWifi() {
        return cafeWifi;
    }

    public void setCafeWifi(String cafeWifi) {
        this.cafeWifi = cafeWifi;
        notifyPropertyChanged(BR.cafeWifi);
    }
}
