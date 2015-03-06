package com.interview.design.pattern.j2ee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午6:22
 *
 * Data Access Object Pattern or DAO pattern is used to separate low level data accessing API
 * or operations from high level business services.
 *
 * Following are the participants in Data Access Object Pattern.
 * Data Access Object Interface - This interface defines the standard operations to be performed on a model object(s).
 * Data Access Object concrete class -This class implements above interface.
 *                                    This class is responsible to get data from a datasource which can be database or xml
 *                                    or any other storage mechanism.
 * Model Object or Value Object - This object is simple POJO containing get/set methods to store data retrieved using DAO class.
 */
public class DataAccessObjectPattern {

    static class Student {         //Model Object
        private String name;
        private int rollNo;

        Student(String name, int rollNo){
            this.name = name;
            this.rollNo = rollNo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRollNo() {
            return rollNo;
        }

        public void setRollNo(int rollNo) {
            this.rollNo = rollNo;
        }
    }

    static interface StudentDao {
        public List<Student> getAllStudents();
        public Student getStudent(int rollNo);
        public void updateStudent(Student student);
        public void deleteStudent(Student student);
    }

    static class StudentDaoImpl implements StudentDao {

        //list is working as a database
        List<Student> students;

        public StudentDaoImpl(){
            students = new ArrayList<Student>();
            Student student1 = new Student("Robert",0);
            Student student2 = new Student("John",1);
            students.add(student1);
            students.add(student2);
        }
        @Override
        public void deleteStudent(Student student) {
            students.remove(student.getRollNo());
            System.out.println("Student: Roll No " + student.getRollNo()
                    +", deleted from database");
        }

        //retrive list of students from the database
        @Override
        public List<Student> getAllStudents() {
            return students;
        }

        @Override
        public Student getStudent(int rollNo) {
            return students.get(rollNo);
        }

        @Override
        public void updateStudent(Student student) {
            students.get(student.getRollNo()).setName(student.getName());
            System.out.println("Student: Roll No " + student.getRollNo()
                    +", updated in the database");
        }
    }

    public static void main(String[] args) {
        StudentDao studentDao = new StudentDaoImpl();

        //print all students
        for (Student student : studentDao.getAllStudents()) {
            System.out.println("Student: [RollNo : "
                    +student.getRollNo()+", Name : "+student.getName()+" ]");
        }


        //update student
        Student student =studentDao.getAllStudents().get(0);
        student.setName("Michael");
        studentDao.updateStudent(student);

        //get the student
        studentDao.getStudent(0);
        System.out.println("Student: [RollNo : "
                +student.getRollNo()+", Name : "+student.getName()+" ]");
    }
}
