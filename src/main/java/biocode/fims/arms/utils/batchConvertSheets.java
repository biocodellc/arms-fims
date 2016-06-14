package biocode.fims.arms.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jdeck on 6/7/16.
 */
public class batchConvertSheets {

    public static void main(String[] args) throws InvalidFormatException, IOException {

        String directoryPath = "/Users/jdeck/IdeaProjects/arms-fims/sampledata/";
        File inDirectory = new File(directoryPath + "in");
        File[] directoryListing = inDirectory.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (FilenameUtils.getExtension(child.getAbsolutePath()).equalsIgnoreCase("xlsx") &&
                       !child.getName().startsWith("~") ) {
                    System.out.println(directoryPath + "in/" + child.getName());
                    try {
                        convertWorkbook(
                                new File(directoryPath + "in/" + child.getName()),
                                new File(directoryPath + "out/" + child.getName()));
                    }   catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle the case where dir is not really a directory.
                    // Checking dir.isDirectory() above would not be sufficient
                    // to avoid race conditions with another process that deletes
                    // directories.
                }
            }
        }
    }

    /**
     * Convert workbook data to desired format, to make them easier to load
     *
     * @param in
     * @param out
     *
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static void convertWorkbook(File in, File out) throws IOException, InvalidFormatException {
        System.out.println("Converting in/" + in.getName() + " to out/" + out.getName());
        XSSFWorkbook wb = new XSSFWorkbook(OPCPackage.open(new File(String.valueOf(in))));

        // Set the sheet name to samples
        wb.setSheetName(0, "Samples");

        // Adjust column names
        Sheet sheet = wb.getSheetAt(0);
        Row columns = sheet.getRow(0);
        ArrayList<Integer> columnsToDelete = new ArrayList<Integer>();
        System.out.println("\tRenaming columns");
        for (Cell cell : columns) {
            String columnName = cell.getStringCellValue();


            // Columns to change their name
            if (approxEquals(columnName, "SITE ID")) cell.setCellValue("stationID");
            if (approxEquals(columnName, "Unit Label")) cell.setCellValue("replicateLabel");
            if (approxEquals(columnName, "DEPLOYMENT ZONE")) cell.setCellValue("habitat");
            if (approxEquals(columnName, "SITE DETAILS")) cell.setCellValue("siteDetails");
            if (approxEquals(columnName, "MATALA MESH")) cell.setCellValue("hasScrubbieLayer");
            if (approxEquals(columnName, "ARMS IDs")) cell.setCellValue("deploymentID");
            if (approxEquals(columnName, "COUNTRY")) cell.setCellValue("country");
            if (approxEquals(columnName, "REGION")) cell.setCellValue("region");
            if (approxEquals(columnName, "LOCALITY NAME")) cell.setCellValue("locality");
            if (approxEquals(columnName, "DEPLOYMENT DATE")) cell.setCellValue("actualDeploymentDate");
            if (approxEquals(columnName, "RECOVERY DATE")) cell.setCellValue("actualRecoveryDate");
            if (approxEquals(columnName, "INTENDED RECOVERY DATE")) cell.setCellValue("intendedRecoveryDate");
            if (approxEquals(columnName, "SOAK TIME Months")) cell.setCellValue("intendedSoakTimeInMonths");
            if (approxEquals(columnName, "NUMBER OF replicate ARMS")) cell.setCellValue("numReplicatesInSet");
            if (approxEquals(columnName, "LATITUDE")) cell.setCellValue("decimalLatitude");
            if (approxEquals(columnName, "LONGITUDE")) cell.setCellValue("decimalLongitude");
            if (approxEquals(columnName, "Depth (m)")) cell.setCellValue("depthInMeters");
            if (approxEquals(columnName, "SUBSTRATE TYPE")) cell.setCellValue("substrateType");
            if (approxEquals(columnName, "NOTES")) cell.setCellValue("notes");
            if (approxEquals(columnName, "LAMINATES")) cell.setCellValue("laminates");
            if (approxEquals(columnName, "Layers number")) cell.setCellValue("numLayers");
            if (approxEquals(columnName, "Plate Photos")) cell.setCellValue("intentToPhotographPlates");
            if (approxEquals(columnName, "Specimen Photos")) cell.setCellValue("intentToPhotographSpecimens");
            if (approxEquals(columnName, "Barcoding data")) cell.setCellValue("intentToBarcode");
            if (approxEquals(columnName, "Metabarcoding data")) cell.setCellValue("intentToMetabarcode");
            if (approxEquals(columnName, "Other data types")) cell.setCellValue("intentToCollectOtherDatatypes");

            // Assemble list columns to delete -- needs to be done at end
           /* if (approxEquals(columnName, "Year")) columnsToDelete.add(cell.getColumnIndex());
            if (approxEquals(columnName, "Depth (ft)")) columnsToDelete.add(cell.getColumnIndex());
            if (approxEquals(columnName, "LEAD ORGANISATION")) columnsToDelete.add(cell.getColumnIndex());
            if (approxEquals(columnName, "PI EMAIL")) columnsToDelete.add(cell.getColumnIndex());
            if (approxEquals(columnName, "Secondary contact person")) columnsToDelete.add(cell.getColumnIndex());
            if (approxEquals(columnName, "PROJECT ID")) columnsToDelete.add(cell.getColumnIndex());
            if (approxEquals(columnName, "LEAD PI")) columnsToDelete.add(cell.getColumnIndex());
            */
        }

        // Delete columns at end
        // better to just ignore the columns for now.
        /*
        Iterator it = columnsToDelete.iterator();
        while (it.hasNext()) {
            Integer columnInt = (Integer) it.next();
            System.out.println("\temptying " + columns.getCell(columnInt).getStringCellValue() + " column index = " + columnInt);
            emptyColumn(sheet, (Integer) columnInt);
        }
        */
        System.out.println("\tWriting file " + out.getAbsolutePath());
        FileOutputStream fileOut = new FileOutputStream(out);

        wb.write(fileOut);
        System.out.println("\tClosing file");
        fileOut.close();

    }

    public static boolean approxEquals(String columnName, String value) {
        String cleanedColumnName = columnName.trim().replaceAll(" +", " ");
        if (cleanedColumnName.equalsIgnoreCase(value.trim())) return true;
        else return false;
    }

    /**
     * Given a sheet, this method deletes a column from a sheet and moves
     * all the columns to the right of it to the left one cell.
     * <p/>
     * Note, this method will not update any formula references.
     *
     * @param sheet
     * @param column
     */
    public static void emptyColumn(Sheet sheet, int columnToDelete) {
        for (int r = 0; r < sheet.getLastRowNum() + 1; r++) {
            Row row = sheet.getRow(r);
            row.getCell(columnToDelete).setCellValue("");

        }


    }


}
