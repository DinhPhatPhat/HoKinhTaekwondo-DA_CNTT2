package com.hokinhtaekwondo.hokinh_taekwondo.utils.exception.export;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.imports.UserImportResult;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.imports.UserImportRowResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserImportErrorFileGenerator {

    public static byte[] generate(UserImportResult result) {

        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("Người Dùng");

            // Header
            Row header = sheet.createRow(0);
            String[] headers = {
                    "Mã võ sinh",
                    "Họ và tên",
                    "Ngày sinh",
                    "Cấp đai",
                    "Địa chỉ",
                    "SĐT",
                    "__Lỗi__"
            };


            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            // Style
            CellStyle errorStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setColor(IndexedColors.RED.getIndex());
            font.setBold(true);
            errorStyle.setFont(font);

            int r = 1;
            for (UserImportRowResult row : result.getFailedRows()) {

                Row excelRow = sheet.createRow(r++);

                excelRow.createCell(0).setCellValue(row.getUserId());
                excelRow.createCell(1).setCellValue(row.getFullName());
                excelRow.createCell(2).setCellValue(row.getDateOfBirth());
                excelRow.createCell(3).setCellValue(row.getBeltLevel());
                excelRow.createCell(4).setCellValue(row.getAddress());
                excelRow.createCell(5).setCellValue(row.getPhoneNumber());

                Cell errorCell = excelRow.createCell(6);
                errorCell.setCellValue(row.getError());
                errorCell.setCellStyle(errorStyle);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Tạo file các người dùng bị lỗi khi thêm không được do gặp lỗi: ", e);
        }
    }
}

