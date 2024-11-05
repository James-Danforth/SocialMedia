package edu.lehigh.cse216.bug.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DataRowTest extends TestCase{
    public DataRowTest(String testName){
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(DataRowTest.class);
    }

    public void testConstructor() {
        String content = "Test Content";
        int id = 17;
        DataRow d = new DataRow(id, 0, 0, content);

        assertTrue(d.mContent.equals(content));
        assertTrue(d.mId == id);
        assertFalse(d.mCreated == null);
    }

    public void testCopyconstructor() {
        String content = "Test Content For Copy";
        int id = 177;
        int likes = 0;
        int dislikes = 0;
        
        DataRow d = new DataRow(id, likes, dislikes, content);
        DataRow d2 = new DataRow(d);
        assertTrue(d2.mLikes==d.mLikes);
        assertTrue(d2.mContent.equals(d.mContent));
        assertTrue(d2.mId == d.mId);
        assertTrue(d2.mCreated.equals(d.mCreated));
        
    }


}
