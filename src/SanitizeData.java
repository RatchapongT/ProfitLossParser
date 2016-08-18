import java.io.*;
import java.util.*;

/**
 * Created by Zanko on 8/16/2016.
 */
public class SanitizeData {
    private static Map<String, String> dataMap = new HashMap<>();
    private static List<String> rows = new ArrayList<>();

    public static void main(String[] args) {
        try {
            String sCurrentLine;
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Input/NewDataMapping.txt"));
            bufferedReader.readLine();
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                String[] arr = sCurrentLine.split(",");
                String mode = arr[0].trim();
                String newMode = arr[1].trim();
                String newManager = arr[1].trim();
                String workerNickname = arr[3].trim();
                String newWorkerNickname = arr[4].trim();
                String newWorkerUsername = arr[5].trim();
                String externalPartyNickname = arr[6].trim();
                String newExternalPartyNickname = arr[7].trim();
                String externalPartyUsername = arr[8].trim();
                String newExternalPartyUsername = arr[9].trim();
                String type = arr[10].trim();
                String oldData = mode + "*" + workerNickname + "*" + externalPartyNickname + "*" + externalPartyUsername + "*" + type;
                String newData = newMode + "*" + newManager + "*" + newWorkerNickname + "*" + newWorkerUsername + "*" + newExternalPartyNickname + "*" + newExternalPartyUsername;
                dataMap.put(oldData, newData);
            }
            String[] files = new String[]{"Malay-Customer.txt", "Malay-Partner.txt", "Thai-Customer.txt", "Thai-Partner.txt"};
            for (String fileName : files) {
                bufferedReader = new BufferedReader(new FileReader("./Output/" + fileName));
                bufferedReader.readLine();
                while ((sCurrentLine = bufferedReader.readLine()) != null) {
                    String[] arr = sCurrentLine.split(",");
                    String Mode = arr[0].trim();
                    String Date = arr[1].trim();
                    String ManagerNickname = arr[2].trim();
                    String WorkerNickname = arr[3].trim();
                    String Number = arr[4].trim();
                    String ExternalPartyNickname = arr[5].trim();
                    String ExternalPartyUsername = arr[6].trim();
                    String MoneyIn = fileName.indexOf("Partner") > -1 ? arr[8].trim() : arr[7].trim();
                    String MoneyOut = fileName.indexOf("Partner") > -1 ? arr[7].trim() : arr[8].trim();
                    String Type = arr[9].trim();
                    String query = Mode + "*" + WorkerNickname + "*" + ExternalPartyNickname + "*" + ExternalPartyUsername + "*" + Type;
                    //System.out.println(query);
                    if (dataMap.containsKey(query)) {
                        String[] newData = dataMap.get(Mode + "*" + WorkerNickname + "*" + ExternalPartyNickname + "*" + ExternalPartyUsername + "*" + Type).split("\\*");
                        String newMode = newData[0].trim();
                        String newManager = newData[2].trim();
                        String newWorkerNickname = newData[2].trim();
                        String newWorkerUsername = newData[3].trim();
                        String newExternalPartyNickname = newData[4].trim();
                        String newExternalPartyUsername = newData[5].trim();
                        rows.add(newMode + ", " + Date + ", " + newManager + ", " + newWorkerNickname + ", " + newWorkerUsername + ", " + newExternalPartyNickname + ", " + newExternalPartyUsername + ", " + MoneyIn + ", " + MoneyOut + ", " + Type+ "\n");
                    }
                }

            }
            try {
                File file = new File("./Output/PLInfoSanitized.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
//                    bw.write("Customer, Date, Manager Nickname, Worker Nickname, Number, Customer Nickname, Customer ID, Money In, Money Out, Type\n");
                for (String toWriteString : rows) {
                    bw.write(toWriteString);
                }
                bw.close();
                System.out.println("Finished writing file: " + "PLInfoSanitized.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public void writeFile(List<String> rows, String filename, String mode, String header) {
//        try {
//            File file = new File("./Output/" + filename + "-" + mode + ".txt");
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            FileWriter fw = new FileWriter(file.getAbsoluteFile());
//            BufferedWriter bw = new BufferedWriter(fw);
//            bw.write(header);
//            for (String toWriteString : rows) {
//                bw.write(toWriteString);
//            }
//            bw.close();
//            System.out.println("Finished writing file: " + filename + "-" + mode + ".txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
