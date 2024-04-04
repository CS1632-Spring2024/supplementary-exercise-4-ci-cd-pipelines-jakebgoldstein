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
import java.lang.reflect.InvocationTargetException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RentACatUnitTest {
    RentACat r;
    Cat c1, c2, c3;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream stdout;
    String newline = System.lineSeparator();

    @Before
    public void setUp() throws Exception {
        r = RentACat.createInstance(InstanceType.IMPL);

        c1 = Cat.createInstance(InstanceType.MOCK, 1, "Jennyanydots");
        Mockito.when(c1.getId()).thenReturn(1);
        Mockito.when(c1.getRented()).thenReturn(false);
        Mockito.when(c1.getName()).thenReturn("Old Deuteronomy");

        c2 = Cat.createInstance(InstanceType.MOCK, 2, "Old Deuteronomy");
        Mockito.when(c2.getId()).thenReturn(2);
        Mockito.when(c2.getRented()).thenReturn(false);
        Mockito.when(c2.getName()).thenReturn("Old Deuteronomy");

        c3 = Cat.createInstance(InstanceType.MOCK, 3, "Mistoffelees");
        Mockito.when(c3.getId()).thenReturn(3);
        Mockito.when(c3.getRented()).thenReturn(false);
        Mockito.when(c3.getName()).thenReturn("Old Deuteronomy");

        Mockito.when(c1.toString()).thenReturn("ID 1. Jennyanydots");
        Mockito.when(c2.toString()).thenReturn("ID 2. Old Deuteronomy");
        Mockito.when(c3.toString()).thenReturn("ID 3. Mistoffelees");

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
    public void testGetCatNullNumCats0() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method m = r.getClass().getDeclaredMethod("getCat", int.class);
        m.setAccessible(true);
        Object ret = m.invoke(r , 2);
        assertNull(ret);
        assertEquals("Invalid cat ID." + newline, out.toString());
    }

    @Test
    public void testGetCatNumCats3() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<? extends RentACat> className = r.getClass();
        r.addCat(c1);
        r.addCat(c2);
        r.addCat(c3);
        Method m = className.getDeclaredMethod("getCat", int.class);
        m.setAccessible(true);
        Cat ret = (Cat) m.invoke(r, 2);
        assertEquals(2, ret.getId());
    }

    @Test
    public void testListCatsNumCats0() {
        String ret = r.listCats();
        assertEquals("", ret);
    }

    @Test
    public void testListCatsNumCats3() {
        r.addCat(c1);
        r.addCat(c2);
        r.addCat(c3);
        String ret = r.listCats();
        assertEquals("ID 1. Jennyanydots\nID 2. Old Deuteronomy\nID 3. Mistoffelees\n", ret);
    }

    @Test
    public void testRenameFailureNumCats0() {
        assertFalse(r.renameCat(2, "Garfield"));
        assertNotEquals(c2.getName(), "Garfield");
        assertEquals("Invalid cat ID." + newline, out.toString());
    }

    @Test
    public void testRenameNumCat3() {
        r.addCat(c1);
        r.addCat(c2);
        r.addCat(c3);
        Mockito.when(c2.getId()).thenReturn(2);
        boolean ret = r.renameCat(2, "Garfield");
        Mockito.verify(c2).renameCat("Garfield");
        assertTrue(ret);
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
