package com.todo.group1.todo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.todo.group1.todo.R;
import com.todo.group1.todo.data.ToDoContract.TaskEntry;
import com.todo.group1.todo.data.ToDoContract.TaskLabel;
import com.todo.group1.todo.data.ToDoContract.TaskPriority;


/**
 * Created by Justin Banks on 7/23/16.
 * Manages a local database for tasks
 */
public class ToDoDbHelper extends SQLiteOpenHelper {

    // must be incremented upon schema change
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "todo.db";

    private final Context mContext;

    public ToDoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Create a new dataase. This creates all tables and inserts values when needed.
     * @param sqLiteDatabase the database to be created. Passed by the system.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold our labels
        final String SQL_CREATE_LABEL_TABLE = "CREATE TABLE " + TaskLabel.TABLE_NAME + " (" +
                TaskLabel._ID + " INTEGER PRIMARY KEY," +
                TaskLabel.COLUMN_LABEL + " TEXT NOT NULL " +
                " );";

        // Create a table to hold our priorities
        final String SQL_CREATE_PRIORITY_TABLE = "CREATE TABLE " + TaskPriority.TABLE_NAME + " (" +
                TaskPriority._ID + " INTEGER PRIMARY KEY," +
                TaskPriority.COLUMN_PRIORITY + " TEXT NOT NULL " +
                " );";

        final String SQL_INSERT_PRIORITY_NONE = "INSERT INTO " + TaskPriority.TABLE_NAME +
                " (" + TaskPriority.COLUMN_PRIORITY + ") VALUES ('"
                + mContext.getString(R.string.priority_none) + "');" ;

        final String SQL_INSERT_PRIORITY_LOW = "INSERT INTO " + TaskPriority.TABLE_NAME +
                " (" + TaskPriority.COLUMN_PRIORITY + ") VALUES ('"
                + mContext.getString(R.string.priority_low) + "');" ;

        final String SQL_INSERT_PRIORITY_MED = "INSERT INTO " + TaskPriority.TABLE_NAME +
                " (" + TaskPriority.COLUMN_PRIORITY + ") VALUES ('"
                + mContext.getString(R.string.priority_med) + "');" ;

        final String SQL_INSERT_PRIORITY_HIGH = "INSERT INTO " + TaskPriority.TABLE_NAME +
                " (" + TaskPriority.COLUMN_PRIORITY + ") VALUES ('"
                + mContext.getString(R.string.priority_high) + "');" ;

        final String SQL_INSERT_LABEL_BLANK = "INSERT INTO " + TaskLabel.TABLE_NAME +
                " (" + TaskLabel.COLUMN_LABEL + ") VALUES ('');" ;

        // Create a table to hold our tasks
        final String SQL_CREATE_TASK_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                TaskEntry._ID + " INTEGER PRIMARY KEY," +
                TaskEntry.COLUMN_CREATE_DATE + " INTEGER NOT NULL," +
                TaskEntry.COLUMN_DUE_DATE + " INTEGER," +
                TaskEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                TaskEntry.COLUMN_DETAIL + " TEXT," +
                TaskEntry.COLUMN_IS_COMPLETED + " INTEGER NOT NULL," +
                TaskEntry.COLUMN_IS_DELETED + " INTEGER NOT NULL," +
                TaskEntry.COLUMN_REMINDER_ADDED + " INTEGER," +
                TaskEntry.COLUMN_LABEL_ID + " INTEGER," +
                TaskEntry.COLUMN_PRIORITY_ID + " INTEGER," +
                TaskEntry.COLUMN_PARENT_TASK_ID + " INTEGER," +

                // Set up the priority column as a foreign key to the priority table
                " FOREIGN KEY (" + TaskEntry.COLUMN_PRIORITY_ID + ") REFERENCES " +
                TaskPriority.TABLE_NAME + "(" + TaskPriority._ID + "), " +

                // Set up label column as a foreign key to the label table
                " FOREIGN KEY (" + TaskEntry.COLUMN_LABEL_ID + ") REFERENCES " +
                TaskLabel.TABLE_NAME + "(" + TaskLabel._ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_LABEL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PRIORITY_TABLE);
        sqLiteDatabase.execSQL(SQL_INSERT_PRIORITY_NONE);
        sqLiteDatabase.execSQL(SQL_INSERT_PRIORITY_LOW);
        sqLiteDatabase.execSQL(SQL_INSERT_PRIORITY_MED);
        sqLiteDatabase.execSQL(SQL_INSERT_PRIORITY_HIGH);
        sqLiteDatabase.execSQL(SQL_INSERT_LABEL_BLANK);
        sqLiteDatabase.execSQL(SQL_CREATE_TASK_TABLE);
    }

    /**
     * This function is ran if the DATABASE_VERSION has changed (if the schema changes.)
     * @param sqLiteDatabase the database.
     * @param i the new schema.
     * @param i1 passed by the system.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
}
