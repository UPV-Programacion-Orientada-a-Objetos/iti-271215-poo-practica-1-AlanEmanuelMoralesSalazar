package edu.upvictoria.fpoo;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class commandsSQL {
    private String pathG = null;

    // FUNCION PARA CREAR LA TABLA
    public void createTable(String nameTable, String columns) {
        File f = new File(pathG + "/" + nameTable + ".csv");
        if (f.exists()) {
            System.out.println("La tabla " + nameTable + " ya existe");
            return;
        }
        try {
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            String[] columnDefs = columns.split(",");
            StringBuilder columnInfo = new StringBuilder();

            columnInfo.append("Columna,Tipo de Dato,NULL?,PK?\n");

            for (String col : columnDefs) {
                String[] colParts = col.trim().split("\\s+");
                String columnName = colParts[0];
                String columnType = colParts.length > 1 ? colParts[1] : " ";
                String isNullable = "SI";
                String isPK = "NO";
                for (int i = 2; i < colParts.length; i++) {
                    if (colParts[i].equalsIgnoreCase("NOT")) {
                        if (i + 1 < colParts.length && colParts[i + 1].equalsIgnoreCase("NULL")) {
                            isNullable = "NO";
                            i++;
                        }
                    }
                }
                for (int i = 2; i < colParts.length; i++){
                    if (colParts[i].equalsIgnoreCase("PRIMARY")){
                        if (i + 1 < colParts.length && colParts[i +1].equalsIgnoreCase("KEY")){
                            isPK = "SI";
                            i++;
                        }
                    }
                }
                columnInfo.append(columnName).append(",").append(columnType).append(",").append(isNullable).append(",").append(isPK).append("\n");
            }
            pw.write(columnInfo.toString());
            pw.close();
            System.out.println("Tabla creada con éxito");
        } catch (IOException e) {
            throw new RuntimeException("Error creando tabla " + nameTable);
        }
    }
    
    //FUNCIÓN PARA EL SELECT
    public void select(String nameTable, String[] selectedColumns, String condition) {
        nameTable = nameTable.toLowerCase();
        File tableFile = new File(pathG + "/" + nameTable + ".csv");
        if (!tableFile.exists()) {
            System.out.println("La tabla " + nameTable + " no existe");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(tableFile))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("La tabla " + nameTable + " está vacía");
                return;
            }

            String[] columns = headerLine.split(",");
            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (int i = 0; i < columns.length; i++) {
                columnIndexMap.put(columns[i].trim().toUpperCase(), i);
            }

            // Si selectedColumns está vacío, seleccionará todas las columnas
            if (selectedColumns == null || selectedColumns.length == 0) {
                selectedColumns = columns;
            }

            WHERE where = new WHERE(columnIndexMap);
            condition = condition != null ? condition.toLowerCase() : null;

            for (String col : selectedColumns) {
                System.out.print(col.trim().toLowerCase() + " ");
            }
            System.out.println();

            String line;
            boolean foundRows = false;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if (condition == null || where.evaluateCondition(row, condition)) {
                    foundRows = true;
                    for (String col : selectedColumns) {
                        Integer colIndex = columnIndexMap.get(col.trim().toUpperCase());
                        if (colIndex != null) {
                            System.out.print(row[colIndex] + " ");
                        } else {
                            System.out.print("NULL ");
                        }
                    }
                    System.out.println();
                }
            }

            if (!foundRows) {
                System.out.println("No se encontraron filas que coincidan con la condición.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer la tabla " + nameTable);
        }
    }

    //FUNCION PARA EL DELETE
    public int delete(String tableName, String condition) {
        tableName = tableName.toLowerCase();
        File tableFile = new File(pathG + "/" + tableName + ".csv");
        if (!tableFile.exists()) {
            System.out.println("La tabla " + tableName + " no existe");
            return 0;
        }

        File tempFile = new File(pathG + "/" + tableName + "_temp.csv");
        int rowsAffected = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(tableFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("La tabla " + tableName + " está vacía");
                return 0;
            }
            bw.write(headerLine + "\n");

            String[] columns = headerLine.split(",");
            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (int i = 0; i < columns.length; i++) {
                columnIndexMap.put(columns[i].trim().toUpperCase(), i);
            }

            WHERE where = new WHERE(columnIndexMap);
            condition = condition != null ? condition.toLowerCase() : null;

            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if (condition == null || !where.evaluateCondition(row, condition)) {
                    bw.write(line + "\n");
                } else {
                    rowsAffected++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar la tabla " + tableName);
        }

        if (rowsAffected > 0) {
            if (!tableFile.delete() || !tempFile.renameTo(tableFile)) {
                System.out.println("Error al actualizar la tabla después de eliminar filas.");
                return 0;
            }
        } else {
            tempFile.delete();
        }

        return rowsAffected;
    }

    //FUNCION PARA EL INSERT
    public void insert(String tableName, String[] columns, String[] values) {
        File structFile = new File(pathG + "/" + tableName + "_struct.csv");
        if (!structFile.exists()) {
            System.out.println("La estructura de la tabla " + tableName + " no existe");
            return;
        }
    
        File newFile = new File(pathG + "/" + tableName + ".csv");
    
        try (BufferedReader br = new BufferedReader(new FileReader(structFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, true))) {
    
            // lee las columnas del _struct para obtener los nombres de las columnas
            List<String> fileColumns = new ArrayList<>();
            String line;
            boolean isHeaderWritten = (newFile.length() != 0);
        
            // lee cada linea para extraer los nombres de las columnas
            while ((line = br.readLine()) != null){
                String[] structParts = line.split(",");
                if(!structParts[0].equalsIgnoreCase("Columna")){
                    fileColumns.add(structParts[0].trim());
                }
            }
            // escribe el encabezado en caso de que sea la primera vez
            if (!isHeaderWritten){
                bw.write(String.join(",", fileColumns) + "\n");
            }

            //crea un array para almacenar los valores a insertar
            String[] insertValues = new String[fileColumns.size()];

            for (int i = 0; i < insertValues.length; i++){
                insertValues[i] = "NULL";
            }

            boolean idIncluded = false;
            for (int i = 0; i < columns.length; i++) {
                String column = columns[i].trim();
                String value = values[i].trim();

                // verifica si el id esta incluido
                if (column.equalsIgnoreCase("id")){
                    idIncluded = true;
                }
    
                // Buscar la columna en el archivo _struct.csv y asignar el valor correspondiente
                for (int j = 0; j < fileColumns.size(); j++) {
                    if (fileColumns.get(j).equalsIgnoreCase(column)) {
                        insertValues[j] = value;
                        break;
                    }
                }
            }

            if (!idIncluded) {
                throw new RuntimeException("EL id es obligatorio, no puede ser nulo");
            }
    
            // Crear la fila para insertar
            StringBuilder mod = new StringBuilder();
            for (String val : insertValues) {
                mod.append(val).append(",");
            }
            String row = mod.toString().trim();
            bw.write(row.substring(0, row.length() - 1) + "\n");
    
            System.out.println("Datos insertados con éxito en la tabla " + tableName);
        } catch (IOException e) {
            throw new RuntimeException("Error al insertar datos en la tabla " + tableName);
        }
    }    

    //FUNCION PARA EL UPDATE
    public void updateTable(String tableName, Map<String, String> updates, String condition) {
        tableName = tableName.toLowerCase();
        File tableFile = new File(pathG + "/" + tableName + ".csv");
        if (!tableFile.exists()) {
            System.out.println("La tabla " + tableName + " no existe");
            return;
        }

        File tempFile = new File(pathG + "/" + tableName + "_temp.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(tableFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String header = br.readLine(); // leer el encabezado
            bw.write(header + "\n"); // copiar el encabezado al archivo temporal

            String[] columns = header.split(",");
            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (int i = 0; i < columns.length; i++) {
                columnIndexMap.put(columns[i].trim().toUpperCase(), i);
            }

            WHERE where = new WHERE(columnIndexMap);
            String line;

            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if (condition == null || where.evaluateCondition(row, condition)) {
                    // Realizar las actualizaciones
                    for (Map.Entry<String, String> entry : updates.entrySet()) {
                        Integer colIndex = columnIndexMap.get(entry.getKey().toUpperCase());
                        if (colIndex != null) {
                            row[colIndex] = entry.getValue().toLowerCase();
                        }
                    }
                }
                bw.write(String.join(",", row) + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar datos de la tabla " + tableName);
        }

        // Reemplazar el archivo original por el temporal
        if (!tableFile.delete()) {
            throw new RuntimeException("Error al eliminar el archivo original");
        }
        if (!tempFile.renameTo(tableFile)) {
            throw new RuntimeException("Error al renombrar el archivo temporal");
        }

        System.out.println("Datos actualizados con éxito en la tabla " + tableName);
    }

    //FUNCION PARA EL DROP
    public void dropTable(String nameTable) {
        try {
            File directorio = new File(pathG);
            if (!directorio.exists()) {
                throw new RuntimeException("No hay tablas creadas");
            } else {
                File[] files = directorio.listFiles();
                boolean found = false;
                for (File file : files) {
                    if (file.isFile() && file.getName().equals(nameTable + ".csv")) {
                        BufferedReader ent = new BufferedReader(new InputStreamReader(System.in));
                        System.out.print("¿Seguro de eliminar la tabla " + nameTable + "? (S/N): ");
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
                            return;
                        }
                    }
                }
                if (!found) {
                    throw new RuntimeException("No se encontró la tabla : " + nameTable);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar la tabla : " + nameTable);
        }
    }

    //FUNCION PARA EL USE
    public void use(String path) {
        File arch = new File(path);
        if (!arch.exists() || !arch.isDirectory()) {
            throw new RuntimeException("Ruta no encontrada o no es un directorio");
        } else {
            this.pathG = path;
            System.out.println("Ruta configurada a: " + path);
        }
    }

    //FUNCION PARA EL SHOW TABLES;
    public void showTable() {
        try {
            File directorio = new File(pathG);
            if (!directorio.exists()) {
                throw new RuntimeException("No hay tablas creadas");
            } else {
                File[] files = directorio.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isFile() && f.getName().endsWith(".csv")) {
                            String fileName = f.getName();
                            String tableName = fileName.substring(0, fileName.length() - 4); 
                            System.out.println(tableName);
                        }
                    }
                } else {
                    System.out.println("No hay tablas creadas");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error en la lectura del archivo");
        }
    }
}
