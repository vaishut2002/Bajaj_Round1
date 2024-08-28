
import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonHasher {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java -jar test.jar <PRN Number> <path to JSON file>");
            System.exit(1);
        }

        String prnNumber = args[0];
        String jsonFile = args[1];

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(jsonFile));

        String destinationValue = getDestinationValue(jsonObject);

        String randomString = generateRandomString(8);

        String concatenatedString = prnNumber + destinationValue + randomString;
        String md5Hash = getMD5Hash(concatenatedString);

        System.out.println(md5Hash + ";" + randomString);
    }

    private static String getDestinationValue(JSONObject jsonObject) {
        if (jsonObject.containsKey("destination")) {
            return jsonObject.get("destination").toString();
        }

        for (Object key : jsonObject.keySet()) {
            if (jsonObject.get(key) instanceof JSONObject) {
                String value = getDestinationValue((JSONObject) jsonObject.get(key));
                if (value != null) {
                    return value;
                }
            }
        }

        return null;
    }

    private static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return sb.toString();
    }

    private static final char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private static String getMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
