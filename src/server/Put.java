package server;

import java.io.*;
import java.util.Map;

public class Put implements Runnable {
    DataInputStream input;
    DataOutputStream output;
    String filesPath;
    String fileIdentifier;
    String uniqueID;
    Map<String, String> map;

    public Put(String fileIdentifier, String filesPath, Map<String, String> map, DataInputStream input, DataOutputStream output) {
        this.fileIdentifier = fileIdentifier;
        this.map = map;
        this.input = input;
        this.output = output;
        this.filesPath = filesPath;
    }

    @Override
    public void run() {
        try {
            FilesTracker.assignMapIdentifier(map, fileIdentifier);

            uniqueID = String.valueOf(FilesTracker.getUniqueID());
            if (fileIdentifier.isBlank()) fileIdentifier = uniqueID;

            int length = input.readInt();
            byte[] message = new byte[length];
            input.readFully(message, 0, message.length);

            File file = new File(filesPath + fileIdentifier);
            try (PrintWriter printWriter = new PrintWriter(file)) {
                printWriter.print(new String(message));
                System.out.println("File saved on the hard drive!");
            } catch (IOException e) {
                e.printStackTrace();
            }

            output.writeUTF("200 " + uniqueID);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
