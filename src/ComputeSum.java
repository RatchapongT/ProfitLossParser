import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Zanko on 8/16/2016.
 */
public class ComputeSum {
    public static class RNObject {
        public double moneyInRN;
        public double moneyOutRN;
        public double moneyInDang;
        public double moneyOutDang;
        public double moneyInPle;
        public double moneyOutPle;

        public RNObject(double moneyInRN,
                        double moneyOutRN,
                        double moneyInDang,
                        double moneyOutDang,
                        double moneyInPle,
                        double moneyOutPle) {
            this.moneyInRN = moneyInRN;
            this.moneyOutRN = moneyOutRN;
            this.moneyInDang = moneyInDang;
            this.moneyOutDang = moneyOutDang;
            this.moneyInPle = moneyInPle;
            this.moneyOutPle = moneyOutPle;
        }
    }

    public static class RNP {
        public double RNPIn;
        public double RNPOut;
        public double RNMoneyIn;
        public double RNMoneyOut;

        public RNP(double RNPIn,
                   double RNPOut,
                   double RNMoneyIn,
                   double RNMoneyOut
        ) {
            this.RNPIn = RNPIn;
            this.RNPOut = RNPOut;
            this.RNMoneyIn = RNMoneyIn;
            this.RNMoneyOut = RNMoneyOut;
        }
    }
    Set<String> globalCustomerSet = new HashSet<>();
    Set<String> globalPartnerSet = new HashSet<>();
    Set<String> globalWorkerSet = new HashSet<>();
    Set<String> globalManagerSet = new HashSet<>();
    Map<String, Set<String>> globalExternalPartyOwner = new HashMap<>();
    public static void main(String[] args) {
        check("Thai");
        check("Malay");
    }

    static String queryDate = "2016-05-31";
    static boolean queryMode = false;
    static String includeMode = "Whang ";


