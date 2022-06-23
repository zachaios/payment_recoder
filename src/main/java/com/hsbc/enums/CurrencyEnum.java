package com.hsbc.enums;

/**
 * @author wangziquan
 * @date 2022/6/22 20:55
 */
public enum CurrencyEnum {

    USD("USD"),
    HKD("HKD"),
    CNY("CNY");

    private final String currencyName;


    private CurrencyEnum(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencyName() {
        return currencyName;
    }

}