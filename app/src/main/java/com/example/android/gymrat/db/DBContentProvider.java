/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.gymrat.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DBContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DBHelper mHelper;

    static final int WORKOUT = 100;
    static final int PRS = 200;
    static final int GYMS = 300;

    private static final SQLiteQueryBuilder sQueryBuilder;

    static {
        sQueryBuilder = new SQLiteQueryBuilder();

        sQueryBuilder.setTables(DBContract.WorkoutEntry.TABLE_NAME);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DBContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DBContract.PATH_WORKOUT, WORKOUT);
        matcher.addURI(authority, DBContract.PATH_PRS, PRS);
        matcher.addURI(authority, DBContract.PATH_GYM, GYMS);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUT:
                return DBContract.WorkoutEntry.CONTENT_TYPE;
            case PRS:
                return DBContract.PREntry.CONTENT_TYPE;
            case GYMS:
                return DBContract.GymEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case WORKOUT: {
                retCursor = mHelper.getReadableDatabase().query(
                        DBContract.WorkoutEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location"
            case PRS: {
                retCursor = mHelper.getReadableDatabase().query(
                        DBContract.PREntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case GYMS: {
                retCursor = mHelper.getReadableDatabase().query(
                        DBContract.GymEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case WORKOUT: {
                long _id = db.insert(DBContract.WorkoutEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DBContract.WorkoutEntry.buildWorkoutUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case PRS: {
                long _id = db.insert(DBContract.PREntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DBContract.PREntry.buildPRUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case GYMS: {
                long _id = db.insert(DBContract.GymEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DBContract.GymEntry.buildGymUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case WORKOUT:
                rowsDeleted = db.delete(
                        DBContract.WorkoutEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRS:
                rowsDeleted = db.delete(
                        DBContract.PREntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GYMS:
                rowsDeleted = db.delete(
                        DBContract.GymEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case WORKOUT:
//                normalizeDate(values);
                rowsUpdated = db.update(DBContract.WorkoutEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PRS:
                rowsUpdated = db.update(DBContract.PREntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case GYMS:
                rowsUpdated = db.update(DBContract.GymEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

//    @NonNull
//    @Override
//    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
//        return super.applyBatch(operations);
//    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        switch (match) {
            case WORKOUT:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DBContract.WorkoutEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case PRS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DBContract.PREntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case GYMS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DBContract.GymEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}