import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            // Flux de lecture et écriture pour communiquer avec le client
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Envoyer le message "220 Service ready" une fois la connexion établie
            out.println("220 Service ready");

            // Demander le nom d'utilisateur
            out.println("331 User name ok, need password");
            String login = in.readLine().trim();
            System.out.println(login);
            System.in.read();

            // Demander le mot de passe
            out.println("331 Enter password");
            String password = in.readLine().trim();
            System.out.println(password);

            // Vérifier les informations d'identification
            if (isValidUser(login, password)) {
                out.println("230 User logged in");
            } else {
                out.println("530 Login incorrect");
                return;
            }

            // Boucle de gestion des commandes FTP
            String clientCommand;
            do {
                out.println("220 Entrez une commande (quit pour se déconnecter): ");
                clientCommand = in.readLine().trim();

                // Implémenter la logique pour les commandes FTP
                switch (clientCommand.toLowerCase()) {
                    case "quit":
                        System.out.println("221 Déconnexion.");
                        break;
                    case "get":
                        // Ajoutez ici la logique pour la commande 'get'
                        // out.println("200 Commande GET traitée avec succès");
                        break;
                    case "dir":
                        // Ajoutez ici la logique pour la commande 'dir'
                        // out.println("200 Commande DIR traitée avec succès");
                        break;
                    case "cd":
                        // Ajoutez ici la logique pour la commande 'cd'
                        // out.println("200 Commande CD traitée avec succès");
                        break;
                    default:
                        out.println("500 Commande non reconnue.");
                }

            } while (!clientCommand.equalsIgnoreCase("quit"));

            // Fermer les flux et la connexion
            in.close();
            out.close();
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidUser(String login, String password) {
        // Vérifier les informations d'identification ici (à remplacer par une logique réelle)
        return login.equals("USER metzo") && password.equals("PASS a");
    }
}
