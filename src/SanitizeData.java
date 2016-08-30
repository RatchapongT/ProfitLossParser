import java.io.*;
import java.util.*;

public class SanitizeData {
    private static Map<String, String> dataMap = new HashMap<>();
    private static List<String> rows = new ArrayList<>();

    public static class MoneyObject {
        public double moneyIn;
        public double moneyOut;

        public MoneyObject(double moneyIn, double moneyOut) {
            this.moneyIn = moneyIn;
            this.moneyOut = moneyOut;
        }
    }

    public static void main(String[] args) {
        try {
            String sCurrentLine;
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Input/NewDataMapping.txt"));
            bufferedReader.readLine();
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                String[] arr = sCurrentLine.split(",");
                String mode = arr[0].trim();
                String newMode = arr[1].trim().toUpperCase();
                String newManager = arr[2].trim().toUpperCase();
                String workerNickname = arr[3].trim();
                String newWorkerNickname = arr[4].trim().toUpperCase();
                String newWorkerUsername = arr[5].trim().toUpperCase();
                String externalPartyNickname = arr[6].trim();
                String newExternalPartyNickname = arr[7].trim().toUpperCase();
                String externalPartyUsername = arr[8].trim();
                String newExternalPartyUsername = arr[9].trim().toUpperCase();
                String type = arr[10].trim();
                String MW = arr[11].trim();
                String MP = arr[12].trim();
                String TW = arr[13].trim();
                String TP = arr[14].trim();
                String oldData = mode + "_" + workerNickname + "_" + externalPartyNickname + "_" + externalPartyUsername + "_" + type;
                String newData = newMode + "_" + newManager + "_" + newWorkerNickname + "_" + newWorkerUsername + "_" + newExternalPartyNickname + "_" + newExternalPartyUsername + "_" + MW + "_" + MP + "_" + TW + "_" + TP;
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
                    if (arr[5].indexOf(".0") > -1) {
                        ExternalPartyNickname = arr[5].substring(0, arr[5].indexOf(".0")).trim();
                    }
                    if (arr[6].indexOf(".0") > -1) {
                        ExternalPartyUsername = arr[6].substring(0, arr[6].indexOf(".0")).trim();
                    }

                    String Type = arr[9].trim();
                    String query = Mode + "_" + WorkerNickname + "_" + ExternalPartyNickname + "_" + ExternalPartyUsername + "_" + Type;
                    if (dataMap.containsKey(query)) {
                        String[] newData = dataMap.get(Mode + "_" + WorkerNickname + "_" + ExternalPartyNickname + "_" + ExternalPartyUsername + "_" + Type).split("_");
                        String newMode = newData[0].trim();
                        String newManager = newData[1].trim();
                        String newWorkerNickname = newData[2].trim();
                        String newWorkerUsername = newData[3].trim();
                        String newExternalPartyNickname = newData[4].trim();
                        String newExternalPartyUsername = newData[5].trim();
                        String MW = newData[6].trim();
                        String MP = newData[7].trim();
                        String TW = newData[8].trim();
                        String TP = newData[9].trim();
                        String MoneyIn = newMode.equals("P") ? arr[8].trim() : arr[7].trim();
                        String MoneyOut = newMode.equals("P") ? arr[7].trim() : arr[8].trim();
                        rows.add(newMode + ", " + Date + ", " + newManager + ", " + newWorkerNickname + ", " + newWorkerUsername + ", " + newExternalPartyNickname + ", " + newExternalPartyUsername + ", " + MoneyIn + ", " + MoneyOut + ", " + Type + ", " + MW + ", " + MP + ", " + TW + ", " + TP + "\n");
                    }
                }

            }

