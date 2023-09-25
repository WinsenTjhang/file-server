package server;

import java.util.Map;

public class FilesTracker {
    static String uniqueID;
    static String generateUID(Map<String, String> map) {
        for (int i = 1; i < 1000; i++) {
            if (!map.containsKey(String.valueOf(i))) {
                return String.valueOf(i);
            }
        }
        return null;
    }

    static void assignMapIdentifier(Map<String, String> map, String fileIdentifier) {
        uniqueID = generateUID(map);
        if (fileIdentifier.isBlank()) {
            map.put(uniqueID, uniqueID);
        } else {
            map.put(uniqueID, fileIdentifier);
        }
    }

    public static String getUniqueID() {
        return uniqueID;
    }
}
