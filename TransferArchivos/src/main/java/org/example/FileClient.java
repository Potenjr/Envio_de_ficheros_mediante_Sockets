package org.example;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FileClient {
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado al servidor.");

            while (true) {
                System.out.println("Comandos disponibles: UPLOAD, DOWNLOAD, LIST, EXIT");
                System.out.print("Ingrese un comando: ");
                String command = scanner.nextLine();

                if (command.equalsIgnoreCase("UPLOAD")) {
                    uploadFile(dis, dos, scanner);
                } else if (command.equalsIgnoreCase("DOWNLOAD")) {
                    downloadFile(dis, dos, scanner);
                } else if (command.equalsIgnoreCase("LIST")) {
                    listFiles(dis, dos);
                } else if (command.equalsIgnoreCase("EXIT")) {
                    dos.writeUTF("EXIT");
                    break;
                } else {
                    System.out.println("Comando no reconocido.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void uploadFile(DataInputStream dis, DataOutputStream dos, Scanner scanner) throws IOException {
        System.out.print("Ingrese la ruta del archivo a subir: ");
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (file.exists()) {
            dos.writeUTF("UPLOAD");
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());

            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("Archivo subido: " + file.getName());
        } else {
            System.out.println("Archivo no encontrado.");
        }
    }

    private static void downloadFile(DataInputStream dis, DataOutputStream dos, Scanner scanner) throws IOException {
        System.out.print("Ingrese el nombre del archivo a descargar: ");
        String fileName = scanner.nextLine();
        dos.writeUTF("DOWNLOAD");
        dos.writeUTF(fileName);

        String response = dis.readUTF();
        if (response.equals("EXISTS")) {
            long fileSize = dis.readLong();
            System.out.println("Descargando archivo: " + fileName + " (" + fileSize + " bytes)");

            // Verificar si el directorio de descargas existe, si no, crear uno
            File downloadDir = new File("downloads");
            if (!downloadDir.exists()) {
                if (downloadDir.mkdirs()) {
                    System.out.println("Directorio 'downloads' creado.");
                } else {
                    System.out.println("Error al crear el directorio 'downloads'.");
                    return; // Salir si no se puede crear el directorio xq ya esta creado
                }
            }

            // Guardar el archivo en la carpeta de descargas
            try (FileOutputStream fos = new FileOutputStream("downloads/" + fileName);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while (fileSize > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    fileSize -= bytesRead;
                }
            }
            System.out.println("Archivo descargado: " + fileName);
        } else {
            System.out.println("Archivo no encontrado en el servidor.");
        }
    }

    private static void listFiles(DataInputStream dis, DataOutputStream dos) throws IOException {
        dos.writeUTF("LIST");

        // Espera la respuesta del servidor
        String response = dis.readUTF();
        if (response.equals("LIST_START")) {
            int fileCount = dis.readInt();
            if (fileCount > 0) {
                System.out.println("Archivos en el servidor:");
                for (int i = 0; i < fileCount; i++) {
                    System.out.println(dis.readUTF());
                }
            } else {
                System.out.println("No hay archivos en el servidor.");
            }
        } else {
            System.out.println("Error al listar archivos.");
        }
    }
}
