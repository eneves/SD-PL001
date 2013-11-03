/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.Agenda;
import common.Event;
import common.Message;
import common.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
/**
 *
 * @author João
 */
public class Client {

    final private Socket socket;
    final private ObjectOutputStream out;
    final private ObjectInputStream in;
    private Agenda agenda;

    public Client(String hostname, int portNumber) throws java.net.UnknownHostException, IOException {
        this.socket = new Socket(hostname, portNumber);
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.in = new ObjectInputStream(this.socket.getInputStream());
        this.agenda = new Agenda();
    }

    public Message addEvent(Event e) {
        return new Message(MessageType.CSAdd, e);
    }

    public Message updateEvent(Event e) {
        return new Message(MessageType.CSUpdate, e);
    }

    public Message deleteEvent(int id) {
        return new Message(MessageType.CSDelete, getEvent(id));
    }

    public Message findEvent(Event e) {
        return new Message(MessageType.CSFind, e);
    }

    public Event getEvent(int id) {
        Event event = null;
        for (Event e : agenda.getEvents()) {
            if (e.getId() == id) {
                event = e;
                break;
            }
        }
        return event;
    }

    public void processMessage(Message m) {
        try {
            this.out.writeObject(m);
        } catch (IOException ex) {
            System.err.println("Error sending message");
        }
        try {
            m = (Message) this.in.readObject();
            processReceivedMessage(m);
        } 
        catch (ClassNotFoundException c) {
            System.err.println("Server sent an invalid object");
        } 
        catch (IOException e) {
            System.err.println("Error reading response message");
        }
    }
    public void processReceivedMessage(Message msg) {
        switch (msg.getType()) {
            case SCAdd:
                setAgenda();
                break;
            case SCUpdate:
                setAgenda();
                break;
            case SCDelete:
                if(msg.getData() == 1){
                    System.out.println("O evento foi apagado com sucesso!");
                    setAgenda();
                }
                else
                    System.out.println("Ocorreu um erro e o evento não foi apagado!");
                break;
            case SCFind:
                agenda.setEvents((Set<Event>) msg.getData());
                break;

        }
    }
    public void setAgenda (){
        processMessage(findEvent(null));
    }

    public String showAgenda() {
        StringBuilder str = new StringBuilder();
        for (Event e : agenda.getEvents()) {
            str.append(e.toString());
            str.append("\n");
        }
        return str.toString();
    }
}
