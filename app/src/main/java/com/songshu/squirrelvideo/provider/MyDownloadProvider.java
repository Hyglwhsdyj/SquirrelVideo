package com.songshu.squirrelvideo.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.utils.L;

import java.util.Arrays;

/**
 * Created by yb on 15-8-11.
 */
public class MyDownloadProvider extends ContentProvider {

    private static final String TAG = MyDownloadProvider.class.getSimpleName() + ":";


    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        L.d(TAG, "...uri:" + uri + "...projection:" + Arrays.toString(projection) + "...selection:" + selection + "...selectionArgs:" + Arrays.toString(selectionArgs) + "...sortOrder:" + sortOrder);
        Cursor cursor = App.getmDBHelper().getWritableDatabase().query(true, "Download", projection, selection, selectionArgs, null, null, null, "1");
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
