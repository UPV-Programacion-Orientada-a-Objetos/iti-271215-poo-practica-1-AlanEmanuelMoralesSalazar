package edu.upvictoria.fpoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.*;

public class App {
    public static void main(String[] args) {
        BufferedReader ent = new BufferedReader(new InputStreamReader(System.in));
        Analizador sql = new Analizador();
        commandsSQL cmdSQL = new commandsSQL();
        StringBuilder qb = new StringBuilder();

        try {
            while (true) {
                System.out.print("> ");
                String line = ent.readLine().trim();
                if (line.equalsIgnoreCase("exit")) {
                    break;
                }
                qb.append(line).append(" ");
                // checamos si la sentencia termina en ;
                if (line.endsWith(";")){
                    String q = qb.toString().trim();
                    q = q.substring(0, q.length() - 1).trim();
                    sql.commands(q, cmdSQL);
                    qb.setLength(0);  //limpia el stringbuilder
                }
            }
            ent.close();
        } catch (IOException e) {
            System.err.println("Ha ocurrido un error al leer la entrada");
        }
    }
}


