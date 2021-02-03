package com.poxo.inventorymanager;
import android.content.ContentValues;
import android.util.Log;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

public class ExcelHelper {


    public static final String Inventory = "Inventory";
    public static final String id = "_id";// 0 integer
    public static final String ProductName = "ProductName";// 1 text(String)
    public static final String ProductNo = "ProductNo";// 2 text(String)
    public static final String Found = "Found";// 3 text(String)
    public static final String AssetNo = "AssetNo";// 4 text(String)
    public static final String MajorCat = "MajorCat";// 5 text(String)
    public static final String MinorCat = "MinorCat";// 6 text(String)
    public static final String AssetName = "AssetName";// 7 text(String)
    public static final String TagState = "TagState";// 8 text(String)
    public static final String Type = "Type";// 8 text(String)
    public static final String Remarks = "Remarks";// 8 text(String)
    public static final String AssetCap = "AssetCap";// 8 text(String)
    public static final String Quant = "Quant";// 8 text(String)
    public static final String OriCost = "OriCost";// 8 text(String)
    public static final String CurCost = "CurCost";// 8 text(String)
    public static final String NetBlock = "NetBlock";// 8 text(String)


    public static void insertExcelToSqlite(XlsxCon dbAdapter, Sheet sheet) {

        for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext(); ) {
            Row row = rit.next();

            ContentValues contentValues = new ContentValues();
            row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
            row.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);




            contentValues.put(ProductName, row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(ProductNo, row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(AssetNo, row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(MajorCat, row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(MinorCat, row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(AssetName, row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(TagState, row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(Type, row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(Remarks, row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(AssetCap, row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(Quant, row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(OriCost, row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(CurCost, row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(NetBlock, row.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
//            contentValues.put(Found, row.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            contentValues.put(Found,"0");

            try {
                if (dbAdapter.insert("Inventory", contentValues) < 0) {
                    return;
                }
            } catch (Exception ex) {
                Log.d("Exception in importing", ex.getMessage());
            }
        }
    }
}
