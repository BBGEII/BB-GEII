package com.example.jvf.robot2;

/**
 * Created by N87 on 21/01/2016.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;


public class DBHelper extends SQLiteOpenHelper {

    /*Table creation*/
    //SQLite need raw SQL query in order to create tables, that's what we're doing down there.
    //Every information are detailed, tables' name, the columns' name, and a are put together to build up the "CREATE TABLE" query.
    //Student's information:
    public static final String STUDENT_TABLE_NAME = "Student";
    public static final String STUDENT_KEY = "StudentId";
    public static final String STUDENT_NAME = "StudentName";
    public static final String STUDENT_MAIL = "StudentMail";

    //Task's information:
    public static final String TASK_TABLE_NAME = "Task";
    public static final String TASK_KEY = "TaskId";
    public static final String TASK_NAME = "TaskName";
    public static final String TASK_STUDENTID = "TaskStudentId";

    //TimeMode's information:
    public static final String TIMEMODE_TABLE_NAME = "TimeMode";
    public static final String TIMEMODE_KEY = "TimeModeId";
    public static final String TIMEMODE_TYPE = "ModeType";
    public static final String TIMEMODE_TIME = "TimeSpent";
    public static final String TIMEMODE_STUDENTID = "TimeModeStudentId";

    //Building up the "CREATE TABLE" query:
    private static final String STUDENT_TABLE_CREATE = "CREATE TABLE " + STUDENT_TABLE_NAME + " ("
            + STUDENT_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + STUDENT_NAME + " TEXT, "
            + STUDENT_MAIL + " TEXT);";

    private static final String TASK_TABLE_CREATE = "CREATE TABLE " + TASK_TABLE_NAME + " ("
            + TASK_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK_NAME + " TEXT, "
            + TASK_STUDENTID + " INTEGER);";

    private static final String TIMEMODE_TABLE_CREATE = "CREATE TABLE " + TIMEMODE_TABLE_NAME + " ("
            + TIMEMODE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TIMEMODE_TYPE + " TEXT, "
            + TIMEMODE_TIME + " INTEGER, "
            + TIMEMODE_STUDENTID + " INTEGER);";
    /*----------*/

    public DBHelper(Context context, String DB_Name, CursorFactory cursor, int BDD_Version){
        //Mother's constructor calling, resulting in the creation of the database.
        super(context, DB_Name, cursor, BDD_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creation of our three tables thanks to the "execute SQL" method.
        db.execSQL(STUDENT_TABLE_CREATE);
        db.execSQL(TASK_TABLE_CREATE);
        db.execSQL(TIMEMODE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This method is used to drop all the tables of the current database, and create them back.
        //We use it has a reset but is made to get different versions of a single database.
        dropTable(db, STUDENT_TABLE_NAME);
        dropTable(db, TASK_TABLE_NAME);
        dropTable(db, TIMEMODE_TABLE_NAME);
        this.onCreate(db);                  //Calling onCreate() of this class to create the tables back.
    }

    public void dropTable(SQLiteDatabase db, String TableName){
        //This method drop the given table from the given database
        db.execSQL("DROP TABLE IF EXISTS " + TableName + ";");
    }

}
