package com.example.jvf.robot2;

/**
 * Created by N87 on 21/01/2016.
 */
public class Student {

    //declaration

    private long StudentId;     //Primary key
    private String StudentName;
    private String StudentMail;

    public Student(){}

    public Student(String StudentName, String StudentMail){   //Student's Constructor.
        //Initializing the new Student's data thanks to the constructor's input parameters.
        this.StudentName = StudentName;
        this.StudentMail = StudentMail;
    }

    //Accessors are used to communicate with other classes.

    public long getStudentId(){
        return StudentId;
    }

    public void setStudentId(long in_StudentId) {
        this.StudentId = in_StudentId;
    }

    public String getStudentName(){
        return StudentName;
    }

    public void setStudentName(String in_StudentName){
        this.StudentName = in_StudentName;
    }

    public String getStudentMail(){
        return StudentMail;
    }

    public void setStudentMail(String in_StudentMail){
        this.StudentMail = in_StudentMail;
    }
    
}
