/**
 * Jing Wen gave me head start. Simply love her. on 8/15/16.
 */

import java.io.*;
import java.util.*;

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

    public static String padding(String date) {
        return "00".substring(date.length()) + date;
    }

    public static Cell getCell(Sheet currentSheet, int row, int offset, String mode, String type, File file) {
        if (currentSheet.getRow(row).getCell(offset) == null) {
            System.out.println("[" + mode + "](" + type + ") " + file.getName() + " Null@ col " + offset + " on row " + row);
        }
        return currentSheet.getRow(row).getCell(offset);
    }

    public List<String> processFiles(String path, String mode, String type, int offset) {
        List<String> rows = new ArrayList<>();
        File folder = new File(path);
        try {
            System.out.println("------ [" + mode + "](" + type + ") " + "Opening Folder" + path + " with " + folder.listFiles().length + " files...------");

            for (File file : folder.listFiles()) {
                Workbook workbook = WorkbookFactory.create(file);

                Sheet currentSheet = workbook.getSheetAt(0);
                String[] dateArray = file.getName().split("-");
                String date = padding(dateArray[0]) + "-" + padding(dateArray[1]) + "-" + Integer.toString(Integer.parseInt(dateArray[2].substring(0, 2)) + 1958);

                int row = 0;
                String managerNickname = "Dummy";
                String workerNickname = "";
                if (verboseMode) {
                    System.out.print("[" + mode + "](" + type + ") " + "Begin Parsing " + file.getName() + " with " + currentSheet.getLastRowNum() + " rows...");
                }
                int pre = 0;
                while (row < currentSheet.getLastRowNum()) {
                    while (row < currentSheet.getLastRowNum() && (currentSheet.getRow(row) == null || currentSheet.getRow(row).getCell(offset) == null || !isNumeric(currentSheet.getRow(row).getCell(offset).toString()))) {
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
                        } else if (isNumeric(getCell(currentSheet, row, offset, mode, type, file).toString())) {
                            int number = (int) currentSheet.getRow(row).getCell(offset).getNumericCellValue();
                            if (number == 1) {
                                pre = 1;
                                workerNickname = getCell(currentSheet, row - 1, offset, mode, type, file).toString();
                                if (workerNickname.indexOf("No") > -1) {
                                    workerNickname = getCell(currentSheet, row - 2, offset, mode, type, file).toString();
                                }
                            }
                            if (number - pre != 1 && number != 1) {
                                System.out.println("[" + mode + "](" + type + ") " + file.getName() + " Skipped@ col " + offset + " on row " + row);
                            }
                            pre = number;
                            boolean partnerMode = workerNickname.indexOf("รายการ") > -1;
                            if (type.equals("T")) {
                                String externalPartyNickname = getCell(currentSheet, row, offset + 1, mode, type, file).toString();
                                Cell cell = currentSheet.getRow(row).getCell(offset + 2);
                                String externalPartyUsername = (cell == null || cell.toString().equals("")) ? externalPartyNickname : cell.toString();
                                double moneyIn = getCell(currentSheet, row, offset + 4, mode, type, file).getNumericCellValue();
                                double moneyOut = getCell(currentSheet, row, offset + 5, mode, type, file).getNumericCellValue();

                                if (mode.equals("Customer") && !partnerMode) {
                                    if (!(externalPartyNickname.equals("") && externalPartyUsername.equals(""))) {
                                        rows.add(date + ", " + managerNickname + ", " + workerNickname + ", " + number + ", " + externalPartyNickname + ", " + externalPartyUsername + ", " + moneyIn + ", " + moneyOut + "\n");
                                    }
                                }
                                if (mode.equals("Partner") && partnerMode) {
                                    if (!(externalPartyNickname.equals("") && externalPartyUsername.equals(""))) {
                                        rows.add(date + ", " + managerNickname + ", " + workerNickname + ", " + number + ", " + externalPartyNickname + ", " + externalPartyUsername + ", " + moneyIn + ", " + moneyOut + "\n");
                                    }
                                }
                            }
                            if (type.equals("M")) {
                                String externalPartyNickname = getCell(currentSheet, row, offset + 1, mode, type, file).toString();
                                String externalPartyUsername = externalPartyNickname;
                                double moneyIn = getCell(currentSheet, row, offset + 3, mode, type, file).getNumericCellValue();
                                double moneyOut = getCell(currentSheet, row, offset + 4, mode, type, file).getNumericCellValue();
                                if (mode.equals("Customer") && !partnerMode) {
                                    if (!(externalPartyNickname.equals("") && externalPartyUsername.equals(""))) {
                                        rows.add(date + ", " + managerNickname + ", " + workerNickname + ", " + number + ", " + externalPartyNickname + ", " + externalPartyUsername + ", " + moneyIn + ", " + moneyOut + "\n");
                                    }
                                }
                                if (mode.equals("Partner") && partnerMode) {
                                    if (!(externalPartyNickname.equals("") && externalPartyUsername.equals(""))) {
                                        rows.add(date + ", " + managerNickname + ", " + workerNickname + ", " + number + ", " + externalPartyNickname + ", " + externalPartyUsername + ", " + moneyIn + ", " + moneyOut + "\n");
                                    }
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
        ProfitLossParser profitLossParser = new ProfitLossParser();
        String cHeader = "Date, Manager Nickname, Worker Nickname, Number, Customer Nickname, Customer ID, Money In, Money Out\n";
        String pHeader = "Date, Manager Nickname, Worker Nickname, Number, Partner Nickname, Partner ID, Money In, Money Out\n";
        List<String> first_mPartnerProcessFiles = profitLossParser.processFiles("./Input/Malay", "Partner", "M", 0);
        List<String> first_mCustomerProcessFiles = profitLossParser.processFiles("./Input/Malay", "Customer", "M", 0);
        List<String> first_tPartnerProcessFiles = profitLossParser.processFiles("./Input/Thai", "Partner", "T", 0);
        List<String> first_tCustomerProcessFiles = profitLossParser.processFiles("./Input/Thai", "Customer", "T", 0);

        List<String> second_mPartnerProcessFiles = profitLossParser.processFiles("./Input/Malay", "Partner", "M", 6);
        List<String> second_mCustomerProcessFiles = profitLossParser.processFiles("./Input/Malay", "Customer", "M", 6);
        List<String> second_tPartnerProcessFiles = profitLossParser.processFiles("./Input/Thai", "Partner", "T", 7);
        List<String> second_tCustomerProcessFiles = profitLossParser.processFiles("./Input/Thai", "Customer", "T", 7);

        first_mPartnerProcessFiles.addAll(second_mPartnerProcessFiles);
        first_mCustomerProcessFiles.addAll(second_mCustomerProcessFiles);
        first_tPartnerProcessFiles.addAll(second_tPartnerProcessFiles);
        first_tCustomerProcessFiles.addAll(second_tCustomerProcessFiles);

        profitLossParser.writeFile(first_mPartnerProcessFiles, "Malay", "Partner", cHeader);
        profitLossParser.writeFile(first_mCustomerProcessFiles, "Malay", "Customer", pHeader);
        profitLossParser.writeFile(first_tPartnerProcessFiles, "Thai", "Partner", cHeader);
        profitLossParser.writeFile(first_tCustomerProcessFiles, "Thai", "Customer", pHeader);
    }
}
