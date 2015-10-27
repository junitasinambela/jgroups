/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgroups;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import org.jgroups.Address;
import org.jgroups.JChannel;
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
    final List<Address> state=new LinkedList<>();
    final List<String> stackString;
    
    ReplSet(){
        stackString = new ArrayList<>();
    }
    private boolean add(String element){
        synchronized(stackString){
            return stackString.add(element);
        }
    }
    private boolean contains(String element){
        synchronized(stackString){
            return stackString.contains(element);
        }
    }
    private boolean remove(String element){
        synchronized(stackString){
            return stackString.remove(element);
        }
    }
    private void start() throws Exception {
        state.add(0, null);
        channel=new JChannel();
        channel.setReceiver(this);
        channel.connect("ChatCluster");
        eventLoop();
        channel.close();
    }
    
    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

//    @Override
//    public void receive(Message msg) {
//        Address a = msg.getSrc();
//        synchronized(state){
//            state.add(0, a);
//        }
//    }
    
    @Override
    public void getState(OutputStream output) throws Exception {
//        channel.startFlush(true);
        synchronized(stackString) {
            Util.objectToStream(stackString, new DataOutputStream(output));
        }
//        channel.stopFlush();
    }
    
    @Override
    public void setState(InputStream input) throws Exception {
        Stack<String> list;
        list=(Stack<String>)Util.objectFromStream(new DataInputStream(input));
        synchronized(stackString) {
            stackString.clear();
            stackString.addAll(list);
        }
    }
    private void eventLoop() throws Exception {
//        synchronized(state){
            channel.getState(null, 10000);
//        }
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("> ");
                System.out.flush();
                String line = in.readLine().toLowerCase();
                if(line.startsWith("quit") || line.startsWith("exit"))
                    break;
                if(line.startsWith("contains")){
                    String[] l = line.split("contains", 2);
                    System.out.println(contains(l[1]));
                }
                else if(line.startsWith("remove")){
                    String[] l = line.split("remove", 2);
                    System.out.println(remove(l[1]));
                }
                else {
                    add(line);
//                    Message msg=new Message(null, null, "");
//                    channel.send(msg);
                }
            }
            catch(Exception e) {
                
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new ReplSet().start();
    }
}
