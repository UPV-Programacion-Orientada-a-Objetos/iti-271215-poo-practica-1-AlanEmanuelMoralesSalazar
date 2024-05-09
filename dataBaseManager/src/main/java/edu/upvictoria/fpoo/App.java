package edu.upvictoria.fpoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.*;

public class App
{
    public static void main( String[] args )
    {
        String line = null;
        BufferedReader ent = new BufferedReader(new InputStreamReader(System.in));
        commandsSQL sql = new commandsSQL();
        try {
            System.out.println("Path: ");
            String path = ent.readLine();
            sql.use(path);
        }catch(Exception e) {
            System.out.println("Path no identificado");
            return;
        }
        {
        try {
            while(true) {
                line = ent.readLine();
                sql.commands(line);
                if(line.equals("exit")){
                    break;
                }
            }
            ent.close();
        } catch (IOException e) {
            System.err.println("Ha ocurrido un error al leer el entrada");
        }
        }
    }
}


