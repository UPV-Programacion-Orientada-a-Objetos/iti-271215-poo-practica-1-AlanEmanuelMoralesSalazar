package edu.upvictoria.fpoo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class commandsSQL {
    String path = null;
    public void createTable(String nameTable, String[] columns, String dataType, String Llave){
        File f = new File(path + "/" + nameTable + ".csv");
        try{
            if(!f.exists()){
                f.createNewFile();
            } else {
                throw new FileAlreadyExistsException("La tabla" + nameTable + "ya existe");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void select() {
        
    }
    public void insert() {

    }
    public void updateTable(){

    }
    public void dropTable() {

    }
    public void use(String path) throws RuntimeException{
        File arch = new File(path);
        if(!arch.exists()){
            throw new RuntimeException("File not found");
        } else{
            System.out.println("File found");
            this.path = path;
        }
    }
    public void showTable(){
        try{
            File directorio = new File(path);
            if(!directorio.exists()){
                throw new RuntimeException("No hay tablas creadas");
            } else {
                File[] files = directorio.listFiles();
                if(files != null){
                    for(File f : files){
                        if(f.isFile()) {
                            System.out.println(f.getName());
                        }
                    }
                } else{
                    System.out.println("No hay tablas creadas");
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public void commands(String line) throws IOException{

        //SELECT
        Pattern pat = Pattern.compile("(?i)\\bSELECT\\b.+?\\bFROM\\b.*?;", Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(line);
        while(mat.find()) {

        }
        //INSERT
        Pattern pat = Pattern.compile("^INSERT INTO//(* || [a-zA-Z]//FROM//$", Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(line);
        while(mat.find()) {

        }
        //DROP TABLE
        Pattern pat = Pattern.compile("^DROP TABLE//(* || [a-zA-Z]//FROM$", Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(line);
        while(mat.find()) {

        }
        //UPDATE
        Pattern pat = Pattern.compile("^UPDATE//(*||[a-zA-Z]//FROM$", Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(line);
        while(mat.find()) {

        }

    }
}

