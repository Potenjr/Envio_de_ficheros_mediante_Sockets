package org.example;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class FileServer {
    private static final int PORT = 12345;
    private static final String SERVER_STORAGE_PATH = "server_files/";

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado en el puerto " + PORT);

            // Crear la carpeta para los archivos si no existe
            File serverDir = new File(SERVER_STORAGE_PATH);
            if (!serverDir.exists()) {
                serverDir.mkdir();
            }

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                 DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

                while (true) {
                    String command = dis.readUTF();
                    if (command.equals("UPLOAD")) {
                        receiveFile(dis);
                    } else if (command.equals("DOWNLOAD")) {
                        sendFile(dis, dos);
                    } else if (command.equals("LIST")) {
                        listFiles(dos);
                    } else if (command.equals("EXIT")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Cliente desconectado: " + clientSocket.getInetAddress());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void receiveFile(DataInputStream dis) throws IOException {
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();

            File file = new File(SERVER_STORAGE_PATH + fileName);
            try (FileOutputStream fos = new FileOutputStream(file);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while (fileSize > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    fileSize -= bytesRead;
                }
            }
            System.out.println("Archivo recibido: " + fileName);
        }

        private void sendFile(DataInputStream dis, DataOutputStream dos) throws IOException {
            String fileName = dis.readUTF();
            File file = new File(SERVER_STORAGE_PATH + fileName);

            if (file.exists()) {
                dos.writeUTF("EXISTS");
                dos.writeLong(file.length());

                try (FileInputStream fis = new FileInputStream(file);
                     BufferedInputStream bis = new BufferedInputStream(fis)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        dos.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("Archivo enviado: " + fileName);
            } else {
                dos.writeUTF("NOT_FOUND");
            }
        }

        private void listFiles(DataOutputStream dos) throws IOException {
            File folder = new File(SERVER_STORAGE_PATH);
            File[] listOfFiles = folder.listFiles();

            dos.writeUTF("LIST_START"); // Enviar un mensaje que indica que la lista de archivos empieza

            if (listOfFiles != null) {
                dos.writeInt(listOfFiles.length);
                for (File file : listOfFiles) {
                    dos.writeUTF(file.getName());
                }
            } else {
                dos.writeInt(0);
            }
        }
    }
}
