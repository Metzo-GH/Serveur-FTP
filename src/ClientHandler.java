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

            // Votre code pour l'authentification et la gestion des commandes FTP ira ici

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
