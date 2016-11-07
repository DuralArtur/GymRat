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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class DBContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.gymrat.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WORKOUT = "workout";
    public static final String PATH_PRS = "prs";
    public static final String PATH_GYM = "gym";

    public static final class WorkoutEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORKOUT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT;

        public static final String TABLE_NAME = "workout";
        public static final String COLUMN_WORKOUT_NAME = "workout_name";
        public static final String COLUMN_EXERCISE = "exercise";
        public static final String COLUMN_SET = "set_no";
        public static final String COLUMN_REPS = "reps";
        public static final String COLUMN_EXC_POSITION = "exc_position";
        public static final String COLUMN_WO_POSITION = "wo_position";

        public static Uri buildWorkoutUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PREntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRS;

        public static final String TABLE_NAME = "personal_records";
        public static final String COLUMN_PR_EXERCISE = "exercise";
        public static final String COLUMN_PR_WEIGHT = "weight";
        public static final String COLUMN_PR_REPS = "reps";
        public static final String COLUMN_VID_ADDRESS = "vid_address";
        public static final String COLUMN_POSITION = "position";
        public static Uri buildPRUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
        /*
            Student: This is the buildWeatherLocation function you filled in.
         */
        public static final class GymEntry implements BaseColumns {

            public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_GYM).build();

            public static final String CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GYM;
            public static final String CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GYM;

            // Table name
            public static final String TABLE_NAME = "gym";

            public static final String COLUMN_GYM_NAME = "gym_name";
            public static final String COLUMN_GYM_ADDRESS = "gym_address";
            public static final String COLUMN_GYM_LATT = "gym_latt";
            public static final String COLUMN_GYM_LONG = "gym_long";
            public static final String COLUMN_POSITION = "gym_position";

            public static Uri buildGymUri(long id) {
                return ContentUris.withAppendedId(CONTENT_URI, id);
            }
        }
    }

