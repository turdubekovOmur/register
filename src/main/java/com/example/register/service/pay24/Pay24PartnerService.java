package com.example.register.service.pay24;

import com.example.register.interfaces.Readable;
import com.example.register.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class Pay24PartnerService implements Readable {
    @Override
    public List read(MultipartFile file) throws IOException {
        Workbook workbook = new HSSFWorkbook(file.getInputStream());
        Sheet worksheet =  workbook.getSheetAt(0);

        List<Payment> list = new ArrayList<>();

        log.info("ПОПЫТКА ПАРСИНГА ПАРТНЕРСКОГО ФАЙЛА...");
        try{
            for (int i = 0; i < worksheet.getPhysicalNumberOfRows(); i++) {

                Row row = worksheet.getRow(i);
                Payment partner = new Payment();


                String date = String.valueOf(row.getCell(0).getDateCellValue());

                int akk = (int) row.getCell(6).getNumericCellValue();
                String account = String.valueOf(akk);
                account = account.replaceAll("\\u00a0", "");

                double sum = row.getCell(7).getNumericCellValue();

                partner.setAccount(account);
                partner.setSum(sum);
                partner.setDate(date);

                list.add(partner);
            }
        }catch (NullPointerException | IllegalStateException e){
            log.error("Пустое поле или строка в xlsx фале! {}", e);
        }
        return list;
    }
}
