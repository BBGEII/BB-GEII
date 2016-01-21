package com.example.jvf.robot2;

/**
 * Created by N87 on 21/01/2016.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/*
For reasons of simplicity, we only have one activity for managing the database, the content view is
changed according to what menu the user wants to in.
*/

public class Interface extends AppCompatActivity {

    /*----------*/
    //Prefix Et_ refers to EditText.
    //Prefix Bt_ refers to Button.
    //Prefix Sp_ refers to a Spinner.
    //Prefix Cb_ refers to a CheckBox.

    //Main views
    private Spinner Sp_Select;
    private Spinner Sp_Result;
    private CheckBox Cb_All;
    private EditText Et_Select;

    //Student's views
    private EditText Et_StudentName;
    private EditText Et_StudentMail;
    private EditText Et_StudentID;
    private Button Bt_AddStudent;
    private Button Bt_DelStudent;

    //Task's views
    private EditText Et_TaskName;
    private EditText Et_TaskStudName;
    private Button Bt_AddTask;
    private Button Bt_DelTask;

    //TimeMode's views
    private Spinner Sp_SelectMode;
    private Button Bt_AddTimeMode;
    private EditText Et_TMStudName;
    private EditText Et_TMTime;
    /*----------*/

    private DB_DAO MyDb;            //Database

