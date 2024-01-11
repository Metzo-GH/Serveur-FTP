import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerFTP {
    public void main(String[] args) {
        int portNumber = 2121;

        try (
            ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Attente connexion sur " + portNumber);

            
            Socket socket = serverSocket.accept();
            System.out.println("Connecté.");

            Thread clientThread = new Thread();

            Thread clientHandler = new Thread(new ClientHandler(socket));
            clientHandler.start();

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            String msgCon = "Username : ";
            out.write(msgCon.getBytes());

            Scanner scanner = new Scanner(in);
            String login = scanner.nextLine();
            System.out.print(login);

            String msgPass = "Password : ";
            out.write(msgPass.getBytes());

            String Password = scanner.nextLine();
            System.out.print(Password);
                
            if (verifUser(login, Password)) {
                System.out.println("230 User logged in");
            } else {
                System.out.println("530 Login incorrect");
                scanner.close();
                return;
            }

            String clientCommand;
            do {
                clientCommand = scanner.nextLine();
                switch (clientCommand.toLowerCase()) {
                    case "quit":
                        break;
                    case "get":
                        
                        break;
                    case "dir":
                        
                        break;
                    case "cd":
                        
                        break;
                    default:
                        System.out.println("Commande non reconnue.");
                }

            } while (!clientCommand.equalsIgnoreCase("quit"));


            in.close();
            out.close();
            scanner.close();
            socket.close();
            
        } catch (IOException e) {
            System.err.println("Erreur : " + e);
            e.printStackTrace();
        }
    }

    private boolean verifUser(String login, String password) {
        return login.equals("USER metzo") && password.equals("PASS a");
    }
}
