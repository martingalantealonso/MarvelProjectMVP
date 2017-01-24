package com.example.mgalante.marvelprojectbase.eventbus;

/**
 * Created by mgalante on 24/01/17.
 */

public class NetworkStateChanged {

    public boolean isInternetConected;

    public NetworkStateChanged(boolean isInternetConected) {
        this.isInternetConected = isInternetConected;
    }

    public boolean isInternetConected() {
        return this.isInternetConected;
    }
}
