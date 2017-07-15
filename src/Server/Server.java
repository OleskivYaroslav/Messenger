package Server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {

    private static ArrayList<PrintWriter> streams;

    public static void main(String[] args){
        go();
    }

    private static void go(){
        streams = new ArrayList<PrintWriter>();
        try {
            ServerSocket serverSocket = new ServerSocket(4002);
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("Got user");
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                streams.add(writer);

                Thread thread = new Thread(new Listener(socket));
                thread.start();
            }
        } catch (Exception e) {
        }
    }

    public static void tellEveryone(String message) {
        int x = message.indexOf(':');
        String login = message.substring(0, x);

        Iterator<PrintWriter> iterator = (Iterator) streams.iterator();
        try {
            while (iterator.hasNext()) {
                PrintWriter printWriter = iterator.next();
                printWriter.println(message);
                printWriter.flush();
            }
        } catch (Exception e) {
        }
    }

    private static class Listener implements Runnable{
        BufferedReader bufferedReader;

        Listener(Socket socket){
            InputStreamReader ISReader;
            try {
                ISReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(ISReader);
            } catch (IOException e) {}
        }

        @Override
        public void run() {
            String message;
            try{
                while ((message=bufferedReader.readLine())!=null){
                    System.out.println(message);
                    tellEveryone(message);
                }
            }catch (Exception e){}
        }
    }

}
