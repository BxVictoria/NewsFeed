package com.smotrova.newsfeed.store;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import org.apache.commons.lang3.ArrayUtils;

public class NewsProvider extends ContentProvider {

    public static final String AUTHORITY = "com.smotrova.newsfeed.store";
    public static final String BASE_PATH = "news_list";
    public static final Uri NEWS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + BASE_PATH);
    static final int CODE_NEWS = 1;
    static final int CODE_NEWS_ID = 2;
    private static final int DATABASE_VERSION = 1;
    /*MIME*/
    private static final String TYPE_ITEM = "vnd.android.cursor.item";
    private static final String TYPE_DIR = "vnd.android.cursor.dir";
    private static final String VND = "/vnd.";
    private static final String DOT = ".";
    public static final String TYPE_NEWS_DIR = TYPE_DIR + VND + AUTHORITY + DOT + NewsTable.TABLE_NAME;
    public static final String TYPE_NEWS_ITEM = TYPE_ITEM + VND + AUTHORITY + DOT + NewsTable.TABLE_NAME;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, CODE_NEWS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CODE_NEWS_ID);
    }

    private DBHelper dbHelper;
    private ContentResolver resolver;

    public static Uri getNewsContentUri(long id) {
        return NEWS_CONTENT_URI
                .buildUpon()
                .appendPath(String.valueOf(id))
                .build();
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);
        resolver = context.getContentResolver();
        return dbHelper.getWritableDatabase() != null;
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case CODE_NEWS:
                return TYPE_NEWS_DIR;
            case CODE_NEWS_ID:
                return TYPE_NEWS_ITEM;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        final int rowsDeleted;
        switch (uriType) {
            case CODE_NEWS:
                rowsDeleted = deleteNews(selection, selectionArgs);
                break;
            case CODE_NEWS_ID:
                rowsDeleted = deleteNewsID(uri, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (rowsDeleted != 0) {
            resolver.notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private int deleteNews(String selection, String[] selectionArgs) {
        return dbHelper.getWritableDatabase().delete(NewsTable.TABLE_NAME, selection,
                selectionArgs);
    }

    private int deleteNewsID(Uri uri, String selection, String[] selectionArgs) {
        if (TextUtils.isEmpty(selection)) {
            return dbHelper.getWritableDatabase().delete(NewsTable.TABLE_NAME,
                    NewsTable._ID + "= ?", getLastUriSegmentInArray(uri));
        } else {
            return dbHelper.getWritableDatabase().delete(NewsTable.TABLE_NAME,
                    NewsTable._ID + "= ? and " + selection,
                    ArrayUtils.addAll(getLastUriSegmentInArray(uri), selectionArgs));
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        final long id;
        switch (uriType) {
            case CODE_NEWS:
                id = insertNews(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (id != -1) {
            resolver.notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private long insertNews(ContentValues values) {
        return dbHelper.getWritableDatabase().insert(NewsTable.TABLE_NAME, null, values);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CODE_NEWS:
                cursor = queryNews(projection, selection, selectionArgs, sortOrder);
                break;
            case CODE_NEWS_ID:
                cursor = queryNewsID(projection, selection, selectionArgs, sortOrder, uri);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(resolver, uri);
        return cursor;
    }

    private Cursor queryNews(String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(NewsTable.TABLE_NAME);
        return queryBuilder.query(dbHelper.getWritableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    private Cursor queryNewsID(String[] projection, String selection,
                               String[] selectionArgs, String sortOrder, Uri uri) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(NewsTable.TABLE_NAME);
        queryBuilder.appendWhere(NewsTable._ID + " = ?");
        return queryBuilder.query(dbHelper.getWritableDatabase(), projection, selection,
                ArrayUtils.addAll(selectionArgs, getLastUriSegmentInArray(uri)),
                null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        final int rowsUpdated;
        switch (uriType) {
            case CODE_NEWS:
                rowsUpdated = updateNews(values, selection, selectionArgs);
                break;
            case CODE_NEWS_ID:
                rowsUpdated = updateNewsID(uri, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (rowsUpdated != 0) {
            resolver.notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int updateNews(ContentValues values, String selection,
                           String[] selectionArgs) {
        return dbHelper.getWritableDatabase().update(NewsTable.TABLE_NAME, values,
                selection, selectionArgs);
    }

    private int updateNewsID(Uri uri, ContentValues values, String selection,
                             String[] selectionArgs) {
        if (TextUtils.isEmpty(selection)) {
            return dbHelper.getWritableDatabase().update(NewsTable.TABLE_NAME, values,
                    NewsTable._ID + "= ?", getLastUriSegmentInArray(uri));
        } else {
            return dbHelper.getWritableDatabase().update(NewsTable.TABLE_NAME, values,
                    NewsTable._ID + "= ? and " + selection,
                    ArrayUtils.addAll(getLastUriSegmentInArray(uri), selectionArgs));
        }
    }

    private String[] getLastUriSegmentInArray(Uri uri) {
        return new String[]{uri.getLastPathSegment()};
    }

    public interface NewsTable {
        public static final String TABLE_NAME = "NEWS_TABLE";
        public static final String _ID = BaseColumns._ID;
        public static final String CREATE = "CREATE_FIELD";
        public static final String TITLE = "TITLE";
        public static final String IMG_URL = "IMG_URL";
        public static final String TYPE = "TYPE_FIELD";
        public static final String BODY = "BODY";
        public static final String URL = "URL";
        public static final String POSITION = "POSITION";
        public static final String GOODS_ID = "GOODS_ID";
        public static final String WORDS2 = "WORDS2";
    }

    private static class DBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "news_list.db";

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table "
                    + NewsTable.TABLE_NAME
                    + "("
                    + NewsTable._ID + " integer primary key, "
                    + NewsTable.CREATE + " integer, "
                    + NewsTable.TITLE + " text, "
                    + NewsTable.IMG_URL + " text, "
                    + NewsTable.TYPE + " text, "
                    + NewsTable.BODY + " text, "
                    + NewsTable.URL + " text, "
                    + NewsTable.POSITION + " integer, "
                    + NewsTable.GOODS_ID + " integer, "
                    + NewsTable.WORDS2 + " text"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + NewsTable.TABLE_NAME);
            onCreate(db);
        }
    }

}

