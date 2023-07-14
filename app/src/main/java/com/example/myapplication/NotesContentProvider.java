package com.example.myapplication;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;


public class NotesContentProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher;

    private static final int NOTES_ALL = 1;
    private static final int NOTES_ONE = 2;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(NotesMetaData.AUTHORITY, "notes", NOTES_ALL);
        sUriMatcher.addURI(NotesMetaData.AUTHORITY, "notes/#", NOTES_ONE);
    }

    // Map table columns
    private static final HashMap<String, String> sNotesColumnProjectionMap;

    static {
        sNotesColumnProjectionMap = new HashMap<String, String>();
        sNotesColumnProjectionMap.put(NotesMetaData.NotesTable.ID,
                NotesMetaData.NotesTable.ID);
        sNotesColumnProjectionMap.put(NotesMetaData.NotesTable.TITLE,
                NotesMetaData.NotesTable.TITLE);
        sNotesColumnProjectionMap.put(NotesMetaData.NotesTable.CONTENT,
                NotesMetaData.NotesTable.CONTENT);
    }

    public static class NotesDBHelper extends SQLiteOpenHelper {

        public NotesDBHelper(Context c) {
            super(c, NotesMetaData.DATABASE_NAME, null,
                    NotesMetaData.DATABASE_VERSION);
        }

        private static final String SQL_QUERY_CREATE = "CREATE TABLE "
                + NotesMetaData.NotesTable.TABLE_NAME + " ("
                + NotesMetaData.NotesTable.ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NotesMetaData.NotesTable.TITLE + " TEXT NOT NULL, "
                + NotesMetaData.NotesTable.CONTENT + " TEXT NOT NULL" + ");";

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("In NoteDb Helper"," In On Create");
            db.execSQL(SQL_QUERY_CREATE);
        }

        private static final String SQL_QUERY_DROP = "DROP TABLE IF EXISTS "
                + NotesMetaData.NotesTable.TABLE_NAME + ";";

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
            db.execSQL(SQL_QUERY_DROP);
            onCreate(db);
        }
    }

    // create a db helper object
    private NotesDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new NotesDBHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case NOTES_ALL:
                count = db.delete(NotesMetaData.NotesTable.TABLE_NAME, where,
                        whereArgs);
                break;

            case NOTES_ONE:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(
                        NotesMetaData.NotesTable.TABLE_NAME,
                        NotesMetaData.NotesTable.ID
                                + " = "
                                + rowId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case NOTES_ALL:
                return NotesMetaData.CONTENT_TYPE_NOTES_ALL;

            case NOTES_ONE:
                return NotesMetaData.CONTENT_TYPE_NOTES_ONE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        // you cannot insert a bunch of values at once so throw exception
        if (sUriMatcher.match(uri) != NOTES_ALL) {
            throw new IllegalArgumentException(" Unknown URI: " + uri);
        }

        // Insert once row
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(NotesMetaData.NotesTable.TABLE_NAME, null,
                values);
        if (rowId > 0) {
            Uri notesUri = ContentUris.withAppendedId(
                    NotesMetaData.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(notesUri, null);
            return notesUri;
        }
        throw new IllegalArgumentException("<Illegal>Unknown URI: " + uri);
    }

    // Get values from Content Provider
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case NOTES_ALL:
                builder.setTables(NotesMetaData.NotesTable.TABLE_NAME);
                builder.setProjectionMap(sNotesColumnProjectionMap);
                break;

            case NOTES_ONE:
                builder.setTables(NotesMetaData.NotesTable.TABLE_NAME);
                builder.setProjectionMap(sNotesColumnProjectionMap);
                builder.appendWhere(NotesMetaData.NotesTable.ID + " = "
                        + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor queryCursor = builder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return queryCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case NOTES_ALL:
                count = db.update(NotesMetaData.NotesTable.TABLE_NAME, values,
                        where, whereArgs);
                break;

            case NOTES_ONE:
                String rowId = uri.getLastPathSegment();
                count = db
                        .update(NotesMetaData.NotesTable.TABLE_NAME, values,
                                NotesMetaData.NotesTable.ID + " = " + rowId +
                                        (!TextUtils.isEmpty(where) ? " AND (" + ")" : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}