package com.todo.group1.todo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This class provides the content provider for the To Do application.
 */

@SuppressWarnings("ConstantConditions")
public class ToDoProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ToDoDbHelper mOpenHelper;

    // Uri Identification
    static final int TASKS = 100;
    static final int TASKS_WITH_PRIORITY = 101;
    static final int TASKS_AFTER_DATE = 102;
    static final int TASKS_MARKED_COMPLETE = 103;
    static final int TASKS_WITH_LABEL = 104;
    static final int TASK_WITH_ID = 105;

    static final int LABEL = 300;
    static final int LABEL_WITH_TITLE = 301;
    static final int LABEL_WITH_ID = 302;

    static final int PRIORITY = 500;
    static final int PRIORITY_WITH_ID = 501;

    private static final SQLiteQueryBuilder sTasksWithPriorityAndLabels = new SQLiteQueryBuilder();
    public static final String [] TASK_COLUMNS = {
            ToDoContract.TaskEntry.TABLE_NAME + "." + ToDoContract.TaskEntry._ID,
            ToDoContract.TaskEntry.COLUMN_TITLE,
            ToDoContract.TaskEntry.COLUMN_DETAIL,
            ToDoContract.TaskEntry.COLUMN_DUE_DATE,
            ToDoContract.TaskEntry.COLUMN_CREATE_DATE,
            ToDoContract.TaskEntry.COLUMN_LABEL_ID,
            ToDoContract.TaskLabel.COLUMN_LABEL,
            ToDoContract.TaskEntry.COLUMN_PRIORITY_ID,
            ToDoContract.TaskPriority.COLUMN_PRIORITY,
            ToDoContract.TaskEntry.COLUMN_IS_COMPLETED,
            ToDoContract.TaskEntry.COLUMN_IS_DELETED
    };

    static {
        sTasksWithPriorityAndLabels.setTables(
                ToDoContract.TaskEntry.TABLE_NAME + " INNER JOIN " +
                        ToDoContract.TaskPriority.TABLE_NAME +
                        " ON " + ToDoContract.TaskEntry.TABLE_NAME +
                        "." + ToDoContract.TaskEntry.COLUMN_PRIORITY_ID +
                        " = " + ToDoContract.TaskPriority.TABLE_NAME +
                        "." + ToDoContract.TaskPriority._ID +
                        " INNER JOIN " + ToDoContract.TaskLabel.TABLE_NAME +
                        " ON " + ToDoContract.TaskEntry.TABLE_NAME +
                        "." + ToDoContract.TaskEntry.COLUMN_LABEL_ID +
                        " = " + ToDoContract.TaskLabel.TABLE_NAME +
                        "." + ToDoContract.TaskLabel._ID
        );
    }

    /**
     * Return all incomplete tasks
     * @param uri the uri passed by the caller.
     * @param projection the columns to be returned.
     * @param selection the WHERE clause for the SQLite query.
     * @param selectionArgs the arguments for the SQLite query.
     * @param sortOrder the order by value for the SQLite query.
     * @return a cursor containing the tasks.
     */
    private Cursor getTasks(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String updatedSelection;
        String [] updatedSelectionArgs;
        if (selectionArgs != null) {
            updatedSelection = selection + " AND " +
                    ToDoContract.TaskEntry.COLUMN_IS_COMPLETED + " = ?";
            updatedSelectionArgs = new String[] { selectionArgs[0], "0"};
        }
        else {
            updatedSelection = ToDoContract.TaskEntry.COLUMN_IS_COMPLETED + " = ?";
            updatedSelectionArgs = new String [] {"0"};
        }

        return sTasksWithPriorityAndLabels.query(mOpenHelper.getReadableDatabase(),
                TASK_COLUMNS,
                updatedSelection,
                updatedSelectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Return all tasks with the priority provided in the uri.
     * @param uri the uri passed by the caller, contains the priority id.
     * @param projection the columnst to be returned.
     * @param selection the WHERE clause for the SQLite query.
     * @param selectionArgs the arguments for the WHERE clause (they replace the question marks).
     * @param sortOrder the order by value for the SQLite query.
     * @return a cursor containing the tasks.
     */
    private Cursor getTasksWithPriority(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String updatedSelection;
        String [] updatedSelectionArgs;
        if (selectionArgs != null) {
            updatedSelection = selection + " AND " +
                    ToDoContract.TaskEntry.COLUMN_PRIORITY_ID + " = ? AND " +
                    ToDoContract.TaskEntry.COLUMN_IS_COMPLETED + " = ?";
            updatedSelectionArgs = new String[] { selectionArgs[0], ToDoContract.TaskEntry.getPriorityIdFromUri(uri), "0"};
        }
        else {
            updatedSelection = ToDoContract.TaskEntry.COLUMN_PRIORITY_ID + " = ? AND " +
                    ToDoContract.TaskEntry.COLUMN_IS_COMPLETED + " = ?";
            updatedSelectionArgs = new String [] {ToDoContract.TaskEntry.getPriorityIdFromUri(uri), "0"};
        }

        return sTasksWithPriorityAndLabels.query(mOpenHelper.getReadableDatabase(),
                TASK_COLUMNS,
                updatedSelection,
                updatedSelectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Return all tasks before the date provided in the Uri.
     * @param uri the uri passed by the caller, contains the priority id.
     * @param projection the columnst to be returned.
     * @param selection the WHERE clause for the SQLite query.
     * @param selectionArgs the arguments for the WHERE clause (they replace the question marks).
     * @param sortOrder the order by value for the SQLite query.
     * @return a cursor containing the tasks.
     */
    private Cursor getTasksBeforeDate(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String updatedSelection;
        String [] updatedSelectionArgs;
        if (selectionArgs != null) {
            updatedSelection = selection + " AND " +
                    ToDoContract.TaskEntry.COLUMN_DUE_DATE + " <= ? AND " +
                    ToDoContract.TaskEntry.COLUMN_IS_COMPLETED + " = ?";
            updatedSelectionArgs = new String[] { selectionArgs[0], ToDoContract.TaskEntry.getDateFromUri(uri), "0"};
        }
        else {
            updatedSelection = ToDoContract.TaskEntry.COLUMN_DUE_DATE + " <= ? AND " +
                    ToDoContract.TaskEntry.COLUMN_IS_COMPLETED + " = ?";
            updatedSelectionArgs = new String [] {ToDoContract.TaskEntry.getDateFromUri(uri), "0"};
        }

        return sTasksWithPriorityAndLabels.query(mOpenHelper.getReadableDatabase(),
                TASK_COLUMNS,
                updatedSelection,
                updatedSelectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Return all tasks with the label id given in the Uri.
     * @param uri the uri passed by the caller, contains the priority id.
     * @param projection the columnst to be returned.
     * @param selection the WHERE clause for the SQLite query.
     * @param selectionArgs the arguments for the WHERE clause (they replace the question marks).
     * @param sortOrder the order by value for the SQLite query.
     * @return a cursor containing the tasks.
     */
    private Cursor getTasksWithLabel(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String updatedSelection;
        String [] updatedSelectionArgs;
        if (selectionArgs != null) {
            updatedSelection = selection + " AND " +
                    ToDoContract.TaskEntry.COLUMN_LABEL_ID + " = ? AND " +
                    ToDoContract.TaskEntry.COLUMN_IS_COMPLETED + " = ?";
            updatedSelectionArgs = new String[] { selectionArgs[0], ToDoContract.TaskEntry.getLabelIdFromUri(uri), "0"};
        }
        else {
            updatedSelection = ToDoContract.TaskEntry.COLUMN_LABEL_ID + " = ? AND " +
                    ToDoContract.TaskEntry.COLUMN_IS_COMPLETED + " = ?";
            updatedSelectionArgs = new String [] {ToDoContract.TaskEntry.getLabelIdFromUri(uri), "0"};
        }

        return sTasksWithPriorityAndLabels.query(mOpenHelper.getReadableDatabase(),
                TASK_COLUMNS,
                updatedSelection,
                updatedSelectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Return all tasks marked complete.
     * @param uri the uri passed by the caller, contains the priority id.
     * @param projection the columnst to be returned.
     * @param selection the WHERE clause for the SQLite query.
     * @param selectionArgs the arguments for the WHERE clause (they replace the question marks).
     * @param sortOrder the order by value for the SQLite query.
     * @return a cursor containing the tasks.
     */
    private Cursor getTasksMarkedComplete(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String updatedSelection;
        String [] updatedSelectionArgs;
        if (selectionArgs != null) {
            updatedSelection = selection + " AND " +
                    ToDoContract.TaskEntry.COLUMN_IS_COMPLETED + " = 1";
            updatedSelectionArgs = new String[] { selectionArgs[0]};
        }
        else {
            updatedSelection = ToDoContract.TaskEntry.COLUMN_IS_COMPLETED + " = 1";
            updatedSelectionArgs = new String [] {};
        }

        return sTasksWithPriorityAndLabels.query(mOpenHelper.getReadableDatabase(),
                TASK_COLUMNS,
                updatedSelection,
                updatedSelectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Return the task with the id given in the Uri.
     * @param uri the uri passed by the caller, contains the priority id.
     * @param projection the columnst to be returned.
     * @param sortOrder the order by value for the SQLite query.
     * @return a cursor containing the tasks.
     */
    private Cursor getTaskWithId(Uri uri, String[] projection, String sortOrder) {
        String selection = ToDoContract.TaskEntry.TABLE_NAME + "." +
                ToDoContract.TaskEntry._ID + " = ?";
        String [] selectionArgs = new String [] { ToDoContract.TaskEntry.getTaskIdFromUri(uri) };
        return sTasksWithPriorityAndLabels.query(mOpenHelper.getReadableDatabase(),
                TASK_COLUMNS,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Set up the Uri matcher.
     * @return
     */
    static UriMatcher buildUriMatcher() {
        //The code passed into the constructor represents the code to return for the root URI
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ToDoContract.CONTENT_AUTHORITY;

        // Use the addURI function to match each of the types
        matcher.addURI(authority, ToDoContract.PATH_TASK, TASKS);
        matcher.addURI(authority, ToDoContract.PATH_TASK + "/priority/*", TASKS_WITH_PRIORITY);
        matcher.addURI(authority, ToDoContract.PATH_TASK + "/date/#", TASKS_AFTER_DATE);
        matcher.addURI(authority, ToDoContract.PATH_TASK + "/complete", TASKS_MARKED_COMPLETE);
        matcher.addURI(authority, ToDoContract.PATH_TASK + "/label/*", TASKS_WITH_LABEL);
        matcher.addURI(authority, ToDoContract.PATH_TASK + "/id/#", TASK_WITH_ID);

        matcher.addURI(authority, ToDoContract.PATH_LABEL, LABEL);
        matcher.addURI(authority, ToDoContract.PATH_LABEL + "/title/*", LABEL_WITH_TITLE);
        matcher.addURI(authority, ToDoContract.PATH_LABEL + "/id/#", LABEL_WITH_ID);

        matcher.addURI(authority, ToDoContract.PATH_PRIORITY, PRIORITY);
        matcher.addURI(authority, ToDoContract.PATH_PRIORITY + "/#", PRIORITY_WITH_ID);

        return matcher;
    }

    /**
     * Create the databse helper.
     * @return true to verify the function has completed.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new ToDoDbHelper(getContext());
        return true;
    }

    /**
     * Get the type returned for a given uri.
     * @param uri the uri whose type is to be determined.
     * @return the type of the uri.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return ToDoContract.TaskEntry.CONTENT_TYPE;
            case TASKS_WITH_PRIORITY:
                return ToDoContract.TaskEntry.CONTENT_TYPE;
            case TASKS_AFTER_DATE:
                return ToDoContract.TaskEntry.CONTENT_TYPE;
            case TASKS_MARKED_COMPLETE:
                return ToDoContract.TaskEntry.CONTENT_TYPE;
            case TASKS_WITH_LABEL:
                return ToDoContract.TaskEntry.CONTENT_TYPE;
            case TASK_WITH_ID:
                return ToDoContract.TaskEntry.CONTENT_ITEM_TYPE;
            case LABEL:
                return ToDoContract.TaskLabel.CONTENT_TYPE;
            case LABEL_WITH_TITLE:
                return ToDoContract.TaskLabel.CONTENT_ITEM_TYPE;
            case LABEL_WITH_ID:
                return ToDoContract.TaskLabel.CONTENT_ITEM_TYPE;
            case PRIORITY:
                return ToDoContract.TaskPriority.CONTENT_TYPE;
            case PRIORITY_WITH_ID:
                return ToDoContract.TaskPriority.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
    }

    /**
     * Determine which query to make based on the uri, and make the query.
     * @param uri the uri passed by the caller, contains the priority id.
     * @param projection the columnst to be returned.
     * @param selection the WHERE clause for the SQLite query.
     * @param selectionArgs the arguments for the WHERE clause (they replace the question marks).
     * @param sortOrder the order by value for the SQLite query.
     * @return a cursor containing the tasks.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)) {
            case TASKS:
            {
                retCursor = getTasks(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case TASKS_WITH_PRIORITY:
            {
                retCursor = getTasksWithPriority(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case TASKS_AFTER_DATE:
            {
                retCursor = getTasksBeforeDate(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case TASKS_WITH_LABEL:
            {
                retCursor = getTasksWithLabel(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case TASKS_MARKED_COMPLETE:
            {
                retCursor = getTasksMarkedComplete(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case TASK_WITH_ID:
            {
                retCursor = getTaskWithId(uri, projection, sortOrder);
                break;
            }
            case LABEL:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ToDoContract.TaskLabel.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LABEL_WITH_TITLE:
            {
                selection = ToDoContract.TaskLabel.COLUMN_LABEL + " = ?";
                selectionArgs = new String[] {ToDoContract.TaskLabel.getTitleFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ToDoContract.TaskLabel.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LABEL_WITH_ID:
            {
                selection = "_id = ?";
                selectionArgs = new String [] {ToDoContract.TaskLabel.getIdFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ToDoContract.TaskLabel.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PRIORITY:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ToDoContract.TaskPriority.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PRIORITY_WITH_ID:
            {
                selection = "_id = ?";
                selectionArgs = new String [] {ToDoContract.TaskPriority.getIdFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ToDoContract.TaskPriority.TABLE_NAME,
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

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    /**
     * Insert a record into the database.
     * @param uri the uri that defines which table is to be inserted.
     * @param contentValues the values to be inserted.
     * @return the id of the item inserted.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        // get a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TASKS: {
                long _id = db.insert(ToDoContract.TaskEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = ToDoContract.TaskEntry.buildTaskUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LABEL: {
                long _id = db.insert(ToDoContract.TaskLabel.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = ToDoContract.TaskLabel.buildLabelUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /**
     * Delete a value from the database.
     * @param uri the uri that defines which table will be deleted from.
     * @param selection the WHERE clause for the delete query.
     * @param selectionArgs the arguments for the where clause.
     * @return the number of rows deleted.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case TASKS:
                rowsDeleted = db.delete(
                        ToDoContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LABEL:
                rowsDeleted = db.delete(
                        ToDoContract.TaskLabel.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;    }

    /**
     * Update a row in the database.
     * @param uri the uri that defines which table will be updated.
     * @param values the values that will replace the old values.
     * @param selection the WHERE clause of the update.
     * @param selectionArgs the arguments to the WHERE clause.
     * @return the number of rows updated.
     */
    @Override
    public int update(
            @NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TASKS:
                rowsUpdated = db.update(ToDoContract.TaskEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LABEL:
                rowsUpdated = db.update(ToDoContract.TaskLabel.TABLE_NAME, values, selection,
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
}
