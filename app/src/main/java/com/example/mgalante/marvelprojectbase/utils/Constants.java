package com.example.mgalante.marvelprojectbase.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by mgalante on 16/01/17.
 */

public class Constants {

    public static final String PORT = "80";
    public static final String API_URL = "http://gateway.marvel.com" + ":" + PORT;
    public static final String API_KEY = "cd31f94797faaf1c26a65f7a20cb086b";
    public static final String PRIVATE_KEY = "33ae0776e56c8c24f8edf1f315c8c2e4dc68f609";
    public static final String LOGTAG = "MarvelBaseProject";
    public static final Integer REQ_CREATE_FILE = 1;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 2;

    public static GoogleApiClient apiClient;

    public static LruCache<String, Bitmap> mMemoryCache;
    public static final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    public static final int cacheSize = maxMemory / 8;



}
