package edu.upvictoria.fpoo;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class commandsSQL {
    String path = null;
    public void createTable(String nameTable, String columns){
        File f = new File(path + "/" + nameTable + ".csv");
        try{
            if(!f.exists()){
                f.createNewFile();
            } else {
                throw new FileAlreadyExistsException("La tabla " + nameTable + " ya existe");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void select(String nameTable) {

    }
    public void insert() {

    }
    public void updateTable(){

    }
    public void dropTable(String nameTable) {

    }
    public void use(String path) throws RuntimeException{
        File arch = new File(path);
        if(!arch.exists()){
            throw new RuntimeException("Ruta no encontrada");
        } else{
            System.out.println("Ruta encontrada: " + path);
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
            throw new RuntimeException("Error en la lectura del archivo");
        }
    }
    public void commands(String line) throws IOException{
        try {
            Matcher mat;
            //SELECT
            if ((mat = Pattern.compile("SELECT\\s+\\*\\s+FROM (.+);$", Pattern.CASE_INSENSITIVE).matcher(line)).find()) {
                String nameTable = mat.group(1);
                System.out.println(mat.group(1));
                select(nameTable);
            }
            //CREATE TABLE
            if ((mat = Pattern.compile("\\bCREATE TABLE\\b\\s*(\\w+)\\s*\\((.*?)\\)\\s*;", Pattern.CASE_INSENSITIVE).matcher(line)).find()) {
                String nameTable = mat.group(1);
                String columns = mat.group(2);
                System.out.println(mat.group(2));
                createTable(nameTable, columns);
            }
            //SHOW TABLE
            if ((mat = Pattern.compile("\\bSHOW\\b\\s*\\bTABLES\\b\\s*;", Pattern.CASE_INSENSITIVE).matcher(line)).find()) {
                showTable();
            }
            //INSERT
            if ((mat = Pattern.compile("\\bINSERT INTO\\b\\s*\\bVALUES\\b\\s*\\b(.*);", Pattern.CASE_INSENSITIVE).matcher(line)).find()) {
                String nameTable = mat.group(0);
                System.out.println(mat.group(0));
                insert();
            }
            //DROP TABLE
        }catch (IndexOutOfBoundsException e){
            System.out.println("La expresion ingresada es invalida");
        }
    }
}

