package Server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {

    private static PrintWriter printWriter;
    private static Statement statement;
    private static ArrayList<PrintWriter> streams;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        go();
    }

    private static void go() throws SQLException, ClassNotFoundException {
        streams = new ArrayList<PrintWriter>();
        setDB();
        try {
            ServerSocket serverSocket = new ServerSocket(4002);
            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println("Got user");
                printWriter = new PrintWriter(socket.getOutputStream());
                streams.add(printWriter);
                sendHistory();

                Thread thread = new Thread(new Listener(socket));
                thread.start();
            }
        } catch (Exception e) {
        }
    }

    private static void sendHistory() throws SQLException {

        ResultSet resultSet = statement.executeQuery("SELECT login, message FROM messenger.chat");

        while (resultSet.next()) {
            //printWriter.println(resultSet.getString("message"));

            printWriter.println(resultSet.getString("login")+": "+resultSet.getString("message"));
            printWriter.flush();
        }
    }

    public static void tellEveryone(String message) throws SQLException, ClassNotFoundException {
        int x = message.indexOf(':');
        String login = message.substring(0, x);
        String mess = message.substring(x + 2, message.length() - (x - 2));

        save(login, mess);

        Iterator<PrintWriter> iterator = (Iterator) streams.iterator();
        try {
            while (iterator.hasNext()) {
                printWriter = iterator.next();
                printWriter.println(message);
                printWriter.flush();
            }
        } catch (Exception e) {
        }
    }

    private static void save(String login, String message) throws SQLException, ClassNotFoundException {
        setDB();

        String SQL = "INSERT INTO chat (login, message) VALUES ('" + login + "', '" + message + "')";
        statement.executeUpdate(SQL);
    }

    private static void setDB() throws ClassNotFoundException, SQLException {
        String name = "root";
        String pass = "1111";
        String connectionUrl = "jdbc:mysql://localhost:3306/messenger";
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(connectionUrl, name, pass);
        statement = connection.createStatement();

    }


    private static class Listener implements Runnable {
        BufferedReader bufferedReader;

        Listener(Socket socket) {
            InputStreamReader ISReader;
            try {
                ISReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(ISReader);
            } catch (IOException e) {
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = bufferedReader.readLine()) != null) {
                    System.out.println(message);
                    tellEveryone(message);
                }
            } catch (Exception e) {
            }
        }
    }

}