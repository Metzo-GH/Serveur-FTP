import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerFTP {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(2121)){
            System.out.println("Attente de connexion sur le port 2121 ...");

            while (true) {
                Socket socket = serverSocket.accept();
                clientThread ct = new clientThread(socket);
                ct.start();
            }

        } catch (IOException e) {
            System.err.println("Erreur : " + e);
            e.printStackTrace();
        }
    }

    private static class clientThread extends Thread {
        private ServerSocket dataServerSocket;


        public clientThread(Socket socket) {
            System.out.println("Client connecté.");
            try (InputStream in = socket.getInputStream();
                 Scanner scanner = new Scanner(in);
                 OutputStream out = socket.getOutputStream()) {

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
                    closeCon(scanner, in, out, socket);
                    return;
                }

                String clientCommand;
                do {
                    clientCommand = scanner.nextLine();
                    System.out.println(clientCommand);
                    String[] commandParts = clientCommand.split("\\s+", 2);
                    String command = commandParts[0].toUpperCase();
                    String fileName = commandParts.length > 1 ? commandParts[1] : "";
                
                    switch (command) {
                        case "QUIT":
                            out.write("221 Disconnected.\r\n".getBytes());
                            closeCon(scanner, in, out, socket);
                            break;
                        case "SYST":
                            out.write("215 Remote system type is UNIX.\r\n".getBytes());
                            break;
                        case "EPSV":
                            fileEPSV(out);
                            break;
                        case "RETR":
                            fileRetr(fileName, out);
                            break;
                        case "LIST":
                            fileDir(out);
                            break;
                        case "CWD":
                        case "xCWD":
                            fileCd(fileName, out);
                            break;
                        default:
                            out.write("500 Commande non reconnue\r\n".getBytes());
                    }
                } while (!clientCommand.equalsIgnoreCase("QUIT"));

            } catch (Exception e) {
                System.err.println("Erreur : " + e);
                e.printStackTrace();
            } finally {
                closeCon(null, null, null, socket);
            }
        }

        private static boolean userAuth(String username, String password) {
            return username.equals("USER metzo") && password.equals("PASS ap");
        }

        private static void closeCon(Scanner scanner, InputStream in, OutputStream out, Socket socket) {
            try {
                if (scanner != null) scanner.close();
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture : " + e);
            }
        }



        public void fileEPSV(OutputStream out) {
            try {
                if (dataServerSocket != null && !dataServerSocket.isClosed()) {
                    dataServerSocket.close();
                }
                
                dataServerSocket = new ServerSocket(0);
                int port = dataServerSocket.getLocalPort();
        
                String mes = "229 Entering Extended Passive Mode (|||" + port + "|)\r\n";
                out.write(mes.getBytes());
            } catch (Exception e) {
                System.err.println("Error : " + e);
            }
        }
        

        public void fileRetr(String fileName, OutputStream out) throws IOException {

                File file = new File("storage/" + fileName);
                System.out.println("The filename : " + fileName);
        
                if (!file.exists()) {
                    out.write("550 File not found\r\n".getBytes());
                    return;
                }
        
                long fileSize = file.length();
        
                try (Socket dataSocket = dataServerSocket.accept();
                     FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream dataOut = dataSocket.getOutputStream()) {
        
                    byte[] buffer = new byte[1024];
                    int bytesRead;
        
                    out.write(("150 Opening data connection for " + fileName + " (" + fileSize + " bytes)\r\n").getBytes());
        
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        dataOut.write(buffer, 0, bytesRead);
                    }
        
                    out.write("226 Transfer complete.\r\n".getBytes());
                    dataSocket.close();
                    dataServerSocket.close();
                }
        }
        
        public void fileDir(OutputStream out) throws IOException {
            File directoryCurrent = new File("storage/");
            File[] files = directoryCurrent.listFiles();
        
            if (files == null) {
                out.write("550 Directory not found\r\n".getBytes());
                return;
            } else {
                out.write("150 Here comes the directory listing\r\n".getBytes());

                for (File file : files) {
                    if (file.isDirectory()) {
                        out.write(("D " + file.getName() + "\r\n").getBytes());
                    } else if (file.isFile()) {
                        out.write(("F " + file.getName() + "\r\n").getBytes());
                    }
                }
                out.write("226 Directory send OK.\r\n".getBytes());
            }
        }

        

        private void fileCd(String directoryName, OutputStream out) throws IOException {
            File newDirectory = new File("storage/" + directoryName);
        
            if (newDirectory.exists() && newDirectory.isDirectory()) {
                if (newDirectory.getAbsolutePath().startsWith(new File("storage/").getAbsolutePath())) {
                    out.write("250 Directory changed successfully\r\n".getBytes());
                } else {
                    out.write("550 Permission denied\r\n".getBytes());
                }
            } else {
                out.write("550 Directory not found\r\n".getBytes());
            }
        }
        

    }
}