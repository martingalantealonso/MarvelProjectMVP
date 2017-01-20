package com.example.mgalante.marvelprojectbase.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mgalante on 20/01/17.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper{

    private static final String DATABASE_NAME="characters.db";
    private static final int DATABASE_VERSION=1;
    private static DBHelper helper=null;
    private static final AtomicInteger usageCounter = new AtomicInteger(0);
    private Dao<Characters,Integer> characterDao;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getHelper(Context context) {

        if (helper == null) {

            helper = new DBHelper(context);
        }
        usageCounter.incrementAndGet();
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            TableUtils.createTableIfNotExists(connectionSource,Characters.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        onCreate(database, connectionSource);
    }

    public Dao<Characters, Integer> getCharacterDao() throws SQLException {
        if (characterDao == null) {
            characterDao = getDao(Characters.class);
        }
        return characterDao;
    }

    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();
            characterDao = null;
            helper = null;
        }
    }

}
