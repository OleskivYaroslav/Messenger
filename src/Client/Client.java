package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private static JTextArea textAreaa;
    private static JTextField textField;
    private static BufferedReader reader;
    private static PrintWriter writer;
    private static String login;

    public static  void  main(String[] args){
        go();
    }

    private static void go(){
        login = JOptionPane.showInputDialog("Inpute your name");
        JFrame frame = new JFrame("Messenger 1.0");
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        textAreaa =  new JTextArea(15, 30);
        textAreaa.setLineWrap(true);
        textAreaa.setWrapStyleWord(true);
        textAreaa.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textAreaa);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textField = new JTextField(20);
        JButton sendButton = new JButton("send");

        sendButton.addActionListener(new Send());

        panel.add(scrollPane);
        panel.add(textField);
        panel.add(sendButton);
        setNet();

        Thread thread = new Thread(new Listener());
        thread.start();

        frame.getContentPane().add(BorderLayout.CENTER, panel);

        frame.setSize(400, 315);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }


    private static class Listener implements Runnable{
        @Override
        public void run() {
            String message;
            try{
                while ((message=reader.readLine())!=null){
                    textAreaa.append(message+'\n');
                }
            }catch (Exception e){}
        }
    }


    private static class Send implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = login + ": "+ textField.getText();
            writer.println(message);
            writer.flush();

            textField.setText("");
            textField.requestFocus();
        }
    }

    private static void setNet() {
        try {
            Socket socket = new Socket("127.0.0.1", 4002);
            InputStreamReader ISReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(ISReader);
            writer= new PrintWriter(socket.getOutputStream());
        }catch (Exception e){}
    }
}
