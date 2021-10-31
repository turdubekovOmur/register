package com.example.register.service;

import com.example.register.interfaces.Readable;
import com.example.register.model.PaymentPartner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class PartnerService implements Readable {



    @Override
    public List read(MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        List<PaymentPartner> list = new ArrayList<>();
        log.info("ПОПЫТКА ПАРСИНГА ПАРТНЕРСКОГО ФАЙЛА...");
        try{
            for (int i = 12; i < worksheet.getPhysicalNumberOfRows(); i++) {

                XSSFRow row = worksheet.getRow(i);

                PaymentPartner partner = new PaymentPartner();
                partner.setAccount(row.getCell(6).getStringCellValue());
                partner.setSum(row.getCell(3).getNumericCellValue());

                list.add(partner);
            }
        }catch (NullPointerException | IllegalStateException e){
            log.error("Пустое поле или строка в xlsx фале! {}", e);
        }
        return list;
    }
}
