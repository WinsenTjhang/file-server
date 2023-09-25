package server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Get implements Runnable{
    String fileName;
    File file;
    DataOutputStream output;
    String filesPath;

    public Get(String fileIdentifier, String filesPath, Map<String, String> map, DataOutputStream output) {
        this.fileName = map.getOrDefault(fileIdentifier, fileIdentifier);
        this.file = new File(filesPath + fileName);
        this.output = output;
        this.filesPath = filesPath;
    }

    @Override
    public void run() {
        try {
            if (file.isFile()) {
                output.writeUTF("200");

                byte[] message = Files.readAllBytes(Path.of(filesPath + fileName));
                output.writeInt(message.length);
                output.write(message);
            } else {
                output.writeUTF("404");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
