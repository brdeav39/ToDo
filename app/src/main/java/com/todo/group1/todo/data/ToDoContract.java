package com.todo.group1.todo.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;

/**
 * Defines table and column names for the to do database
 */
public class ToDoContract {

    // Serves as a name for the content provider
    public static final String CONTENT_AUTHORITY = "com.todo.group1.todo";

    // Create a base URI which can be  used to contact the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths for the URI
    public static final String PATH_TASK = "task";
    public static final String PATH_PRIORITY = "priority";
    public static final String PATH_LABEL = "label";

    /**
     * Define the structure of the task table.
     */
    public static final class TaskEntry implements BaseColumns {

        // Set up the TaskEntry URI
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASK).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASK;

        // define name of table
        public static final String TABLE_NAME = "task";

        // title and detail of task
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DETAIL = "detail";

        // date task is due
        public static final String COLUMN_DUE_DATE = "due_date";
        // date task was created
        public static final String COLUMN_CREATE_DATE = "create_date";

        // label(s) assigned to task
        public static final String COLUMN_LABEL_ID = "label_id";

        // priority assigned to task
        public static final String COLUMN_PRIORITY_ID = "priority_id";

        // whether or not a reminder has been added
        public static final String COLUMN_REMINDER_ADDED = "reminder_added";
        // whether or not a task has been marked complete
        public static final String COLUMN_IS_COMPLETED = "is_complete";
        // whether or not a task has been marked deleted
        public static final String COLUMN_IS_DELETED = "is_deleted";

        // the parent task of a task, if it has one
        public static final String COLUMN_PARENT_TASK_ID = "parent_task";

        public static Uri buildTaskUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTaskWithPriority(String id) {
            return CONTENT_URI.buildUpon().appendPath("priority").appendPath(id).build();
        }

        public static String getPriorityIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buildTaskBeforeDate(Date date) {
            // date format needs to be in milliseconds since epoch
            long milliseconds = date.getTime();
            String str_date = String.valueOf(milliseconds);
            return CONTENT_URI.buildUpon().appendPath("date").appendPath(str_date).build();
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buildTaskMarkedComplete() {
            return CONTENT_URI.buildUpon().appendPath("complete").build();
        }

        public static Uri buildTasksWithLabel(String label_id) {
            return CONTENT_URI.buildUpon().appendPath("label").appendPath(label_id).build();
        }

        public static String getLabelIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buildTaskWithId(String id) {
            return CONTENT_URI.buildUpon().appendPath("id").appendPath(id).build();
        }

        public static String getTaskIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

    /**
     * Define the contents of the Priority table and populate it.
     */
    public static final class TaskPriority implements BaseColumns {

        // Set up the Priority URI
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRIORITY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRIORITY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRIORITY;

        // define name of table
        public static final String TABLE_NAME = "priority";

        // define priority
        public static final String COLUMN_PRIORITY = "priority";

        public static Uri buildPriorityUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPriorityWithId(String priority_id) {
            return CONTENT_URI.buildUpon().appendPath(priority_id).build();
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    /**
     * Define the contents of the label table.
     */
    public static final class TaskLabel implements BaseColumns {

        // Set up the Label Uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LABEL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LABEL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LABEL;

        // define name of table
        public static final String TABLE_NAME = "label";

        // define label
        public static final String COLUMN_LABEL = "label";

        public static Uri buildLabelUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildLabelsWithTitle(String title) {
            return CONTENT_URI.buildUpon().appendPath("title").appendPath(title).build();
        }

        public static Uri buildLabelWithId(String label_id) {
            return CONTENT_URI.buildUpon().appendPath("id").appendPath(label_id).build();
        }

        public static String getTitleFromUri(Uri uri) { return uri.getPathSegments().get(2); }

        public static String getIdFromUri(Uri uri) { return uri.getPathSegments().get(2); }

        public static String getLabelFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
