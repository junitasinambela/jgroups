package jgroups;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import jgroups.main.ReplSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author user
 */
public class SetTest {
    
    public SetTest() {
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

    @Test
    public void testSet() {
        ReplSet set1 = new ReplSet();
        ReplSet set2 = new ReplSet();
        try {
            set1.init();
            Thread.sleep(5000);
            set1.handleCommand("add a");
            Thread.sleep(1000);
            set1.handleCommand("add b");
            Thread.sleep(1000);
            set2.init();
            Thread.sleep(5000);
            assertEquals("check set2 getState", true, set2.contains("b"));
            set2.handleCommand("add c");
            Thread.sleep(1000);
            set2.handleCommand("remove a");
            Thread.sleep(1000);
            assertEquals("check remove message", false, set1.contains("a"));
            assertEquals("check add message", true, set1.contains("c"));
        } catch (Exception ex) {
            
        } finally {
            set1.close();
            set2.close();
        }
        
        
    }
}
