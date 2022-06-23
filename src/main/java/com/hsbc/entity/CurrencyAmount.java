package com.hsbc.entity;

import com.hsbc.enums.CurrencyEnum;

/**
 * 货币金额entity
 * 通过构造器做参数校验，充血模式，职责单一化
 * @author wangziquan
 * @date 2022/6/23 11:23
 */
public class CurrencyAmount {

    private String currency;
    private int amount;

    public CurrencyAmount(String currencyAmount){
        // 判断是否输入空字符串
        String val1 = currencyAmount.replaceAll(" ", "");
        if(val1.length() == 0){
            throw new RuntimeException();
        }
        // 判断是否属于合法货币编码
        String currency = val1.substring(0, 3);
        if (CurrencyEnum.valueOf(currency) == null){
            throw new RuntimeException();
        }
        // 判断金额是否合法
        String amountStr = val1.replaceAll(currency, "");
        if (amountStr.length() == 0) {
            throw new RuntimeException();
        }
        Integer amount = null;
        try {
            amount = Integer.valueOf(amountStr);
        }catch (Exception e){
            throw new RuntimeException();
        }
        if(amount == null && amount.intValue() == 0){
            throw new RuntimeException();
        }
        this.currency = currency;
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public int getAmount() {
        return amount;
    }

}
