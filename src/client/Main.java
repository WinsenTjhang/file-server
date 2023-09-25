package client;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        final String address = "127.0.0.1";
        final int port = 23456;
        final String filesPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "client" + File.separator + "data" + File.separator;

        // For local testing
//        final String filesPath = System.getProperty("user.dir") + File.separator + "File Server" + File.separator
//                + "task" + File.separator + "src" + File.separator + "client" + File.separator + "data" + File.separator;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            System.out.print("Enter action (1 - get a file, 2 - save a file, 3 - delete a file): ");
            String action = scanner.nextLine();

            if ("exit".equals(action)) {
                output.writeUTF("exit");
                System.out.println("The request was sent.");
            } else {

                switch (action) {
                    case "1" -> {
                        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");

                        switch (scanner.nextLine()) {
                            case "1" -> System.out.print("Enter name of the file: ");
                            case "2" -> System.out.print("Enter id: ");
                        }

                        String identifier = scanner.nextLine();

                        output.writeUTF("GET " + identifier);
                        System.out.println("The request was sent.");

                        String response = input.readUTF();

                        if (response.equals("200")) {
                            // --- File reading ---
                            int length = input.readInt();
                            byte[] message = new byte[length];
                            input.readFully(message, 0, message.length);
                            String fileContent = new String(message);

                            System.out.print("The file was downloaded! Specify a name for it: ");
                            String fileName = scanner.nextLine();

                            File file = new File(filesPath + fileName);
                            try (PrintWriter printWriter = new PrintWriter(file)) {
                                printWriter.print(fileContent);
                                System.out.println("File saved on the hard drive!");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            System.out.println("The response says that this file is not found!");
                        }
                    }

                    case "2" -> {
                        System.out.print("Enter name of the file: ");
                        String fileName = scanner.nextLine();

                        // For local testing
//                        Path path = Path.of(System.getProperty("user.dir") + File.separator + "File Server" + File.separator + "task" + File.separator + "src" + File.separator
//                                + "client" + File.separator + "data" + File.separator + fileName);

                        Path path = Path.of(System.getProperty("user.dir") + File.separator + "src" + File.separator
                                + "client" + File.separator + "data" + File.separator + fileName);

                        byte[] message = Files.readAllBytes(path);

                        System.out.print("Enter name of the file to be saved on server: ");
                        String nameOnServer = scanner.nextLine();

                        output.writeUTF("PUT " + nameOnServer);
                        output.writeInt(message.length);
                        output.write(message);

                        System.out.println("The request was sent.");

                        String[] response = input.readUTF().split(" ");
                        if (response[0].equals("200")) {
                            System.out.println("Response says that file is saved! ID = " + response[1]);
                        } else {
                            System.out.println("The response says that creating the file was forbidden!");
                        }
                    }

                    case "3" -> {
                        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");

                        switch (scanner.nextLine()) {
                            case "1" -> System.out.print("Enter name of the file: ");
                            case "2" -> System.out.print("Enter id: ");
                        }

                        String identifier = scanner.nextLine();

                        output.writeUTF("DELETE " + identifier);
                        System.out.println("The request was sent.");

                        String statusCode = input.readUTF();
                        if (statusCode.equals("200")) {
                            System.out.println("The response says that this file was deleted successfully!");
                        } else {
                            System.out.println("The response says that the file was not found!");
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
