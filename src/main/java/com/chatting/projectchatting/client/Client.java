package com.chatting.projectchatting.client;

import com.chatting.projectchatting.domain.Message;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class Client extends Thread{

    public static final String HOST_NAME = "localhost";

    private Sender sender;
    private Receiver receiver;
    private final Button button1;
    private final Button button2;
    private final TextArea textArea;
    private final int port;

    private final TextArea currentUserArea;

    private final String userName;


    public Client(Button button1, Button button2, TextArea textArea, int port, String userName, TextArea currentUserArea) {
        this.sender = null;
        this.receiver = null;
        this.button1 = button1;
        this.button2 = button2;
        this.textArea = textArea;
        this.port = port;
        this.userName = userName;
        this.currentUserArea = currentUserArea;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(HOST_NAME, port));
            sender = new Sender(socket);
            receiver = new Receiver(socket, this);
            send(Message.firstMessage(userName));
            receiver.start();
            button1.setDisable(true);
            button2.setDisable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String sender, String text) {
        this.sender.send(Message.text(sender, text));
    }

    public void send(Message message) {
        this.sender.send(message);
    }

    public void receiveMessage(Message message) {
        String getText = message.toString();
        textArea.setText(textArea.getText() + ProfanityFilter.filter(getText) +"\n");
    }

    public void setCurrentUser(List<String> currentUser) {
        StringBuilder sb = new StringBuilder();
        for (String userName : currentUser) {
            sb.append(userName).append("\n");
        }
        currentUserArea.setText(sb.toString());
    }

    public void close(){
        System.out.println("Close --- ");
        this.sender.send(Message.logout());
    }
}
