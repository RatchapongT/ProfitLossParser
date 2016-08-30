/**
 * Jing Wen gave me head start. Simply love her. on 8/15/16.
 */

import java.io.*;
import java.util.*;

import org.apache.poi.ddf.EscherColorRef;
import org.apache.poi.ss.usermodel.*;

public class ProfitLossParser {
    boolean debugMode = false;
    boolean verboseMode = false;

    public static boolean isNumeric(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isNumber(Sheet currentSheet, int row, int offset) {
        try {
            double number = currentSheet.getRow(row).getCell(offset).getNumericCellValue();
            return isNumeric(Double.toString(number));
        } catch (Exception ioe) {
            return false;
        }
    }

    public static String padding(String date) {
        return "00".substring(date.length()) + date;
    }

    public static Cell getCell(Sheet currentSheet, int row, int offset, String mode, String type, File file) {
        if (currentSheet.getRow(row).getCell(offset) == null) {
            //System.out.println("[" + mode + "](" + type + ") " + file.getName() + " Null@ col " + offset + " on row " + row);
        }
        return currentSheet.getRow(row).getCell(offset);
    }

    public List<String> processFiles(String managerNickname, String path, String mode, String type, int offset) {
        List<String> rows = new ArrayList<>();
        File folder = new File(path);
        int row = 0;
        try {
            System.out.println("------ [" + mode + "](" + type + ") " + "Opening Folder" + path + " with " + (folder.listFiles() == null ? 0 : folder.listFiles().length) + " files...------");
            if (folder.listFiles() != null) {
                for (File file : folder.listFiles()) {

                    Workbook workbook = WorkbookFactory.create(file);
                    Sheet currentSheet = workbook.getSheetAt(0);
                    if (currentSheet.getLastRowNum() < 10) {
                        currentSheet = workbook.getSheetAt(1);
                    }
                    String[] dateArray = file.getName().split("-");
                    String date = padding(dateArray[0]) + "-" + padding(dateArray[1]) + "-" + Integer.toString(Integer.parseInt(dateArray[2].substring(0, 2)) + 1957);
                    row = 0;
                    String workerNickname = "";
                    if (verboseMode) {
                        System.out.print("[" + mode + "](" + type + ") " + "Begin Parsing " + file.getName() + " with " + currentSheet.getLastRowNum() + " rows...");
                    }

                    int pre = 0;
                    while (row < currentSheet.getLastRowNum()) {
                        while (row < currentSheet.getLastRowNum() && (currentSheet.getRow(row) == null || currentSheet.getRow(row).getCell(offset) == null || currentSheet.getRow(row).getCell(offset).toString().equals("") || !isNumber(currentSheet, row, offset))) {
                            row++;
                        }
                        while (row < currentSheet.getLastRowNum()) {

                            if (currentSheet.getRow(row) == null) {
                                if (debugMode) {
                                    System.out.println("[" + mode + "](" + type + ") " + file.getName() + " Null@ row " + row);
                                }
                            } else if (getCell(currentSheet, row, offset, mode, type, file) == null) {
                                if (debugMode) {
                                    System.out.println("[" + mode + "](" + type + ") " + file.getName() + " Null@ col " + offset + " on row " + row);
                                }
                            } else if (isNumber(currentSheet, row, offset)) {
                                int number = (int) currentSheet.getRow(row).getCell(offset).getNumericCellValue();
                                if (number == 0) {
                                    row++;
                                    continue;
                                }
                                if (number == 1) {
                                    pre = 1;
                                    workerNickname = "";
                                    if (getCell(currentSheet, row - 1, offset, mode, type, file) == null || getCell(currentSheet, row - 1, offset, mode, type, file).toString().equals("")) {
                                        workerNickname = getCell(currentSheet, row - 1, offset - 1, mode, type, file).toString();
                                    } else {
                                        workerNickname = getCell(currentSheet, row - 1, offset, mode, type, file).toString();
                                        if (workerNickname.indexOf("No") > -1 || workerNickname.indexOf("NO") > -1) {
                                            workerNickname = getCell(currentSheet, row - 2, offset, mode, type, file).toString();
                                        }
                                    }
                                    if (workerNickname.equals("")) {
                                        workerNickname = getCell(currentSheet, row - 1, offset + 4, mode, type, file).toString();
                                    }
                                    if (workerNickname.equals("")) {
                                        System.out.println("[" + mode + "](" + type + ") " + file.getName() + " WTF" + "@ col " + offset + " on row " + row + " Number: " + number + ", Pre: " + pre);
                                    }

                                }
                                //if (workerNickname.equals("ซื้อ")) break;
                                if (number - pre != 1 && number != 1) {
                                    //System.out.println("[" + mode + "](" + type + ") " + file.getName() + " Skipped@ col " + offset + " on row " + row + " Number: " + number + ", Pre: " + pre);
                                }
                                pre = number;
                                boolean partnerMode = workerNickname.indexOf("โป๊ว") > -1;

                                if (getCell(currentSheet, row, offset + 1, mode, type, file) != null) {

                                    String externalPartyNickname = getCell(currentSheet, row, offset + 1, mode, type, file).toString();
                                    Cell cell = currentSheet.getRow(row).getCell(offset + 2);
                                    String externalPartyUsername = (cell == null || cell.toString().equals("")) ? externalPartyNickname : cell.toString();

                                    try {
                                        Double mIn = null;
                                        Double mOut = null;
                                        double moneyIn = 0.0;
                                        double moneyOut = 0.0;
                                        try {
                                            mIn = Double.parseDouble(getCell(currentSheet, row, offset + 4, mode, type, file).toString().trim());
                                        } catch (NumberFormatException e) {
                                            mIn = null;
                                        }
                                        try {
                                            mOut = Double.parseDouble(getCell(currentSheet, row, offset + 5, mode, type, file).toString().trim());
                                        } catch (NumberFormatException e) {
                                            mOut = null;
                                        }
                                        if (mIn == null) {
                                            moneyIn = (getCell(currentSheet, row, offset + 4, mode, type, file) == null || getCell(currentSheet, row, offset + 4, mode, type, file).toString().trim().equals("")) ? 0 : getCell(currentSheet, row, offset + 4, mode, type, file).getNumericCellValue();
                                        } else {
                                            moneyIn = mIn;
                                        }
                                        if (mOut == null) {
                                            moneyOut = (getCell(currentSheet, row, offset + 5, mode, type, file) == null || getCell(currentSheet, row, offset + 5, mode, type, file).toString().trim().equals("")) ? 0 : getCell(currentSheet, row, offset + 5, mode, type, file).getNumericCellValue();
                                        } else {
                                            moneyOut = mOut;
                                        }
                                        if (mode.equals("Customer") && !partnerMode) {

                                            if (!(externalPartyNickname.equals("") && externalPartyUsername.equals(""))) {
                                                if (externalPartyNickname.equals("TEST")) {
                                                    System.out.println("C, " + date + ", " + managerNickname + ", " + workerNickname + ", " + number + ", " + externalPartyNickname + ", " + externalPartyUsername + ", " + moneyIn + ", " + moneyOut + "\n");
                                                }

                                                rows.add("C, " + date + ", " + managerNickname + ", " + workerNickname + ", " + number + ", " + externalPartyNickname + ", " + externalPartyUsername + ", " + moneyIn + ", " + moneyOut + ", " + type + "\n");
                                            }
                                        }
                                        if (mode.equals("Partner") && partnerMode) {
                                            if (!(externalPartyNickname.equals("") && externalPartyUsername.equals(""))) {
                                                if (externalPartyNickname.equals("ปีนัง")) {
                                                    rows.add("P, " + date + ", " + managerNickname + ", " + workerNickname + ", " + number + ", " + externalPartyNickname + ", " + externalPartyUsername + ", " + (moneyIn / 8.4) + ", " + (moneyOut / 8.4) + ", " + type + "\n");
                                                } else if (!externalPartyNickname.equals("ปีนังเงินไทย")) {
                                                    rows.add("P, " + date + ", " + managerNickname + ", " + workerNickname + ", " + number + ", " + externalPartyNickname + ", " + externalPartyUsername + ", " + moneyIn + ", " + moneyOut + ", " + type + "\n");
                                                }


                                            }
                                        }
                                    } catch (Exception ioe) {
                                        System.out.println("EXCEPTION!!!! [" + mode + "](" + type + ") " + file.getName() + " EXCEPTION@ col " + offset + " on row " + row);
                                    }
                                }
                            }
                            row++;
                        }
                    }
                    if (verboseMode) {
                        System.out.println("Done: " + file.getName());
                    }
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();

        }
        return rows;
    }

    public void writeFile(List<String> rows, String filename, String mode, String header) {
        try {
            File file = new File("./Output/" + filename + "-" + mode + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(header);
            for (String toWriteString : rows) {
                bw.write(toWriteString);
            }
            bw.close();
            System.out.println("Finished writing file: " + filename + "-" + mode + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String cHeader = "Customer, Date, Manager Nickname, Worker Nickname, Number, Customer Nickname, Customer ID, Money In, Money Out, Type\n";
        String pHeader = "Partner, Date, Manager Nickname, Worker Nickname, Number, Partner Nickname, Partner ID, Money Out, Money In, Type\n";
        ProfitLossParser profitLossParser = new ProfitLossParser();
//        List<String> first_customerTest = profitLossParser.processFiles("MalayP2", "./Input/test", "Customer", "M", 1);
//        List<String> first_partnerTest = profitLossParser.processFiles("MalayP2", "./Input/test", "Partner", "M", 1);
//        List<String> second_customerTest = profitLossParser.processFiles("MalayP2", "./Input/test", "Customer", "M", 8);
//        List<String> second_partnerTest = profitLossParser.processFiles("MalayP2", "./Input/test", "Partner", "M", 8);
//        List<String> customerTest = new ArrayList<>();
//        List<String> partnerTest = new ArrayList<>();
//        customerTest.addAll(first_customerTest);
//        customerTest.addAll(second_customerTest);
//        partnerTest.addAll(first_partnerTest);
//        partnerTest.addAll(second_partnerTest);
//        profitLossParser.writeFile(partnerTest, "Malay", "Partner-TEST", pHeader);
//        profitLossParser.writeFile(customerTest, "Malay", "Customer-TEST", cHeader);

        List<String> first_mPartnerProcessFilesP1 = profitLossParser.processFiles("MalayP1", "./Input/MalayP1", "Partner", "M", 1);
        List<String> first_mCustomerProcessFilesP1 = profitLossParser.processFiles("MalayP1", "./Input/MalayP1", "Customer", "M", 1);
        List<String> first_mPartnerProcessFilesP2 = profitLossParser.processFiles("MalayP2", "./Input/MalayP2", "Partner", "M", 1);
        List<String> first_mCustomerProcessFilesP2 = profitLossParser.processFiles("MalayP2", "./Input/MalayP2", "Customer", "M", 1);
        List<String> first_tPartnerProcessFiles = profitLossParser.processFiles("Thai", "./Input/Thai", "Partner", "T", 0);
        List<String> first_tCustomerProcessFiles = profitLossParser.processFiles("Thai", "./Input/Thai", "Customer", "T", 0);

        List<String> second_mPartnerProcessFilesP1 = profitLossParser.processFiles("MalayP1", "./Input/MalayP1", "Partner", "M", 8);
        List<String> second_mCustomerProcessFilesP1 = profitLossParser.processFiles("MalayP1", "./Input/MalayP1", "Customer", "M", 8);
        List<String> second_mPartnerProcessFilesP2 = profitLossParser.processFiles("MalayP2", "./Input/MalayP2", "Partner", "M", 8);
        List<String> second_mCustomerProcessFilesP2 = profitLossParser.processFiles("MalayP2", "./Input/MalayP2", "Customer", "M", 8);
        List<String> second_tPartnerProcessFiles = profitLossParser.processFiles("Thai", "./Input/Thai", "Partner", "T", 7);
        List<String> second_tCustomerProcessFiles = profitLossParser.processFiles("Thai", "./Input/Thai", "Customer", "T", 7);

        List<String> malayCustomer = new ArrayList<>();
        List<String> malayPartner = new ArrayList<>();
        List<String> thaiCustomer = new ArrayList<>();
        List<String> thaiPartner = new ArrayList<>();
        malayPartner.addAll(first_mPartnerProcessFilesP1);
        malayPartner.addAll(first_mPartnerProcessFilesP2);
        malayPartner.addAll(second_mPartnerProcessFilesP1);
        malayPartner.addAll(second_mPartnerProcessFilesP2);

        malayCustomer.addAll(first_mCustomerProcessFilesP1);
        malayCustomer.addAll(first_mCustomerProcessFilesP2);
        malayCustomer.addAll(second_mCustomerProcessFilesP1);
        malayCustomer.addAll(second_mCustomerProcessFilesP2);

        thaiPartner.addAll(first_tPartnerProcessFiles);
        thaiPartner.addAll(second_tPartnerProcessFiles);
        thaiCustomer.addAll(first_tCustomerProcessFiles);
        thaiCustomer.addAll(second_tCustomerProcessFiles);

        profitLossParser.writeFile(malayPartner, "Malay", "Partner", pHeader);
        profitLossParser.writeFile(malayCustomer, "Malay", "Customer", cHeader);
        profitLossParser.writeFile(thaiPartner, "Thai", "Partner", pHeader);
        profitLossParser.writeFile(thaiCustomer, "Thai", "Customer", cHeader);
    }
}
