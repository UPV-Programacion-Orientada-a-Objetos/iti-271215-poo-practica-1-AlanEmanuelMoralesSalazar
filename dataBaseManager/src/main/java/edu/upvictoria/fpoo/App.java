package edu.upvictoria.fpoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.*;

public class App
{
    public static void main( String[] args )
    {
        commandsSQL sql = new commandsSQL();
        try {
            sql.use("/home/alan/Escritorio/POO");
        }catch(Exception e) {

        }
        {
        String line = null;
        BufferedReader ent = new BufferedReader(new InputStreamReader(System.in));
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


