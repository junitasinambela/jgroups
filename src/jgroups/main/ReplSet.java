/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgroups.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

/**
 *
 * @author user
 */
public class ReplSet extends ReceiverAdapter{
    JChannel channel;
    String user_name=System.getProperty("user.name", "n/a");
    final Set replSet;
    
    ReplSet(){
        replSet = new HashSet();
    }
    
    private void start() throws Exception {
        channel=new JChannel();
        channel.setReceiver(this);
        channel.connect("SetCluster");
        channel.getState(null, 10000);
        eventLoop();
        channel.close();
    }
    
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }
    
    public void getState(OutputStream output) throws Exception {
        synchronized(replSet) {
            Util.objectToStream(replSet, new DataOutputStream(output));
        }
    }
    
    public void setState(InputStream input) throws Exception {
        Set list;
        list=(Set)Util.objectFromStream(new DataInputStream(input));
        synchronized(replSet) {
            replSet.clear();
            replSet.addAll(list);
        }
    }
    
    public void receive(Message msg) {
        String command = (String) msg.getObject();
        String element = command.split(" ",2)[1];
        
        if(command.startsWith("add")) {
            add(element);
            System.out.println("Received add command, ReplSet :" + replSet.toString());
            System.out.print("> ");
        } else if (command.startsWith("remove")) {
            remove(element);
            System.out.println("Received remove command, ReplSet :" + replSet.toString());
            System.out.print("> ");
        }

    }
    
    private void eventLoop() throws Exception {
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("> ");
                System.out.flush();
                String line = in.readLine().toLowerCase();
                if(line.startsWith("quit") || line.startsWith("exit"))
                    break;
                if(line.startsWith("add")){
                    String[] l = line.split("add", 2);
                    //System.out.println("add " + l[1]);
                    Message msg = new Message(null, null, line);
                    channel.send(msg);
                }
                else if(line.startsWith("remove")){
                    String[] l = line.split("remove", 2);
                    //System.out.println("remove " + l[1]);
                    Message msg = new Message(null, null, line);
                    channel.send(msg);
                }
                else if(line.startsWith("contains")) {
                    String[] l = line.split(" ",2);
                    System.out.println(contains(l[1]));
                }
                else {
                    System.out.println("Usage : ");
                    System.out.println("- add <element>");
                    System.out.println("- remove <element>");
                    System.out.println("- contains <element>");
                    System.out.println("- quit/exit");
                }
                
            }
            catch(Exception e) {
                
            }
        }
    }
    
    private <E> boolean add(E element){
        synchronized(replSet){
            return replSet.add(element);
        }
    }
    private <E> boolean contains(E element){
        synchronized(replSet){
            return replSet.contains(element);
        }
    }
    private <E> boolean remove(E element){
        synchronized(replSet){
            return replSet.remove(element);
        }
    }
    
    public static void main(String[] args) throws Exception {
        new ReplSet().start();
    }
}
