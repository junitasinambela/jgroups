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
    final Stack stackString;
    
    public ReplStack(){
        stackString = new Stack<>();
    }
    public <E> void push(E element){
        synchronized(stackString){
            stackString.add(element);
        }
    }
    public <E> E pop(){
        synchronized(stackString){
            return (E) stackString.pop();
        }
    }
    public <E> E top(){
        synchronized(stackString){
               return (E) stackString.peek();
        }
    }
    
    
    public void init() throws Exception{
        channel=new JChannel();
        channel.setReceiver(this);
        channel.connect("ChatCluster");
        channel.getState(null, 10000);
    }
    
    private void start() throws Exception {
        init();
        eventLoop();
        closeConn();
    }
    
    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }
    
    private void handleMsg(String msg){
        if(msg.startsWith("pop")){
            pop();
        }
        else {
            push(msg);
        }
    }
    @Override
    public void receive(Message msg) {
        String a = (String) msg.getObject();
        handleMsg(a);
    }
    
    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized(stackString) {
            Util.objectToStream(stackString, new DataOutputStream(output));
        }
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
    private void eventLoop(){
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("> ");
                System.out.flush();
                String line = in.readLine().toLowerCase();
                if(line.startsWith("quit") || line.startsWith("exit"))
                    break;
                handleCmd(line);
            }
            catch(Exception e) {
                
            }
        }
    }
    
    public void handleCmd(String line) throws Exception{
        if(line.startsWith("top")){
            System.out.print(">> top : ");
            System.out.println((String)top());
        }
        else {
            Message msg=new Message(null, null, line);
            channel.send(msg);
        }
    }
    
    public void closeConn(){
        channel.close();
    }

    public static void main(String[] args) throws Exception {
        new ReplStack().start();
    }
}
