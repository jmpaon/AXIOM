/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jmpaon
 */
public class MicmacMatrixTest {
    
    public MicmacMatrixTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of MICMACranking method, of class MicmacMatrix.
     */
    @Test
    public void testMICMACranking() {
        System.out.println("MICMACranking");
        MicmacMatrix.Orientation orientation = null;
        MicmacMatrix instance = null;
        VarInfoTable<String> expResult = null;
        VarInfoTable<String> result = instance.MICMACranking(orientation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of power method, of class MicmacMatrix.
     */
    @Test
    public void testPower() {
        System.out.println("power");
        String[] names = {"A","B","C"};
        double[] vals = {0,1,2,3,0,-2,5,-1,0};
        MicmacMatrix instance = new MicmacMatrix(3, true, names, vals);
        MicmacMatrix expResult = null;
        MicmacMatrix result = instance.power();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of booleanImpactMatrix method, of class MicmacMatrix.
     */
    @Test
    public void testBooleanImpactMatrix() {
        System.out.println("booleanImpactMatrix");
        double threshold = 0.0;
        MicmacMatrix instance = null;
        MicmacMatrix expResult = null;
        MicmacMatrix result = instance.booleanImpactMatrix(threshold);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOrdering method, of class MicmacMatrix.
     */
    @Test
    public void testGetOrdering() {
        System.out.println("getOrdering");
        MicmacMatrix.Orientation orientation = null;
        MicmacMatrix instance = null;
        MicmacMatrix.Ordering expResult = null;
        MicmacMatrix.Ordering result = instance.getOrdering(orientation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sumRanking method, of class MicmacMatrix.
     */
    @Test
    public void testSumRanking() {
        System.out.println("sumRanking");
        int varIndex = 0;
        MicmacMatrix.Orientation orientation = null;
        MicmacMatrix instance = null;
        int expResult = 0;
        int result = instance.sumRanking(varIndex, orientation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of altMICMAC method, of class MicmacMatrix.
     */
    @Test
    public void testAltMICMAC() {
        System.out.println("altMICMAC");
        MicmacMatrix.Orientation orientation = null;
        MicmacMatrix instance = null;
        VarInfoTable expResult = null;
        VarInfoTable result = instance.altMICMAC(orientation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAltOrdering method, of class MicmacMatrix.
     */
    @Test
    public void testGetAltOrdering() {
        System.out.println("getAltOrdering");
        MicmacMatrix.Orientation orientation = null;
        MicmacMatrix instance = null;
        MicmacMatrix.AltOrdering expResult = null;
        MicmacMatrix.AltOrdering result = instance.getAltOrdering(orientation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        
    }
    
}
