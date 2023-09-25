package server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        final String address = "127.0.0.1";
        final int port = 23456;
        final String filename = "keyMap.data";
        int poolSize = Runtime.getRuntime().availableProcessors();
        Map<String, String> map;

        System.out.println("Server started!");

        try {
            map = (HashMap<String, String>) SerializationUtils.deserialize(filename);
        } catch (IOException | ClassNotFoundException e) {
            map = new HashMap<>();
        }

        System.out.println(map);

        while (true) {
            try (
                    ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address));
                    Socket socket = server.accept();
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream())
            ) {

                String request = input.readUTF();
                if (request.equals("exit")) {
                    socket.close();
                    break;
                }

                String action = request.split(" ")[0];
                String fileIdentifier = "";

                try {
                    fileIdentifier = request.split(" ")[1];
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }

                // For local testing
//                final String filesPath = System.getProperty("user.dir") + File.separator + "File Server" + File.separator + "task"
//                        + File.separator + "src" + File.separator + "server" + File.separator + "data" + File.separator;

                final String filesPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "server" + File.separator + "data" + File.separator;

                switch (action) {
                    case "GET" -> {
                        Get get = new Get(fileIdentifier, filesPath, map, output);

                        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
                        executor.submit(get);
                        executor.shutdown();

                        try {
                            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    case "PUT" -> {
                        Put put = new Put(fileIdentifier, filesPath, map, input, output);

                        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
                        executor.submit(put);
                        executor.shutdown();

                        try {
                            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    case "DELETE" -> {
                        String fileName = map.getOrDefault(fileIdentifier, fileIdentifier); // to handle action request by filename
                        File file = new File(filesPath + fileName);

                        if (file.delete()) {
                            output.writeUTF("200");
                            if (fileIdentifier.matches("\\d")) {
                                map.remove(fileIdentifier);
                            } else {
                                map.values().remove(fileIdentifier);
                            }

                        } else {
                            output.writeUTF("404");
                        }

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                SerializationUtils.serialize(map, filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}