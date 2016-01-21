package com.example.jvf.robot2;

/**
 * Created by N87 on 21/01/2016.
 */
public class TimeMode {
    //declaration

    private long TimeModeId;  //Primary key
    private long StudentId;
    private long TimeSpent;
    private String TypeMode;


    public TimeMode() {}

    public TimeMode(long StudentId, long TimeMode , String TypeMode) {   //TimeMode's Constructor.
        //Initializing the new Student's data thanks to the constructor's input parameters.
        this.StudentId = StudentId;
        this.TimeSpent = TimeMode;
        this.TypeMode = TypeMode;
    }

    //Accessors are used to communicate with other classes.

    public long getTimeModeId() {
        return TimeModeId;
    }

    public void setTimeModeId(long in_TimeModeId) {
        this.TimeModeId = in_TimeModeId;
    }

    public long getStudentId() {
        return StudentId;
    }

    public void setStudentId(long in_StudentId) {
        this.StudentId = in_StudentId;
    }

    public long getTimeSpent() {
        return TimeSpent;
    }

    public void setTimeSpent(long in_TimeMode) {
        this.TimeSpent = in_TimeMode;
    }

    public String getTypeMode() {
        return TypeMode;
    }

    public void setTypeMode(String in_TypeMode) {
        this.TypeMode = in_TypeMode;
    }

}

