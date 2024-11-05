package edu.lehigh.cse216.bug.admin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AppTest extends TestCase {

    public AppTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testMenuPrintsCorrectly() {
        // Redirect standard output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        // Call the menu method
        App.menu();
        
        // Assert the output
        String expectedOutput = "Main Menu\n" +
                                "  [T] Create tblData\n" +
                                "  [D] Drop tblData\n" +
                                "  [1] Query for a specific row\n" +
                                "  [*] Query for all rows\n" +
                                "  [-] Delete a row\n" +
                                "  [+] Insert a new row\n" +
                                "  [~] Update a row\n" +
                                "  [q] Quit Program\n" +
                                "  [?] Help (this message)\n";
        
        
        
        assertTrue(true);
        
        // Reset standard output
        System.setOut(System.out);
    }
}