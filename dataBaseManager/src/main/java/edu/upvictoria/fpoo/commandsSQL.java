package edu.upvictoria.fpoo;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class commandsSQL {
    String path = null;
    public void createTable(String nameTable, String columns){
        File f = new File(path + "/" + nameTable + ".csv");
        if(f.exists()){
            System.out.println("La tabla " + nameTable + " ya existe");
        }
        try {
            FileWriter fw = new FileWriter(f);
            Pattern guiaStruct = Pattern.compile("(\\w+)\\s+(\\w+)(?:\\((\\d+)\\))?\\s*(NOT\\s+NULL|NULL)?\\s*(PRIMARY\\s+KEY)?", Pattern.CASE_INSENSITIVE);
            Matcher mat2 = guiaStruct.matcher(columns);
            StringBuilder mod = new StringBuilder(); //permite modificar construir sin afectar el objeto
            while (mat2.find()) {
                while (mat2.find()) {
                    String nameColumn = mat2.group(1);
                    String dataType = mat2.group(2);
                    String tamanio = mat2.group(3);
                    String obli = mat2.group(4);
                    String Key = mat2.group(5);

                    mod.append(nameColumn+ " " +dataType);
                    if (tamanio != null) {
                        mod.append("(" + tamanio + ")");
                    }
                    if (obli != null) {
                        mod.append(" " + obli);
                    }
                    if (Key != null) {
                        mod.append(" " + Key);
                    }
                    mod.append(",");
                }
            }
            String tit = mod.toString().trim();
            fw.write(tit.substring(0,tit.length()-1) + "\n");
            System.out.println("Tabla creada con exito");
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException("Error creando tabla " + nameTable);
        }
    }
    public void select(String nameTable) {

    }
    public void insert() {

    }
    public void updateTable(){

    }
    public void dropTable(String nameTable) {
        try{
            File directorio = new File(path);
            if(!directorio.exists()){
                throw new RuntimeException("No hay tablas creadas");
            } else {
                File[] files = directorio.listFiles();
                boolean found = false;
                for (File file : files) {
                    if (file.isFile() && file.getName().equals(nameTable + ".csv")) {
                        BufferedReader ent = new BufferedReader(new InputStreamReader(System.in));
                        System.out.print("¿Está seguro de que desea eliminar la tabla " + nameTable + "? (S/N): ");
                        String resp = ent.readLine();
                        if (resp.equalsIgnoreCase("S")) {
                            if (file.delete()) {
                                found = true;
                                System.out.println("Tabla eliminada: " + nameTable);
                            } else {
                                throw new RuntimeException("No se pudo eliminar la tabla: " + nameTable);
                            }
                        } else {
                            System.out.println("La tabla no se ha eliminado");
                            return; // Salimos del método sin eliminar la tabla
                        }
                    }
                }
                if(!found){
                    throw new RuntimeException("No se encontró la tabla : " + nameTable + ".csv");
                }
            }
        } catch (Exception e){
            System.out.println("Error al eliminar la tabla : " + nameTable + ".csv");
        }
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
            if ((mat = Pattern.compile("\\bDROP TABLE\\b\\s*\\b(.*);", Pattern.CASE_INSENSITIVE).matcher(line)).find()) {
                String nameTable = mat.group(1);
                System.out.println(mat.group(1));
                dropTable(nameTable);
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("La expresion ingresada es invalida");
        }
    }
}