            try {
                List<String> newRows = new ArrayList<>();
                Map<String, MoneyObject> newMap = new HashMap<>();
                Map<String, ComputeSum.RNP> newMapRN = new HashMap<>();
                for (String row : rows) {
                    String[] filtered = row.split(",");
                    String newMode = filtered[0].trim();
                    String Date = filtered[1].trim();
                    String newManager = filtered[2].trim();
                    String newWorkerNickname = filtered[3].trim();
                    String newWorkerUsername = filtered[4].trim();
                    String newExternalPartyNickname = filtered[5].trim();
                    String newExternalPartyUsername = filtered[6].trim();
                    String MoneyIn = filtered[7].trim();
                    String MoneyOut = filtered[8].trim();
                    String Type = filtered[9].trim();
                    String MW = filtered[10].trim();
                    String MP = filtered[11].trim();
                    String TW = filtered[12].trim();
                    String TP = filtered[13].trim();
                    String key = newMode + "_"
                            + Date
                            + "_" + newManager
                            + "_" + newWorkerNickname
                            + "_" + newWorkerUsername
                            + "_" + newExternalPartyNickname
                            + "_" + newExternalPartyUsername
                            + "_" + Type
                            + "_" + MW
                            + "_" + MP
                            + "_" + TW
                            + "_" + TP;
                    if (!newMap.containsKey(key)) {
                        newMap.put(key, new MoneyObject(0, 0));
                    }
                    newMap.get(key).moneyIn = newMap.get(key).moneyIn + Double.parseDouble(MoneyIn);
                    newMap.get(key).moneyOut = newMap.get(key).moneyOut + Double.parseDouble(MoneyOut);
                }
                for (String key : newMap.keySet()) {
                    String[] filtered = key.split("_");
                    String newMode = filtered[0].trim();
                    String Date = filtered[1].trim();
                    String newManager = filtered[2].trim();
                    String newWorkerNickname = filtered[3].trim();
                    String newWorkerUsername = filtered[4].trim();
                    String newExternalPartyNickname = filtered[5].trim();
                    String newExternalPartyUsername = filtered[6].trim();
                    String Type = filtered[7].trim();
                    String MW = filtered[8].trim();
                    String MP = filtered[9].trim();
                    String TW = filtered[10].trim();
                    String TP = filtered[11].trim();
                    if (Type.equals("M")) {
                        if (newExternalPartyUsername.equals("RNH")) {
                            if (!newMapRN.containsKey(key)) {
                                newMapRN.put(key, new ComputeSum.RNP(0, 0, 0, 0));
                            }
                            newMapRN.get(key).RNMoneyIn = newMapRN.get(key).RNMoneyIn + newMap.get(key).moneyIn;
                            newMapRN.get(key).RNMoneyOut = newMapRN.get(key).RNMoneyOut + newMap.get(key).moneyOut;
                        } else if (newExternalPartyUsername.equals("RN")) {
                            if (!newMapRN.containsKey(key)) {
                                newMapRN.put(key, new ComputeSum.RNP(0, 0, 0, 0));
                            }
                            newMapRN.get(key).RNPIn = newMapRN.get(key).RNPIn + newMap.get(key).moneyIn;
                            newMapRN.get(key).RNPOut = newMapRN.get(key).RNPOut + newMap.get(key).moneyOut;
                            newRows.add(newMode + "," + Date + "," + newManager + "," + newWorkerNickname + "," + newWorkerUsername + "," + newExternalPartyNickname + "," + newExternalPartyUsername + "," + newMap.get(key).moneyIn + "," + newMap.get(key).moneyOut + "," + Type + "," + MW + "," + MP + "," + TW + "," + TP + "\n");
                        } else {
                            newRows.add(newMode + "," + Date + "," + newManager + "," + newWorkerNickname + "," + newWorkerUsername + "," + newExternalPartyNickname + "," + newExternalPartyUsername + "," + newMap.get(key).moneyIn + "," + newMap.get(key).moneyOut + "," + Type + "," + MW + "," + MP + "," + TW + "," + TP + "\n");
                        }
                    }
                    if (Type.equals("T")) {
                        if ((newExternalPartyUsername.equals("RNH") && newMode.equals("C")) || (newExternalPartyUsername.equals("RNT") && newMode.equals("P"))) {
                            newRows.add(newMode + "," + Date + "," + newManager + "," + newWorkerNickname + "," + newWorkerUsername + "," + newExternalPartyNickname + "," + newExternalPartyUsername + "," + (newMap.get(key).moneyIn * 0.3) + "," + (newMap.get(key).moneyOut * 0.3) + "," + Type + "," + MW + "," + MP + "," + TW + "," + TP + "\n");
                        } else {
                            newRows.add(newMode + "," + Date + "," + newManager + "," + newWorkerNickname + "," + newWorkerUsername + "," + newExternalPartyNickname + "," + newExternalPartyUsername + "," + newMap.get(key).moneyIn + "," + newMap.get(key).moneyOut + "," + Type + "," + MW + "," + MP + "," + TW + "," + TP + "\n");
                        }
                    }
                }
                List<String> Commission = new ArrayList<>();
                String head = "Mode,Date,ManagerUsername,WorkerNickname,WorkerUsername,ExternalPartyNickname,ExternalPartyUsername,MoneyIn,MoneyOut,Type,MW,MP,TW,TP\n";
                List<String> nonCommission = new ArrayList<>();
                List<String> removedDuplication = new ArrayList<>();
                for (String key : newMapRN.keySet()) {
                    ComputeSum.RNP rnp = newMapRN.get(key);
                    String[] filtered = key.split("_");
                    String newMode = filtered[0].trim();
                    String Date = filtered[1].trim();
                    String newManager = filtered[2].trim();
                    String newWorkerNickname = filtered[3].trim();
                    String newWorkerUsername = filtered[4].trim();
                    String newExternalPartyNickname = filtered[5].trim();
                    String newExternalPartyUsername = filtered[6].trim();
                    String Type = filtered[7].trim();
                    String MW = filtered[8].trim();
                    String MP = filtered[9].trim();
                    String TW = filtered[10].trim();
                    String TP = filtered[11].trim();
                    double moneyIn = (rnp.RNMoneyIn - rnp.RNPIn) / 2.0;
                    double moneyOut = (rnp.RNMoneyOut - rnp.RNPOut) / 2.0;
                    newRows.add(newMode + "," + Date + "," + newManager + "," + newWorkerNickname + "," + newWorkerUsername + "," + newExternalPartyNickname + "," + newExternalPartyUsername + "," + moneyIn + "," + moneyOut + "," + Type + "," + MW + "," + MP + "," + TW + "," + TP + "\n");
                }
                Map<String, List<String>> test = new HashMap<>();
                for (String row : newRows) {
                    String[] filtered = row.split(",");
                    String newMode = filtered[0].trim();
                    String Date = filtered[1].trim();
                    String newManager = filtered[2].trim();
                    String newWorkerNickname = filtered[3].trim();
                    String newWorkerUsername = filtered[4].trim();
                    String newExternalPartyNickname = filtered[5].trim();
                    String newExternalPartyUsername = filtered[6].trim();
                    String MoneyIn = filtered[7].trim();
                    String MoneyOut = filtered[8].trim();
                    String Type = filtered[9].trim();
                    String MW = filtered[10].trim();
                    String MP = filtered[11].trim();
                    String TW = filtered[12].trim();
                    String TP = filtered[13].trim();
                    String key = Date + "_" + newExternalPartyNickname + "_" + newExternalPartyUsername;
                    if (!test.containsKey(key)) {
                        test.put(key, new ArrayList<>());
                    }
                    test.get(key).add(row);
//                    if (test.containsKey(key)) {
//                        System.out.println("-------");
//                        System.out.println(row);
//                        System.out.println(test.get(key));
//                        System.out.println("-------");
//                    } else {
//                        test.put(key, row);
//                    }
                }
                List<String> dups = new ArrayList<>();
                for (String key : test.keySet()) {
                    if (test.get(key).size() > 1) {
                        for (String row : test.get(key)) {
                            dups.add(row);
                        }
                    }
                }
                for (String row : newRows) {
                    String[] filtered = row.split(",");
                    String newWorkerNickname = filtered[3].trim();
                    if (newWorkerNickname.equals("COMMISSION")) {
                        Commission.add(row);
                    } else {
                        nonCommission.add(row);
                    }
                }
               // Collections.sort(dups);
                File file = new File("./Output/dups.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                for (String toWriteString : dups) {
                    bw.write(toWriteString);
                }
                bw.close();

                Collections.sort(nonCommission);

                file = new File("./Output/PLInfoSanitized.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fw = new FileWriter(file.getAbsoluteFile());
                bw = new BufferedWriter(fw);
                bw.write(head);
                for (String toWriteString : nonCommission) {
                    bw.write(toWriteString);
                }
                bw.close();

                Collections.sort(Commission);
                file = new File("./Output/PLInfoSanitized-Commission.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fw = new FileWriter(file.getAbsoluteFile());
                bw = new BufferedWriter(fw);
//                    bw.write("Customer, Date, Manager Nickname, Worker Nickname, Number, Customer Nickname, Customer ID, Money In, Money Out, Type\n");
                bw.write(head);
                for (String toWriteString : Commission) {
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
}
