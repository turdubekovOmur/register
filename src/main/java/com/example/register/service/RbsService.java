package com.example.register.service;

import com.example.register.interfaces.Readable;
import com.example.register.model.PaymentRBS;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Getter
@Slf4j
public class RbsService implements Readable {


    @Override
    public List read(MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);
        log.info("ПОПЫТКА ПАРСИНГА ФАЙЛА РБС...");

        List<PaymentRBS> list = new ArrayList<>();

        log.info("Initial size: {}", list.size());

        try {
            for (int i = 12; i < sheet.getPhysicalNumberOfRows(); i++) {

                PaymentRBS rbs = new PaymentRBS();
                XSSFRow row = sheet.getRow(i);

                if (row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING) {
                    String value = row.getCell(3).getStringCellValue();
                    String money = value.substring(1, value.length() - 1);
                    if (money.contains(",")) {

                        String money2 = money.replace(",", "");

                        rbs.setSum(Double.parseDouble(money2));
                    } else {
//                        String[] point = row.getCell(3).getStringCellValue().split("\\.");
////                        System.out.println("before: " + point[0]);
////                        String space = point[0].strip();
////                        System.out.println("after: " + space);

                        String parse = row.getCell(3).getStringCellValue();
                        String sum2 = parse.substring(1, parse.length()-1);
                        rbs.setSum(Double.parseDouble(sum2));

                    }
                }

                if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    System.out.println("double: " + row.getCell(3).getNumericCellValue());
                    rbs.setSum(row.getCell(3).getNumericCellValue());
                }


                String[] operation = row.getCell(4).toString().split("Дата");

                String[] str = operation[0].split(" ");
                String[] str2 = str[str.length - 1].split("\\.");
                rbs.setAccount(str2[0]);

                list.add(rbs);
            }

        } catch (NullPointerException | IllegalStateException | NumberFormatException n) {
            log.error("Пустая строка!");
            n.printStackTrace();
        }

        return list;
    }
}
