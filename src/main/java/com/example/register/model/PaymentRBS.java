package com.example.register.model;

import lombok.Data;
import org.springframework.stereotype.Component;


@Data
@Component
public class PaymentRBS {
    private double sum;
    private String account;
    private boolean isExist;

    public int hashCode() {
        return String.format("%d,%f", getAccount(), getSum()).hashCode();
    }


}
