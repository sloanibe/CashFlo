package monk.cashflo;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class HelloWorldTest {

    private HelloWorld helloWorld;

    @Before
    public void setUp() throws Exception {
        helloWorld = new HelloWorld();
        System.out.println("test2");
    }

    @Test
    public void testGetFirstName() {

        assertEquals("Jane", "Jane");
    }

}
