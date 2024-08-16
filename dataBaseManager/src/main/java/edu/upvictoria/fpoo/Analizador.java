package edu.upvictoria.fpoo;

import java.util.*;

public class Analizador {
    public void commands(String line, commandsSQL cmdSQL) {
        line = line.trim();
        if (line.toUpperCase().startsWith("USE")) {
            handleUse(line, cmdSQL);
        } else if (line.toUpperCase().startsWith("CREATE TABLE")) {
            handleCreateTable(line, cmdSQL);
        } else if (line.toUpperCase().startsWith("SELECT")) {
            handleSelect(line, cmdSQL);
        } else if (line.toUpperCase().startsWith("INSERT INTO")) {
            handleInsert(line, cmdSQL);
        } else if (line.toUpperCase().startsWith("DROP TABLE")) {
            handleDropTable(line, cmdSQL);
        } else if (line.toUpperCase().startsWith("SHOW TABLES")) {
            handleShowTables(line, cmdSQL);
        } else if (line.toUpperCase().startsWith("DELETE FROM")){
            handleDelete(line, cmdSQL);
        } else if (line.toUpperCase().startsWith("UPDATE")) {
            handleUpdate(line, cmdSQL);
        } else {
            System.out.println("Comando no soportado: " + line);
        }
    }
    private void handleUse(String line, commandsSQL cmdSQL) {
        try {
            String path = line.substring(4).trim();
            cmdSQL.use(path);
        } catch (Exception e) {
            System.out.println("Error al usar la ruta. Comando mal formado.");
        }
    }
    
    private void handleCreateTable(String line, commandsSQL cmdSQL) {
        try {
            String[] parts = line.split("\\s+", 3);
            String tableName = parts[2].substring(0, parts[2].indexOf('(')).trim();
            String columns = parts[2].substring(parts[2].indexOf('(') + 1, parts[2].lastIndexOf(')')).trim();
            if (!reservedWords.contains(tableName.toUpperCase())) {
                cmdSQL.createTable(tableName, columns);
            } else {
                System.out.println("No se puede crear tablas con palabras reservadas");
            }
        } catch (Exception e) {
            System.out.println("Error al crear la tabla. Comando mal formado.");
        }
    }

    public void handleSelect(String line, commandsSQL cmdSQL) {
        try {
            line = line.trim().toUpperCase();

        // Extraer el nombre de la tabla
        String[] parts = line.split("FROM");
        if (parts.length < 2) {
            throw new RuntimeException("Consulta SELECT mal formada: falta la cláusula FROM");
        }

        // Manejar la parte del SELECT
        String selectPart = parts[0].replace("SELECT", "").trim();
        String[] selectedColumns = selectPart.equals("*") ? null : selectPart.split(",");

        // Manejar la parte del FROM
        String fromPart = parts[1].trim();
        String tableName;
        String condition = null;

        // Verificar si hay una cláusula WHERE
        if (fromPart.contains("WHERE")) {
            String[] fromParts = fromPart.split("WHERE");
            tableName = fromParts[0].trim();
            condition = fromParts[1].trim();
        } else {
            tableName = fromPart;
        }

        cmdSQL.select(tableName, selectedColumns, condition);

        } catch (RuntimeException e){
            System.out.println("Error al seleccionar la tabla. Comando mal formado.");
        }
    }
    private void handleUpdate(String line, commandsSQL cmdSQL) {
        try {
            line = line.toUpperCase();
            // Remover el prefijo "UPDATE" y dividir en partes
            String remaining = line.substring(6).trim();
            String[] parts = remaining.split("SET");
            String tableName = parts[0].trim();
            String[] setClauses = parts[1].split("WHERE");

            // Procesar la parte del SET
            String[] assignments = setClauses[0].split(",");
            Map<String, String> updates = new HashMap<>();
            for (String assignment : assignments) {
                String[] pair = assignment.split("=");
                updates.put(pair[0].trim(), pair[1].trim());
            }

            // Procesar la cláusula WHERE
            String condition = null;
            if (setClauses.length > 1) {
                condition = setClauses[1].trim();
            }

            cmdSQL.updateTable(tableName, updates, condition);
        } catch (Exception e) {
            System.out.println("Error al actualizar la tabla. Comando mal formado.");
        }
    }