    android.support.v7.app.ActionBar menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface);
        //Display the logo on the ActionBar
        menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setDisplayShowTitleEnabled(false);
        menu.setDisplayHomeAsUpEnabled(false);

        //Creating and opening the database.
        MyDb = new DB_DAO(this);
        MyDb.open();
    }

    /*  OnClick methods */
    //----------Common----------
    public void ClearSelected(View view){
        //This method is called by every single EditText, in order to clear them.
        ((EditText)view).setText("");

        //It also enables the affiliated buttons.
        //Buttons were disabled to prevent the user from adding default EditText's values to the database.
        //Some could not exist yet, returning a null pointer exception.
        try {
            Bt_AddStudent.setEnabled(true);
            Bt_DelStudent.setEnabled(true);
        }catch(NullPointerException e){}
        try {
            Bt_AddTask.setEnabled(true);
            Bt_DelTask.setEnabled(true);
        }catch(NullPointerException e){}
        try{
            Bt_AddTimeMode.setEnabled(true);
        }catch(NullPointerException e){}
    }
    public void Return(View view){
        //Set the main interface's content view back in place when the "Return" button is pressed.
        setContentView(R.layout.activity_interface);
    }
    public void Upgrade(View view){
        //Used to drop and recreate all tables.
        MyDb.upgradeTable();
        Toast.makeText(this, "All tables are dropped", Toast.LENGTH_SHORT).show();
    }

    //----------Query----------
    public void ManageQuery(View view){
        //Setting the new content view up when entering the query menu.
        setContentView(R.layout.activity_query);
        Sp_Select = (Spinner) findViewById(R.id.Sp_Select);
        Sp_Result = (Spinner) findViewById(R.id.Sp_Result);
        Et_Select = (EditText) findViewById(R.id.Et_SelectName);
        Cb_All = (CheckBox) findViewById(R.id.Cb_All);

        //We are using a lot of spinners in this application, they really suit our needs.
        //The first spinner is a list that show different tables we can query in the database.
        List<String> SelectList = new ArrayList<>();
        SelectList.add("Student");
        SelectList.add("Task");
        SelectList.add("TimeMode");
        //The list itself can't be directly put in a spinner, it needs to be adapted.
        //Android holds a class that do the adapting job.
        ArrayAdapter<String> DataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, SelectList);
        DataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sp_Select.setAdapter(DataAdapter);

    }
    public void HideInput(View view){
        //When the check box is checked, it means that the user wants to get all the data of what he chose in the spinner.
        //Therefore, the EditText affiliated to the query parameter can be disabled.
        if(((CheckBox)view).isChecked())
            Et_Select.setEnabled(false);
        else
            Et_Select.setEnabled(true);
    }


    /* Disclaimer: Any brain burning due to a reading of the following lines isn't our responsibility.
    Good luck, and may the force be with you. */
    public void QueryOn(View view){
        //This method is designed to handle all query available, and display the result.
        //The result of the query is displayed on a spinner, and all our query methods return List, we'll then need a lot of Lists.
        List<String> ResultList = new ArrayList<>();            //What will be displayed on the spinner.
        List<Student> StudentList = new ArrayList<>();          //getStudent methods' result.
        List<Task> TaskList = new ArrayList<>();                //getTask methods' result.
        List<TimeMode> TimeModeList = new ArrayList<>();        //getTimeMode methods' result.

        //The table the user wants to query is get thanks to the first spinner.
        String sSelection = Sp_Select.getSelectedItem().toString();

        //According to what the user chose, different methods are applied.
        switch (sSelection){
            case "Student":
                //The user can either choose to get all students, or a certain one, knowing his name.
                if(Cb_All.isChecked())
                    StudentList = MyDb.getAllStudents();
                else
                    StudentList = MyDb.getStudentByName(Et_Select.getText().toString());
                //The number of students displayed is as big as the number of rows returned by the query.
                if(StudentList != null) {
                    for (int i = 0; i < StudentList.size(); i++) {
                        //Students are added one by one in a list (the growing spinner).
                        ResultList.add(StudentList.get(i).getStudentName() + " - " + StudentList.get(i).getStudentMail() + " - ID: " + StudentList.get(i).getStudentId());
                    }
                }else
                    Toast.makeText(this, "Query has returned no result", Toast.LENGTH_SHORT).show();
                break;

            case "Task":
                //Works like the previous case, but has little differences.
                if(Cb_All.isChecked())
                    TaskList = MyDb.getAllTasks();
                else {
                    TaskList = MyDb.getTaskByName(Et_Select.getText().toString());
                }
                if(TaskList != null) {
                    //We can see thanks the dependencies diagram that a task is affiliated to one or multiple students;
                    //querying the Task Table also means querying the Student Table, and that's why we need a StudentList.
                    for (int i = 0; i < TaskList.size(); i++) {
                        List<Student> TempStud = MyDb.getStudentByID(TaskList.get(i).getStudentId());
                        //if no student is found for the peculiar task, the user is informed thanks to a Toast.
                        /*Careful, a hungry user would try to do as many unsuccessful query as possible to get a lot of Toasts*/
                        if(TempStud != null) {
                            ResultList.add(TempStud.get(0).getStudentName() + " worked on " + TaskList.get(i).getTaskName() /*+ " " + String.valueOf(TaskList.get(i).getTaskId())*/);
                        }else {
                            Toast.makeText(this, "Error, can't link task to student", Toast.LENGTH_SHORT);
                            break;
                        }
                    }
                }else
                    Toast.makeText(this, "Query has returned no result", Toast.LENGTH_SHORT).show();
                break;

            case "TimeMode":
                //If you understood the two previous cases, you don't need explanations for this one.
                //Otherwise be brave and try again.
                if(Cb_All.isChecked())
                    TimeModeList = MyDb.getAllTimeMode();
                else
                    TimeModeList = MyDb.getTimeModeByModeType(Et_Select.getText().toString());
                if(TimeModeList != null) {
                    for (int i = 0; i < TimeModeList.size(); i++) {
                        List<Student> TempStud = MyDb.getStudentByID(TimeModeList.get(i).getStudentId());
                        if(TempStud != null) {
                            ResultList.add(TempStud.get(0).getStudentName() + " spent " + TimeModeList.get(i).getTimeSpent() + " mn on " + TimeModeList.get(i).getTypeMode());
                        }
                    }
                }else
                    Toast.makeText(this, "Query has returned no result", Toast.LENGTH_SHORT).show();
                break;
            default:
                //If, for a dark reason, no table to query is selected, this Toast will appear.
                Toast.makeText(this, "Error, can't query", Toast.LENGTH_SHORT).show();
                break;
        }
        //Finally, now that we're done filling up our list, it's time to adapt it and display it in the spinner.
        ArrayAdapter<String> DataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ResultList);
        DataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sp_Result.setAdapter(DataAdapter);
    }

    //----------Student's----------
    public void ManageStudent(View view){
        //Entering the Student's managing menu will trigger this method and set the right content view up.
        //This method is also called by the "Clear" button in order to reset the display by default.
        setContentView(R.layout.activity_student);
        Et_StudentName = (EditText) findViewById(R.id.Et_StudentName);
        Et_StudentMail = (EditText) findViewById(R.id.Et_StudentMail);
        Et_StudentID = (EditText) findViewById(R.id.Et_StudentId);
        Bt_AddStudent = (Button) findViewById(R.id.Bt_AddStudent);
        Bt_DelStudent = (Button) findViewById(R.id.Bt_DelStudent);

        Et_StudentName.setText("Enter name here");
        Et_StudentMail.setText("Enter e-mail address here");
        Et_StudentID.setText("Enter ID here");
        Bt_AddStudent.setEnabled(false);
        Bt_DelStudent.setEnabled(false);
    }
    public void AddStudent(View view){
        //We get the user's input (in EditText) to add a new student.
        String sName = Et_StudentName.getText().toString();
        String sMail = Et_StudentMail.getText().toString();

        //Really light security on the user's input, we don't want the database to be filled with empty names and e-mail addresses.
        if((sName.length() > 1)&&((sMail.length() > 4))){
            Student TempStudent = new Student(sName, sMail);
            long lID = MyDb.AddStudent(TempStudent);
            //disabling the "Add Student" button will prevent the database from being filled with the same student.
            ((Button)view).setEnabled(false);
            Toast.makeText(this, "Student added", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, String.valueOf(lID), Toast.LENGTH_SHORT).show(); //Debugging toast (yum yum !)
        }else{
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        }

    }
    public void DelStudent(View view){
        String sName = Et_StudentName.getText().toString();
        MyDb.deleteStudentByName(sName);
        //How can't you understand this ?
    }

    //----------Task's----------
    public void ManageTask(View view){
        //As what we did with the ManageStudent() method, here we set up the right content view, disable the buttons, etc ...
        setContentView(R.layout.activity_task);
        Et_TaskName = (EditText) findViewById(R.id.Et_TaskName);
        Et_TaskStudName = (EditText) findViewById(R.id.Et_TaskStudName);
        Bt_AddTask = (Button) findViewById(R.id.Bt_AddTask);
        Bt_DelTask = (Button) findViewById(R.id.Bt_DelTask);

        Et_TaskName.setText("Enter task name here");
        Et_TaskStudName.setText("Enter student's name here");
        Bt_AddTask.setEnabled(false);
        Bt_DelTask.setEnabled(false);
    }
    public void AddTask(View view){
        //Roughly works like AddStudent(), with a little difference.
        //Since tasks are linked to student, we need to query the database with the given student name to get his ID.
        String sTaskName = Et_TaskName.getText().toString();
        String sTaskStudName = Et_TaskStudName.getText().toString();
        if((sTaskName.length() > 3)&&(sTaskStudName.length() > 1)) {
            List<Student> TempStudent = MyDb.getStudentByName(sTaskStudName);
            //If no student has been found, then the task can't be added.
            if (TempStudent != null) {
                Task TempTask = new Task(TempStudent.get(0).getStudentId(), sTaskName);
                long lID = MyDb.AddTask(TempTask);
                ((Button)view).setEnabled(false);
                Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error, non existing student", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Error, invalid input", Toast.LENGTH_SHORT).show();
        }
    }
    public void DelTask(View view){
        //See DelStudent() for complete explanations.
        String sTaskName = Et_TaskName.getText().toString();
        MyDb.deleteTaskByName(sTaskName);
    }

    //---------TimeMode's---------
    public void ManageTimeMode(View view){
        //Again, we set up the content view, disable buttons, etc.
        setContentView(R.layout.activity_timemode);
        Bt_AddTimeMode = (Button) findViewById(R.id.Bt_AddTimeMode);
        Sp_SelectMode = (Spinner) findViewById(R.id.Sp_SelectMode);
        Et_TMStudName = (EditText) findViewById(R.id.Et_TMStudName);
        Et_TMTime = (EditText) findViewById(R.id.Et_TMTime);
        Sp_SelectMode = (Spinner) findViewById(R.id.Sp_SelectMode);

        //TimeModes are related to the different modes, which one can be selected thanks to (another) spinner.
        List<String> ModeList = new ArrayList<>();
        ModeList.add("Accelerometer");
        ModeList.add("Joystick");
        ArrayAdapter<String> DataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ModeList);
        DataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sp_SelectMode.setAdapter(DataAdapter);

        Et_TMStudName.setText("Enter name here");
        Et_TMTime.setText("0");
        Bt_AddTimeMode.setEnabled(false);
    }
    public void AddTimeMode(View view){
        //See AddTask() and AddStudent() for explanations.
        String sTMStudName = Et_TMStudName.getText().toString();
        String sTimeSpent = Et_TMTime.getText().toString();
        if((sTMStudName.length() > 1)&&(sTimeSpent.length() > 0)){
            long lTimeSpent = Long.valueOf(sTimeSpent);
            List<Student> TempStudent = MyDb.getStudentByName(sTMStudName);
            if(TempStudent != null){
                TimeMode TempTimeMode = new TimeMode(TempStudent.get(0).getStudentId(), lTimeSpent, Sp_SelectMode.getSelectedItem().toString());
                long lID = MyDb.AddTimeMode(TempTimeMode);
                ((Button)view).setEnabled(false);
                Toast.makeText(this, "TimeMode added", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Error, non existing student", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Error, invalid input", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_close:

                this.finish();
                return true;
            case R.id.action_credits:
                Intent myIntent = new Intent(this,Credits.class);
                startActivityForResult(myIntent, 0);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}