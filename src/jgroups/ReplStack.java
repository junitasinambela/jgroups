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
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

/**
 *
 * @author user
 */
public class ReplStack extends ReceiverAdapter{
    JChannel channel;
    String user_name=System.getProperty("user.name", "n/a");
    final List<String> state=new LinkedList<>();
    public Stack<String> stackString;
    
    ReplStack(){
        stackString = null;
    }
    private void push(String element){
        stackString.add(element);
    }
    private String pop(){
        return stackString.pop();
    }
    private String top(){
        return stackString.peek();
    }
    private void start() throws Exception {
        channel=new JChannel();
        channel.setReceiver(this);
        channel.connect("ChatCluster");
        channel.getState(null, 10000);
        eventLoop();
        channel.close();
    }
    
    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

//    @Override
//    public void receive(Message msg) {
//        String line=msg.getSrc() + ": " + msg.getObject();
//        System.out.println(line);
//        synchronized(state) {
//            state.add(line);
//        }
//    }
    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized(state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }
    
    @Override
    public void setState(InputStream input) throws Exception {
        List<String> list;
        list=(List<String>)Util.objectFromStream(new DataInputStream(input));
        for(String a : list){
            stackString.push(a);
        }
        synchronized(state) {
            state.clear();
            state.addAll(stackString);
        }
    }
    private void eventLoop() {
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("> ");
                System.out.flush();
                String line = in.readLine().toLowerCase();
                if(line.startsWith("quit") || line.startsWith("exit"))
                    break;
                
            }
            catch(Exception e) {
                
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new ReplStack().start();
    }
}
