package edu.pitt.cs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RentACatUnitTest {
    RentACat r;
    Cat c1, c2, c3;
    ByteArrayOutputStream out;
    PrintStream stdout;
    String newline = System.lineSeparator();

    @Before
    public void setUp() throws Exception {
        r = RentACat.createInstance(InstanceType.IMPL);

        c1 = Cat.createInstance(InstanceType.MOCK, 1, "Jennyanydots");
        c2 = Cat.createInstance(InstanceType.MOCK, 2, "Old Deuteronomy");
        c3 = Cat.createInstance(InstanceType.MOCK, 3, "Mistoffelees");

        Mockito.when(c2.getName()).thenReturn("Old Deuteronomy");
        Mockito.when(c2.getId()).thenReturn(2);
        Mockito.when(c2.getRented()).thenReturn(false);

        out = new ByteArrayOutputStream();
        stdout = System.out;
        System.setOut(new PrintStream(out));
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(stdout);
        r = null;
        c1 = null;
        c2 = null;
        c3 = null;
    }

    @Test
    public void testGetCatNullNumCats0() {
        try {
            Method method = r.getClass().getDeclaredMethod("getCat", int.class);
            method.setAccessible(true);
            Object result = method.invoke(r, 2);
            assertNull(result);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testGetCatNumCats3() {
        r.addCat(c1);
        r.addCat(c2);
        r.addCat(c3);
        try {
            Method method = r.getClass().getDeclaredMethod("getCat", int.class);
            method.setAccessible(true);
            Cat result = (Cat) method.invoke(r, 2);
            assertNotNull(result);
            assertEquals(2, result.getId());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testListCatsNumCats0() {
        assertEquals("", r.listCats());
    }

    @Test
    public void testListCatsNumCats3() {
        r.addCat(c1);
        r.addCat(c2);
        r.addCat(c3);
        String expectedOutput = "ID 1. Jennyanydots" + newline
                              + "ID 2. Old Deuteronomy" + newline
                              + "ID 3. Mistoffelees" + newline;
        assertEquals(expectedOutput, r.listCats());
    }

    @Test
    public void testRenameFailureNumCats0() {
        boolean result = r.renameCat(2, "Garfield");
        assertEquals(false, result);
        assertNotEquals("Garfield", c2.getName());
        assertEquals("Invalid cat ID." + newline, out.toString());
    }

    @Test
    public void testRenameNumCat3() {
        r.addCat(c1);
        r.addCat(c2);
        r.addCat(c3);
        boolean result = r.renameCat(2,"Garfield");
        assertTrue(result);
        Mockito.verify(c2, Mockito.atLeastOnce()).renameCat("Garfield");
    }

    @Test
    public void testRentCatNumCats3() {
        r.addCat(c1);
        r.addCat(c2);
        r.addCat(c3);
        Mockito.when(c2.getId()).thenReturn(2);
        Mockito.when(c2.getName()).thenReturn("Old Deuteronomy");
        assertTrue(r.rentCat(2));
        assertEquals("Old Deuteronomy has been rented." + newline, out.toString());
    }

    @Test
    public void testRentCatFailureNumCats3() {
        r.addCat(c1);
        r.addCat(c2);
        r.addCat(c3);
        Mockito.when(c2.getRented()).thenReturn(true);
        assertFalse(r.rentCat(2));
        assertEquals("Sorry, Old Deuteronomy is not here!" + newline, out.toString());
    }

    @Test
    public void testReturnCatNumCats3() {
        r.addCat(c1);
        r.addCat(c2);
        r.addCat(c3);
        Mockito.when(c2.getRented()).thenReturn(true);
        assertTrue(r.returnCat(2));
        assertEquals("Welcome back, Old Deuteronomy!" + newline, out.toString());
    }

    @Test
    public void testReturnFailureCatNumCats3() {
        r.addCat(c1);
        r.addCat(c2);
        r.addCat(c3);
        Mockito.when(c2.getId()).thenReturn(2);
        Mockito.when(c2.getName()).thenReturn("Old Deuteronomy");
        assertFalse(r.returnCat(2));
        assertEquals("Old Deuteronomy is already here!" + newline, out.toString());
    }
}
