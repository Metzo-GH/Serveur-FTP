import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            Scanner scanner = new Scanner(in);

            out.write("220 Service ready".getBytes());

             String login = scanner.nextLine();
            out.write("331 User name ok, need password".getBytes());
            out.write(login.getBytes());

            out.write("331 Enter password".getBytes());
            String password = scanner.nextLine();
            out.write(password.getBytes());

            if (isValidUser(login, password)) {
                out.write("230 User logged in".getBytes());
            } else {
                out.write("530 Login incorrect".getBytes());
                scanner.close();
                return;
            }


            String clientCommand;
            do {
                out.write("220 Entrez une commande (quit pour se déconnecter): ".getBytes());
                clientCommand = scanner.nextLine();

                switch (clientCommand.toLowerCase()) {
                    case "quit":
                        System.out.println("221 Déconnexion.");
                        break;
                    case "get":

                        break;
                    case "dir":

                        break;
                    case "cd":

                        break;
                    default:
                        out.write("500 Commande non reconnue.".getBytes());
                }

            } while (!clientCommand.equalsIgnoreCase("quit"));


            in.close();
            out.close();
            socket.close();
            scanner.close();

        } catch (IOException e) {
            System.err.println("Erreur : " + e);
            e.printStackTrace();
        }
    }

    private boolean isValidUser(String login, String password) {
        return login.equals("USER metzo") && password.equals("PASS a");
    }
}