    private void handleInsert(String line, commandsSQL cmdSQL) {
        try {
            String remaining = line.substring(11).trim();
    
            String tableName = remaining.substring(0, remaining.indexOf('(')).trim();
    
            // obtiene las columnas especificadas entre paréntesis
            String columnsPart = remaining.substring(remaining.indexOf('(') + 1, remaining.indexOf(')')).trim();
            String[] columns = columnsPart.split(",");
    
            if (remaining.toUpperCase().contains("VALUES")) {
                String valuesPart = remaining.substring(remaining.toUpperCase().indexOf("VALUES") + 6).trim();
                valuesPart = valuesPart.substring(valuesPart.indexOf('(') + 1, valuesPart.lastIndexOf(')')).trim();
                String[] values = valuesPart.split(",");
    
                cmdSQL.insert(tableName, columns, values);
            } else {
                System.out.println("Comando INSERT mal formado. Falta la palabra clave VALUES.");
            }
        } catch (Exception e) {
            System.out.println("Error al insertar datos. Comando mal formado.");    
        }   
    }    

    public void handleDelete(String line, commandsSQL cmdSQL) {
        line = line.trim().toUpperCase();
    
        // Extraer el nombre de la tabla
        String[] parts = line.split("FROM");
        if (parts.length < 2) {
            throw new RuntimeException("Consulta DELETE mal formada: falta la cláusula FROM");
        }
    
        // Manejar la parte del DELETE
        String tableName;
        String condition = null;
    
        // Manejar la parte del FROM
        String fromPart = parts[1].trim();
    
        // Verificar si hay una cláusula WHERE
        if (fromPart.contains("WHERE")) {
            String[] fromParts = fromPart.split("WHERE");
            tableName = fromParts[0].trim();
            condition = fromParts[1].trim();
        } else {
            tableName = fromPart;
        }
    
        // Llamar al método delete de commandsSQL con los parámetros obtenidos
        int rowsAffected = cmdSQL.delete(tableName, condition);
    
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " filas eliminadas con éxito");
        } else {
            System.out.println("No se eliminaron filas de " + tableName);
        }
    }
    
    private void handleDropTable(String line, commandsSQL cmdSQL) {
        try {
            String[] parts = line.split("\\s+");
            String tableName = parts[2].replace(";", "").trim();
            cmdSQL.dropTable(tableName);
        } catch (Exception e) {
            System.out.println("Error al eliminar la tabla. Comando mal formado.");
        }
    }

    private void handleShowTables(String line, commandsSQL cmdSQL) {
        cmdSQL.showTable();
    }

    private final List<String> reservedWords = new ArrayList<>();
    public Analizador() {
        reservedWords.add("ABORT");
        reservedWords.add("ALL");
        reservedWords.add("ALLOCATE");
        reservedWords.add("ANALYSE");
        reservedWords.add("ANALYZE");
        reservedWords.add("AND");
        reservedWords.add("ANY");
        reservedWords.add("AS");
        reservedWords.add("ASC");
        reservedWords.add("BETWEEN");
        reservedWords.add("BINARY");
        reservedWords.add("BIT");
        reservedWords.add("BOTH");
        reservedWords.add("CASE");
        reservedWords.add("CAST");
        reservedWords.add("CHAR");
        reservedWords.add("CHARACTER");
        reservedWords.add("CHECK");
        reservedWords.add("CLUSTER");
        reservedWords.add("COALESCE");
        reservedWords.add("COLLATE");
        reservedWords.add("COLLATION");
        reservedWords.add("COLUMN");
        reservedWords.add("CONSTRAINT");
        reservedWords.add("COPY");
        reservedWords.add("CROSS");
        reservedWords.add("CURRENT");
        reservedWords.add("CURRENT_CATALOG");
        reservedWords.add("CURRENT_DATE");
        reservedWords.add("CURRENT_DB");
        reservedWords.add("CURRENT_SCHEMA");
        reservedWords.add("CURRENT_SID");
        reservedWords.add("CURRENT_TIME");
        reservedWords.add("CURRENT_TIMESTAMP");
        reservedWords.add("CURRENT_USER");
        reservedWords.add("CURRENT_USERID");
        reservedWords.add("CURRENT_USEROID");
        reservedWords.add("DEALLOCATE");
        reservedWords.add("DECIMAL");
        reservedWords.add("DEC");
        reservedWords.add("DEFAULT");
        reservedWords.add("DESC");
        reservedWords.add("DISTINCT");
        reservedWords.add("DISTRIBUTE");
        reservedWords.add("DO");
        reservedWords.add("ELSE");
        reservedWords.add("END");
        reservedWords.add("EXCEPT");
        reservedWords.add("EXCLUDE");
        reservedWords.add("EXPRESS");
        reservedWords.add("EXTEND");
        reservedWords.add("EXTERNAL");
        reservedWords.add("EXTRACT");
        reservedWords.add("FALSE");
        reservedWords.add("FIRST");
        reservedWords.add("FLOAT");
        reservedWords.add("FOLLOWING");
        reservedWords.add("FOREIGN");
        reservedWords.add("FOR");
        reservedWords.add("FROM");
        reservedWords.add("FULL");
        reservedWords.add("FUNCTION");
        reservedWords.add("GENSTATS");
        reservedWords.add("GLOBAL");
        reservedWords.add("GROUP");
        reservedWords.add("HAVING");
        reservedWords.add("IDENTIFIER_CASE");
        reservedWords.add("ILIKE");
        reservedWords.add("IN");
        reservedWords.add("INOUT");
        reservedWords.add("INNER");
        reservedWords.add("INTO");
        reservedWords.add("INTERVAL");
        reservedWords.add("INSERT");
        reservedWords.add("JOIN");
        reservedWords.add("LEADING");
        reservedWords.add("LEFT");
        reservedWords.add("LIKE");
        reservedWords.add("LIMIT");
        reservedWords.add("LOCAL");
        reservedWords.add("LOCK");
        reservedWords.add("MINUS");
        reservedWords.add("MOVE");
        reservedWords.add("NATURAL");
        reservedWords.add("NCHAR");
        reservedWords.add("NEW");
        reservedWords.add("NOT");
        reservedWords.add("NOTNULL");
        reservedWords.add("NULL");
        reservedWords.add("NULLS");
        reservedWords.add("NVL");
        reservedWords.add("NVL2");
        reservedWords.add("OFFSET");
        reservedWords.add("OLD");
        reservedWords.add("ON");
        reservedWords.add("ONLINE");
        reservedWords.add("ONLY");
        reservedWords.add("OR");
        reservedWords.add("OTHERS");
        reservedWords.add("OUT");
        reservedWords.add("OUTER");
        reservedWords.add("OVER");
        reservedWords.add("OVERLAPS");
        reservedWords.add("PARTITION");
        reservedWords.add("POSITION");
        reservedWords.add("PRECEDING");
        reservedWords.add("PRECISION");
        reservedWords.add("PRIMARY");
        reservedWords.add("RESET");
        reservedWords.add("REUSE");
        reservedWords.add("RIGHT");
        reservedWords.add("ROWS");
        reservedWords.add("SELECT");
        reservedWords.add("SESSION_USER");
        reservedWords.add("SETOF");
        reservedWords.add("SHOW");
        reservedWords.add("SOME");
        reservedWords.add("TABLE");
        reservedWords.add("THEN");
        reservedWords.add("TIME");
        reservedWords.add("TIMESTAMP");
        reservedWords.add("TO");
        reservedWords.add("TRAILING");
        reservedWords.add("TRANSACTION");
        reservedWords.add("TRIGGER");
        reservedWords.add("TRIM");
        reservedWords.add("TRUE");
        reservedWords.add("UNBOUNDED");
        reservedWords.add("UNION");
        reservedWords.add("UNIQUE");
        reservedWords.add("USER");
        reservedWords.add("USING");
        reservedWords.add("VERBOSE");
        reservedWords.add("VERSION");
        reservedWords.add("VIEW");
        reservedWords.add("WHEN");
        reservedWords.add("WHERE");
        reservedWords.add("WITH");
        reservedWords.add("WRITE");
    }
}