    public static void check(String mode) {
        Set<String> customerSet = new HashSet<>();
        Set<String> partnerSet = new HashSet<>();
        Set<String> workerSet = new HashSet<>();
        Set<String> managerSet = new HashSet<>();
        Map<String, Set<String>> externalPartyOwner = new HashMap<>();
        Map<String, Double> map = new HashMap<>();
        Map<String, Double> map1 = new HashMap<>();
        Map<String, Double> map2 = new HashMap<>();
        Map<String, RNObject> RNMap = new HashMap<>();
        Map<String, RNP> RNPMap1 = new HashMap<>();
        Map<String, RNP> RNPMap2 = new HashMap<>();
        Map<String, Double> rn1 = new HashMap<>();
        Map<String, Double> rn2 = new HashMap<>();
        String customerInformation = mode.equals("Thai") ? "./Output/Thai-Customer.txt" : "./Output/Malay-Customer.txt";
        String partnerInformation = mode.equals("Thai") ? "./Output/Thai-Partner.txt" : "./Output/Malay-Partner.txt";
        try {

            String sCurrentLine;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(partnerInformation));
            bufferedReader.readLine();
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                String[] arr = sCurrentLine.split(",");
                String[] dates = arr[0].trim().split("-");
                String date = dates[2] + "-" + dates[1] + "-" + dates[0];
                String manager = arr[1].trim();
                String worker = arr[2].trim();
                String number = arr[3].trim();
                String externalNickname = arr[4].trim();
                String externalUsername = arr[5].trim();
                String moneyOut = arr[6].trim();
                String moneyIn = arr[7].trim();
                if (!map.containsKey(date)) {
                    map.put(date, 0.0);
                }
                if (!map1.containsKey(date)) {
                    map1.put(date, 0.0);
                }
                if (!map2.containsKey(date)) {
                    map2.put(date, 0.0);
                }
                partnerSet.add(externalNickname + "\t" + externalUsername);
                managerSet.add(manager);
                workerSet.add(worker);
                if (!externalPartyOwner.containsKey(externalNickname + "\t" + externalUsername)) {
                    externalPartyOwner.put(externalNickname + "\t" + externalUsername, new HashSet<>());
                }
                externalPartyOwner.get(externalNickname + "\t" + externalUsername).add(worker);
                if (externalNickname.indexOf("Ringgit") > -1) {
                    map.put(date, map.get(date) + (Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut)) * 8.4);
                } else {
                    map.put(date, map.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                }
                if (manager.equals("MalayP1")) {
                    map1.put(date, map1.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                    if (queryMode && queryDate.equals(date) && includeMode.indexOf("Whang") > -1) {
                        System.out.println("1-Partner\t" + externalNickname + "\t" + moneyOut + "\t" + moneyIn);
                    }
                }
                if (manager.equals("MalayP2")) {
                    if (externalNickname.indexOf("Ringgit") > -1) {
                        map2.put(date, map2.get(date) + (Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut)) * 8.4);
                    } else {
                        map2.put(date, map2.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                    }
                    if (queryMode && queryDate.equals(date) && includeMode.indexOf("Muay") > -1) {
                        System.out.println("2-Partner\t" + externalNickname + "\t" + moneyOut + "\t" + moneyIn);
                    }

                }
            }
            bufferedReader = new BufferedReader(new FileReader(customerInformation));
            bufferedReader.readLine();
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                String[] arr = sCurrentLine.split(",");

                String[] dates = arr[0].trim().split("-");
                String date = dates[2] + "-" + dates[1] + "-" + dates[0];
                String manager = arr[1].trim();
                String worker = arr[2].trim();
                String number = arr[3].trim();
                String externalNickname = arr[4].trim();
                String externalUsername = arr[5].trim();
                String moneyIn = arr[6].trim();
                String moneyOut = arr[7].trim();
                customerSet.add(externalNickname + "\t" + externalUsername);
                managerSet.add(manager);
                workerSet.add(worker);
                if (!externalPartyOwner.containsKey(externalNickname + "\t" + externalUsername)) {
                    externalPartyOwner.put(externalNickname + "\t" + externalUsername, new HashSet<>());
                }
                externalPartyOwner.get(externalNickname + "\t" + externalUsername).add(worker);
                if (!map.containsKey(date)) {
                    map.put(date, 0.0);
                }
                if (!map1.containsKey(date)) {
                    map1.put(date, 0.0);
                }
                if (!map2.containsKey(date)) {
                    map2.put(date, 0.0);
                }
                if (!rn1.containsKey(date)) {
                    rn1.put(date, 0.0);
                }
                if (!rn2.containsKey(date)) {
                    rn2.put(date, 0.0);
                }
                if (mode.equals("Thai")) {
                    if (worker.equals("รายการ RN")) {
                        if (!RNMap.containsKey(date)) {
                            RNMap.put(date, new RNObject(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                        }
                        if (externalNickname.equals("RN")) {
                            RNMap.get(date).moneyInRN = Double.parseDouble(moneyIn);
                            RNMap.get(date).moneyOutRN = Double.parseDouble(moneyOut);


                        }
                        if (externalNickname.equals("RN โป๊ว แตง")) {
                            RNMap.get(date).moneyInDang = Double.parseDouble(moneyIn);
                            RNMap.get(date).moneyOutDang = Double.parseDouble(moneyOut);

                        }
                        if (externalNickname.equals("RN โป๊ว เปิ้ล")) {
                            RNMap.get(date).moneyInPle = Double.parseDouble(moneyIn);
                            RNMap.get(date).moneyOutPle = Double.parseDouble(moneyOut);

                        }
                        if (externalNickname.equals("เปิ้ล โป๊ว RN")) {
                            map.put(date, map.get(date) - Double.parseDouble(moneyIn) + Double.parseDouble(moneyOut));
                        }
                    } else {
                        map.put(date, map.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                    }
                } else {
                    if (manager.equals("MalayP1")) {
                        if (queryMode && queryDate.equals(date) && includeMode.indexOf("Whang") > -1) {
                            System.out.println("1-Customer\t" + externalNickname + "\t" + moneyIn + "\t" + moneyOut);
                        }
                        if (worker.equals("ซื้อ")) {
                            if (!RNPMap1.containsKey(date)) {
                                RNPMap1.put(date, new RNP(0.0, 0.0, 0.0, 0.0));
                            }
                            RNPMap1.get(date).RNMoneyIn = RNPMap1.get(date).RNMoneyIn + Double.parseDouble(moneyIn);
                            RNPMap1.get(date).RNMoneyOut = RNPMap1.get(date).RNMoneyOut + Double.parseDouble(moneyOut);
                        } else if (externalNickname.equals("RNP")) {
                            if (!RNPMap1.containsKey(date)) {
                                RNPMap1.put(date, new RNP(0.0, 0.0, 0.0, 0.0));
                            }
                            RNPMap1.get(date).RNPIn = RNPMap1.get(date).RNPIn + Double.parseDouble(moneyIn);
                            RNPMap1.get(date).RNPOut = RNPMap1.get(date).RNPOut + Double.parseDouble(moneyOut);
                            map1.put(date, map1.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                            map.put(date, map.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                        } else {
                            map1.put(date, map1.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                            map.put(date, map.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                        }
                    } else if (manager.equals("MalayP2")) {
                        if (queryMode && queryDate.equals(date) && includeMode.indexOf("Muay") > -1) {
                            System.out.println("2-Customer\t" + externalNickname + "\t" + moneyIn + "\t" + moneyOut);
                        }
                        if (worker.equals("ซื้อ")) {
                            if (!RNPMap2.containsKey(date)) {
                                RNPMap2.put(date, new RNP(0.0, 0.0, 0.0, 0.0));
                            }
                            RNPMap2.get(date).RNMoneyIn = RNPMap2.get(date).RNMoneyIn + Double.parseDouble(moneyIn);
                            RNPMap2.get(date).RNMoneyOut = RNPMap2.get(date).RNMoneyOut + Double.parseDouble(moneyOut);
                        } else if (externalNickname.equals("RNP")) {
                            if (!RNPMap2.containsKey(date)) {
                                RNPMap2.put(date, new RNP(0.0, 0.0, 0.0, 0.0));
                            }

                            RNPMap2.get(date).RNPIn = RNPMap2.get(date).RNPIn + Double.parseDouble(moneyIn);
                            RNPMap2.get(date).RNPOut = RNPMap2.get(date).RNPOut + Double.parseDouble(moneyOut);
                            map2.put(date, map2.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                            map.put(date, map.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                        } else {
                            if (externalNickname.indexOf("Ringgit") > -1) {
                                map2.put(date, map2.get(date) + (Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut)) * 8.4);
                                map.put(date, map.get(date) + (Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut)) * 8.4);
                            } else {
                                map2.put(date, map2.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                                map.put(date, map.get(date) + Double.parseDouble(moneyIn) - Double.parseDouble(moneyOut));
                            }
                        }
                    }
                }
            }

            if (mode.equals("Thai")) {
                for (String date : RNMap.keySet()) {
                    RNObject rn = RNMap.get(date);
                    double moneyIn = (rn.moneyInRN - rn.moneyInDang) * 0.3 + rn.moneyInPle;
                    double moneyOut = (rn.moneyOutRN - rn.moneyOutDang) * 0.3 + rn.moneyOutPle;
                    map.put(date, map.get(date) + moneyIn - moneyOut);
                }
            } else {
                for (String date : RNPMap1.keySet()) {
                    RNP rnp = RNPMap1.get(date);

                    double moneyIn = (rnp.RNMoneyIn - rnp.RNPIn) / 2.0;
                    double moneyOut = (rnp.RNMoneyOut - rnp.RNPOut) / 2.0;
                    rn1.put(date, rn1.get(date) + moneyIn - moneyOut);
                }
                for (String date : RNPMap2.keySet()) {
                    RNP rnp = RNPMap2.get(date);
                    double moneyIn = (rnp.RNMoneyIn - rnp.RNPIn) / 2.0;
                    double moneyOut = (rnp.RNMoneyOut - rnp.RNPOut) / 2.0;

                    rn2.put(date, rn2.get(date) + moneyIn - moneyOut);
                }
            }
//            if (!queryMode) {
//                System.out.println("---------------ALL------------------");
//            }
//            List<String> list = new ArrayList<>(map.keySet());
//            Collections.sort(list);
//            if (mode.equals("Malay")) {
//                if (!queryMode) {
//                    System.out.println("DATE" + "\t" + "WHANG" + "\t" + "MAUY" + "\t" + "ALL w/o RN" + "\t" + "WHANG RN" + "\t" + "MUAY RN" + "\t" + "ALL w/ RN");
//                }
//            }
//            for (String date : list) {
//                if (!queryMode || queryDate.equals(date)) {
//                    System.out.println(date + "\t" + map1.get(date) + "\t" + map2.get(date) + "\t" + map.get(date) + "\t" + rn1.get(date) + "\t" + rn2.get(date) + "\t" +
//                            (rn2.get(date) + rn1.get(date) + map.get(date)));
//                }
//            }
            List<String> partnerList = new ArrayList<>(partnerSet);
            List<String> customerList = new ArrayList<>(customerSet);
            List<String> managerList = new ArrayList<>(managerSet);
            List<String> workerList = new ArrayList<>(workerSet);
            Collections.sort(partnerList);
            Collections.sort(customerList);
            Collections.sort(managerList);
            Collections.sort(workerList);
            System.out.println("------------------------MANAGER------------------------");
            for (String s : managerList) {
                System.out.println(s);
            }
            System.out.println("------------------------WORKER------------------------");
            for (String s : workerList) {
                System.out.println(s);
            }
            System.out.println("------------------------CUSTOMER------------------------");

            for (String s : customerList) {
                for (String w : externalPartyOwner.get(s)) {
                    System.out.println(w + "\t" + s);
                }
            }
            System.out.println("------------------------PARTNER------------------------");
            for (String s : partnerList) {
                for (String w : externalPartyOwner.get(s)) {
                    System.out.println(w + "\t" + s);
                }
            }

            Set<String> globalCustomerSet = new HashSet<>();
            Set<String> globalPartnerSet = new HashSet<>();
            Set<String> globalWorkerSet = new HashSet<>();
            Set<String> globalManagerSet = new HashSet<>();
            Map<String, Set<String>> globalExternalPartyOwner = new HashMap<>();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
