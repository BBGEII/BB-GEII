package com.example.jvf.robot2;

/**
 * Created by N87 on 21/01/2016.
 */
public class Task {

    //declaration

    private long TaskId;    //Primary key
    private long StudentId;
    private String TaskName;


    public Task() {
    }

    public Task(long StudentId, String TaskName) {   //Task's Constructor.
        //Initializing the new Student's data thanks to the constructor's input parameters.
        this.StudentId = StudentId;
        this.TaskName = TaskName;
    }

    //Accessors are used to communicate with other classes.

    public long getTaskId() {
        return TaskId;
    }

    public void setTaskId(long in_TaskId) {
        this.TaskId = in_TaskId;
    }

    public long getStudentId() {
        return StudentId;
    }

    public void setStudentId(long in_StudentId) {
        this.StudentId = in_StudentId;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String in_TaskName) {
        this.TaskName = in_TaskName;
    }

}