import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerFTP {

    public static void main(String[] args) {

        try {

            ServerSocket serverSocket = new ServerSocket(2121);
            System.out.println("Attente de connexion sur le port 2121 ...");

            while (true) {
                Socket socket = serverSocket.accept();
                clientThread ct = new clientThread(socket, serverSocket);
                ct.start();
            }

        } catch (IOException e) {
            System.err.println("Erreur : " + e);
            e.printStackTrace();
        }
    }

    private static class clientThread extends Thread {
        public clientThread(Socket socket, ServerSocket serverSocket) {
            System.out.println("Client connecté.");
            try ( InputStream in = socket.getInputStream();
            Scanner scanner = new Scanner(in);
            OutputStream out = socket.getOutputStream() ) {
            
                out.write("220 Service ready\r\n".getBytes());
                
                String username = scanner.nextLine();
                out.write("331 User name ok, need password\r\n".getBytes());
                String password = scanner.nextLine();

                System.out.println(username);
                System.out.println(password);
                

                if (userAuth(username, password)) {
                    out.write("230 User logged in\r\n".getBytes());
                } else {
                    out.write("530 Login incorrect\r\n".getBytes());
                    closeCon(scanner, in, out, socket, serverSocket);
                    return;
                }

                
                String clientCommand;
                do {
                    clientCommand = scanner.nextLine();
                
                    switch (clientCommand) {
                        case "quit":
                            out.write("221 Disconnected.\r\n".getBytes());
                            closeCon(scanner, in, out, socket,serverSocket);
                            break;
                        case "get":
                        
                            break;
                        case "dir":
                
                            break;
                        case "cd":

                            break;
                        default:
                            out.write("500 Commande non reconnue.\r\n".getBytes());
                    }
                
                } while (!clientCommand.equalsIgnoreCase("quit"));

            } catch (Exception e) {
                System.err.println("Erreur : " + e);
                e.printStackTrace();
            }
        }
    }


    private static boolean userAuth(String username, String password) {
        return username.equals("USER metzo") && password.equals("PASS ap");
    }

    private static void closeCon(Scanner scanner, InputStream in, OutputStream out, Socket socket, ServerSocket serverSocket) throws IOException {
        scanner.close();
        in.close();
        out.close();
        socket.close();
        serverSocket.close();
    }
}
