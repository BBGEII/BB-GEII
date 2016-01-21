package com.example.jvf.robot2;

/**
 * Created by N87 on 21/01/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DB_DAO {


    private static final String DATABASE_NAME = "Student";
    private static final int Version_Db = 1;    //Database version (we only use one)

    private SQLiteDatabase MyDb;                //Database
    private DBHelper MyDbHelper;                //Database helper

    public DB_DAO(){}

    public DB_DAO(Context context){
        //We create the database and all tables
        MyDbHelper = new DBHelper(context,DATABASE_NAME, null, Version_Db);
    }

    public void open(){
        //We open the database and make it writable
        MyDb = MyDbHelper.getWritableDatabase();
    }
    public void close(){
        //We close access to the database
        MyDb.close();
    }
    public SQLiteDatabase getDb(){
        return MyDb;
    }
    public void upgradeTable(){
        //Call the onUpgrade() method from the DBHelper, in order to drop and recreate all tables
        MyDbHelper.onUpgrade(MyDb, 0, 0);
    }

    public long AddStudent(Student student){
        //We create a Content Value in which we put the new student's data
        //The column in which the data goes and the data itself are "paired"
        ContentValues values = new ContentValues();
        values.put(DBHelper.STUDENT_NAME, student.getStudentName());
        values.put(DBHelper.STUDENT_MAIL, student.getStudentMail());

        //then we insert the Content Value in the database
        long iD = MyDb.insert(DBHelper.STUDENT_TABLE_NAME, null, values);
        // if iD = -1 an error occurred, if not, the 'long' value is the KeyID of the inserted values
        if (iD != -1) {
            student.setStudentId(iD);
        }
        return iD;
    }
    public long AddTask(Task task){
        //Same as what we did in AddStudent(), with different parameters
        ContentValues values = new ContentValues();
        values.put(DBHelper.TASK_NAME, task.getTaskName());
        values.put(DBHelper.TASK_STUDENTID, task.getStudentId());

        long iD = MyDb.insert(DBHelper.TASK_TABLE_NAME, null, values);
        if(iD != -1) {
            task.setTaskId(iD);
        }
        return iD;
    }
    public long AddTimeMode(TimeMode timemode){
        //See AddStudent() for explanation, these methods work the same
        ContentValues values = new ContentValues();
        values.put(DBHelper.TIMEMODE_STUDENTID, timemode.getStudentId());
        values.put(DBHelper.TIMEMODE_TIME, timemode.getTimeSpent());
        values.put(DBHelper.TIMEMODE_TYPE, timemode.getTypeMode());

        long iD = MyDb.insert(DBHelper.TIMEMODE_TABLE_NAME, null, values);
        if(iD != -1){
            timemode.setTimeModeId(iD);
        }
        return iD;
    }

    public int deleteStudentByName(String sStudentName){
        //A table of strings holds the arguments of the SQL WHERE clause
        String[] args = {sStudentName};
        //DATABASE.delete() returns the number of rows deleted. It takes the table name, the condition (here columnName = input), and arguments.
        return MyDb.delete(DBHelper.STUDENT_TABLE_NAME, DBHelper.STUDENT_NAME + " = ?", args);
    }
    public int deleteTaskByName(String sTaskName){
        //Check deleteStudentByName for explanation
        String[] args = {sTaskName};
        return MyDb.delete(DBHelper.TASK_TABLE_NAME, DBHelper.TASK_NAME + " = ?", args);
    }

    //unused method.(Nobody can erase time !!!)
    /*public int deleteTimeModeByStudentId(long StudentId){
        String[] args = {String.valueOf(StudentId)};
        return MyDb.delete(DBHelper.TASK_TABLE_NAME, DBHelper.TIMEMODE_STUDENTID + " = ?", args);
    }*/

    public List<Student> getStudentByName(String sStudentName){
        //We query the database, thanks to the rawQuery() method, that returns a Cursor as a result.
        //With a little knowledge in SQL, rawQuery() is really simple to use, and is quite efficient.
        //We will then need to convert this cursor into a more suitable class, here, a Student.
        String[] args = {sStudentName};
        Cursor curs = MyDb.rawQuery("SELECT * FROM " + DBHelper.STUDENT_TABLE_NAME + " WHERE "+ DBHelper.STUDENT_NAME + " = ?", args);
        return CursorToStudent(curs);   //Explanation below
    }
    public List<Task> getTaskByName(String sTaskName){
        //See getStudentByName() for explanation.
        String[] args = {sTaskName};
        Cursor curs = MyDb.rawQuery("SELECT * FROM " + DBHelper.TASK_TABLE_NAME + " WHERE " + DBHelper.TASK_NAME + " = ?", args);
        return CursorToTask(curs);
    }
    public List<TimeMode> getTimeModeByModeType(String sModeType){
        //See getStudentByName() for explanations.
        String[] args = {sModeType};
        Cursor curs = MyDb.rawQuery("SELECT * FROM " + DBHelper.TIMEMODE_TABLE_NAME + " WHERE " + DBHelper.TIMEMODE_TYPE + " = ?", args);
        return CursorToTimeMode(curs);
    }
    public List<Student> getStudentByID(long iD){
        //Works like getStudentByName(), but uses different parameters and arguments.
        String[] args = {String.valueOf(iD)};
        Cursor curs = MyDb.rawQuery("SELECT * FROM " + DBHelper.STUDENT_TABLE_NAME + " Where " + DBHelper.STUDENT_KEY + " = ?", args);
        return CursorToStudent(curs);
    }

    public List<Student> getAllStudents(){
        //Basically query the database to get all students.
        //Returns a List of Student whether there's one or several results.
        Cursor curs = MyDb.rawQuery("SELECT * FROM " + DBHelper.STUDENT_TABLE_NAME, null);
        return CursorToStudent(curs);
    }
    public List<Task> getAllTasks(){
        //See getAllStudents() for explanations.
        Cursor curs = MyDb.rawQuery("SELECT * FROM " + DBHelper.TASK_TABLE_NAME, null);
        return CursorToTask(curs);
    }
    public List<TimeMode> getAllTimeMode(){
        //Go to getAllStudent() for explanations.
        Cursor curs = MyDb.rawQuery("SELECT * FROM " + DBHelper.TIMEMODE_TABLE_NAME, null);
        return CursorToTimeMode(curs);
    }


    //This method converts a cursor into a List of Student, no matter the number of students.
    public List<Student> CursorToStudent(Cursor curs){
        //The query can return several answers, so we use a List to store the data.
        List<Student> ListStudent = new ArrayList<>();

        // If the query didn't answer anything, it returns null, so we don't bother doing any conversion.
        if(curs.getCount() == 0){
            return null;
        }else{
            //The first usable data is at the cursor's first position.
            curs.moveToFirst();
            for(int i = 0; i < curs.getCount(); i++) {
                // cursor's content is converted into a " temporary " Student. The student is emptied at each iteration.
                Student TempStudent = new Student();

                //We find the right data thanks to its column name in the table (e.g DBHELPER.STUDENT_NAME for the student name)
                TempStudent.setStudentName(curs.getString(curs.getColumnIndex(DBHelper.STUDENT_NAME)));
                TempStudent.setStudentMail(curs.getString(curs.getColumnIndex(DBHelper.STUDENT_MAIL)));
                TempStudent.setStudentId(curs.getLong(0));
                //Be careful, these lines can generate an exception when they doesn't exists (e.g only 1 column returned from query),
                //so you probably need to catch exception if you plan to not get all columns back from the SQL query

                ListStudent.add(TempStudent);   //Once the temporary student has its attributes filled, it is added to the list.
                curs.moveToNext();              //Move to the next cursor's row.
            }
            curs.close();                       //Destroy the cursor.
            return ListStudent;                 //The student list is finally ready to be returned
        }
    }

    //This method works as the method CursorToStudent, but is designed for a Task
    public List<Task> CursorToTask(Cursor curs){
        //Works like CursorToStudent(), go check the above method.
        List<Task> TaskList = new ArrayList<>();
        if(curs.getCount() == 0)
            return null;
        else{
            curs.moveToFirst();
            for(int i = 0; i < curs.getCount(); i++){
                Task TempTask = new Task();
                TempTask.setTaskName(curs.getString(curs.getColumnIndex(DBHelper.TASK_NAME)));
                TempTask.setStudentId(curs.getLong(curs.getColumnIndex(DBHelper.TASK_STUDENTID)));
                TempTask.setTaskId(curs.getLong(curs.getColumnIndex(DBHelper.TASK_KEY)));
                TaskList.add(TempTask);
                curs.moveToNext();
            }
            curs.close();
            return TaskList;
        }
    }

    //This method converts a cursor into a list of TimeMode
    public List<TimeMode> CursorToTimeMode(Cursor curs){
        //See CursorToStudent() for explanations
        List<TimeMode> TimeModeList = new ArrayList<>();
        if(curs.getCount() == 0)
            return null;
        else{
            curs.moveToFirst();
            for(int i = 0; i < curs.getCount(); i++){
                TimeMode TempTimeMode = new TimeMode();
                TempTimeMode.setTimeSpent(curs.getLong(curs.getColumnIndex(DBHelper.TIMEMODE_TIME)));
                TempTimeMode.setStudentId(curs.getLong(curs.getColumnIndex(DBHelper.TIMEMODE_STUDENTID)));
                TempTimeMode.setTimeModeId(curs.getLong(curs.getColumnIndex(DBHelper.TIMEMODE_KEY)));
                TempTimeMode.setTypeMode(curs.getString(curs.getColumnIndex(DBHelper.TIMEMODE_TYPE)));

                TimeModeList.add(TempTimeMode);
                curs.moveToNext();
            }
            curs.close();
            return TimeModeList;
        }
    }


}


