package edu.lehigh.cse216.bug.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    private GoogleOAuthVerifier verifier;

    /**
     * Set up your test fixture
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        verifier = mock(GoogleOAuthVerifier.class);
    }

    /**
     * Test to verify that a valid token does not cause an exception
     */
    public void testVerifyValidToken() {
        String validToken = "valid.token.value";
        try {
            doNothing().when(verifier);
            // Stubbing the void method should not throw any exception
            GoogleOAuthVerifier.verifyToken(validToken);
        } catch (Exception e) {
            fail("Verification of a valid token should not throw an exception");
        }
    }

    /**
     * Test to verify that an invalid token causes an exception
     */
    public void testVerifyInvalidToken() {
        String invalidToken = "invalid.token.value";
        try {
            doThrow(new SecurityException("Invalid token")).when(verifier);
            // Stubbing the void method to throw an exception for the invalid token
            GoogleOAuthVerifier.verifyToken(invalidToken);
            fail("Verification of an invalid token should throw an exception");
        } catch (SecurityException e) {
            // Expected exception
            assertEquals("Invalid token", e.getMessage());
        } catch (Exception e) {
            fail("Expected a SecurityException for an invalid token");
        }
    }

}
