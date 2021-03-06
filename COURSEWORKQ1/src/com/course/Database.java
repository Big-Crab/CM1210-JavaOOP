package com.course;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Database {

    private ArrayList<StudentInfo> studentList;
    private InfoReader readerWriter;
    private final String assetFolder = "./textdateien/";
    private String loadedFile = null;

    public Database() {
        readerWriter = new InfoReader();
        studentList = new ArrayList<>();
    }

    // loads student info to a list
    public void loadStudentInfo(){

    }

    public ArrayList<StudentInfo> getStudentList() {
        return studentList;
    }

    public void addStudent(String[] info) {
        StudentInfo student = new StudentInfo();
        try {
            student.setName(info[0]);
            student.setStudentID(info[1]);
            student.setCourseName(info[2]);
            student.setCourseID(info[3]);
            student.setHouseNumber(info[4]);
            student.setStreetName(info[5]);
            student.setTown(info[6]);
            student.setPostcode(info[7]);
            studentList.add(student);
        } catch (IOException e) {
            System.out.println("Invalid data entered; Student addition cancelled.");
        }
    }

    public void readFile(String file) {
        ArrayList<String> content = readerWriter.readFile(assetFolder + file);
        if(content != null) {
            studentList.clear();
            content.forEach(this::addToList);
            loadedFile = file;
        }
    }

    private void addToList(String line) {
        StudentInfo info = new StudentInfo();
        String[] collection = line.split("\\|");
        if(collection.length == 8) {
            try {
                info.setName(collection[0]);
                info.setStudentID(collection[1]);
                info.setCourseName(collection[2]);
                info.setCourseID(collection[3]);
                info.setHouseNumber(collection[4]);
                info.setStreetName(collection[5]);
                info.setTown(collection[6]);
                info.setPostcode(collection[7]);
                studentList.add(info);
            } catch (IOException e) {

            }
        } else {
            System.out.println("Error reading line; incorrect number of details interpreted.");
        }
    }

    public void saveFile() {
        // gets the "filename.txt" and splits it into "filename" and "txt" for the createFile method.
        createFile(loadedFile.split("\\.")[0], loadedFile.split("\\.")[1]);
    }

    public void createFile(String location, String format) {
        StringBuilder content = new StringBuilder();
        int j=0;
        for (StudentInfo student : studentList) {
            int i=0;
            for (StringBuffer buffer : student.getInfo()){
                content.append(buffer.toString() + ((i == student.getInfo().size()-1) ? "" : "|") );
                i++;
            }
            // Only put newline/CR char if one isn't on the final line of the file
            content.append((j==studentList.size()-1) ? "" : "\r\n");
            j++;
        }

        if(format.contains("bin")){
            try {
                Files.write(Paths.get(assetFolder + location + "." + format), content.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(format.contains("txt")) {
            OutputStream output = null;
            try {
                output = new BufferedOutputStream(new FileOutputStream(Paths.get(assetFolder + location + "." + format).toString(), false));
                output.write(content.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IO Error occurred when writing file.");
            } finally {
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Error saving file; invalid file type.");
        }
    }
}
