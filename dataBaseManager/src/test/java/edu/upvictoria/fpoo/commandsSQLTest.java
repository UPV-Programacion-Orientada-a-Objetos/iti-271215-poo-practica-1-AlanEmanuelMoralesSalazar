package edu.upvictoria.fpoo;

import junit.framework.TestCase;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class commandsSQLTest extends TestCase {

    public void testCreateTable() {
        // Redirige la salida estándar para poder capturarla y verificarla
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        commandsSQL tableCreator = new commandsSQL();
        tableCreator.createTable("testTable", "id INT NOT NULL PRIMARY KEY, name VARCHAR(255) NULL");

        // Verifica que se haya impreso el mensaje de éxito
        assertEquals("Tabla creada con exito\n", outputStreamCaptor.toString());
    }
    public void testCreateExistingTable() {
        // Redirige la salida estándar para poder capturarla y verificarla
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        commandsSQL tableCreator = new commandsSQL();

        // Crea una tabla con el mismo nombre antes de intentar crearla de nuevo
        tableCreator.createTable("existingTable", "id INT NOT NULL PRIMARY KEY, name VARCHAR(255) NULL");

        // Intenta crear la misma tabla de nuevo
        tableCreator.createTable("existingTable", "id INT NOT NULL PRIMARY KEY, name VARCHAR(255) NULL");

        // Verifica que se haya impreso el mensaje de que la tabla ya existe
        assertEquals("La tabla existingTable ya existe\n", outputStreamCaptor.toString());
    }
    public void testDropExistingTable() {
        // Redirige la entrada estándar para simular la entrada del usuario
        ByteArrayInputStream inputStream = new ByteArrayInputStream("S\n".getBytes());
        System.setIn(inputStream);

        // Redirige la salida estándar para poder capturarla y verificarla
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        commandsSQL tableRemover = new commandsSQL();
        tableRemover.dropTable("testTable");

        // Verifica que se haya impreso el mensaje de tabla eliminada
        assertEquals("¿Está seguro de que desea eliminar la tabla testTable? (S/N): Tabla eliminada: testTable\n", outputStreamCaptor.toString());
    }
    public void testDropNonExistingTable() {
        // Redirige la salida estándar para poder capturarla y verificarla
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        commandsSQL tableRemover = new commandsSQL();

        // Intenta eliminar una tabla que no existe
        tableRemover.dropTable("nonExistingTable");

        // Verifica que se haya impreso el mensaje de que la tabla no se encontró
        assertEquals("No se encontró la tabla : nonExistingTable.csv\n", outputStreamCaptor.toString());
    }

}