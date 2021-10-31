package com.example.register.controller;

import com.example.register.model.PaymentPartner;
import com.example.register.model.PaymentRBS;
import com.example.register.service.PartnerService;
import com.example.register.service.RbsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final PartnerService partnerService;
    private final RbsService rbsService;

    private List<PaymentRBS> rbsList;
    private List<PaymentPartner> partnerList;

    private List<PaymentRBS> notMatchedRBS;
    private List<PaymentPartner> notMatchedPartner;

    @PostMapping("/import")
    public String mapReapExcel(@RequestParam("rbs_file") MultipartFile rbsFile, @RequestParam("partner_file") MultipartFile partnerFile) throws IOException {
        partnerList = partnerService.read(partnerFile);
        rbsList = rbsService.read(rbsFile);

        log.info("Partner size: {}", partnerList.size());

        log.info("======================================");

        log.info("RBS size: {}", rbsList.size());

        log.info("Start to compare");
        compare();
        return "redirect:/list";
    }

    @GetMapping("/list")
    public String result(Model model){
        model.addAttribute("notMatchedPartner", notMatchedPartner);
        model.addAttribute("notMatchedRBS", notMatchedRBS);

        return "result";
    }


    public void compare() {
        log.info("ТРАНЗАКЦИИ КОТОРЫЕ НЕ СОВПАЛИ:");
        try {
            for (PaymentRBS rbs : rbsList) {
                for (PaymentPartner partner : partnerList) {
                    if (partner.getAccount().equals(rbs.getAccount()) && partner.getSum() == rbs.getSum()) {
                        rbs.setExist(true);
                        partner.setExist(true);
                    }
                }
            }
        } catch (OutOfMemoryError | ConcurrentModificationException exception) {
            exception.printStackTrace();
        }

        notMatchedRBS = rbsList.stream().filter(r -> !r.isExist()).collect(Collectors.toList());
        notMatchedPartner = partnerList.stream().filter(p -> !p.isExist()).collect(Collectors.toList());

    }
}