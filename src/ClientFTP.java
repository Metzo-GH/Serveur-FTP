import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientFTP {

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 2121);
                InputStream in = socket.getInputStream();
                Scanner scanner = new Scanner(in);
                OutputStream out = socket.getOutputStream()) {

            String response = scanner.nextLine();
            System.out.println("Server response: " + response);
            out.write("USER miage\r\n".getBytes());

            response = scanner.nextLine();
            System.out.println("Server response: " + response);
            out.write("PASS car\r\n".getBytes());

            response = scanner.nextLine();
            System.out.println("Server response: " + response);
            out.write("PING\r\n".getBytes());

            response = scanner.nextLine();
            System.out.println("Server response: " + response);
            response = scanner.nextLine();
            System.out.println("Server response: " + response);
            if (response.equals("PONG")) {
                out.write("200 PONG command ok\r\n".getBytes());
            } else {
                System.out.println("502 Unknown command");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
