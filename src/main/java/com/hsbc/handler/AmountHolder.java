package com.hsbc.handler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangziquan
 * @date 2022/6/22 22:13
 */
public class AmountHolder {

    private AmountHolder(){}

    private volatile static ConcurrentHashMap<String, Integer> payment;

    public static ConcurrentHashMap<String, Integer> getPayment(){
        if (payment == null){
            synchronized (AmountHolder.class){
                if(payment == null){
                    payment = new ConcurrentHashMap<String, Integer>(16);
                }
            }
        }
        return payment;
    }

}
