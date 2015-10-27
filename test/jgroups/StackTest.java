package jgroups;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import jgroups.main.ReplStack;
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
public class StackTest {
    
    public StackTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testSet(){
        ReplStack rep1 = new ReplStack();
        ReplStack rep2 = new ReplStack();
        try {
            rep1.init();
            Thread.sleep(1000);
            rep1.handleCmd("a");
            Thread.sleep(1000);
            assertEquals("Checking top 1 : ", "a", (String)rep1.top());
            rep1.handleCmd("b");
            Thread.sleep(1000);
            assertEquals("Checking top 1 : ", "b", (String)rep1.top());
            rep1.handleCmd("pop");
            Thread.sleep(1000);
            assertEquals("Checking top 1 : ", "a", (String)rep1.top());
            rep1.handleCmd("c");
            Thread.sleep(1000);
            rep2.init();
            Thread.sleep(1000);
            assertEquals("Checking top 2 : ", "c", (String)rep2.top());
            rep2.handleCmd("pop");
            Thread.sleep(1000);
            assertEquals("Checking top 1 : ", "a", (String)rep1.top());
            assertEquals("Checking top 2 : ", "a", (String)rep2.top());
            rep1.closeConn();
            rep2.closeConn();
        } catch (Exception ex) {
            
        }
    }
}
