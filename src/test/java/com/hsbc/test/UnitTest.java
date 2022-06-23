package com.hsbc.test;

import com.hsbc.entity.CurrencyAmount;
import com.hsbc.enums.CurrencyEnum;
import com.hsbc.handler.AmountHolder;
import com.hsbc.handler.RecordHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangziquan
 * @date 2022/6/23 1:42
 */
public class UnitTest {

    @Test
    public void paymentRecordTest()  {
        System.out.println("======JUNIT TEST START======");
        RecordHandler handler = new RecordHandler();
        URL url = getClass().getClassLoader().getResource("data.txt");
        File file = new File(url.getFile());
        try(
                FileReader fileReader = new FileReader(file);
                BufferedReader br = new BufferedReader(fileReader);
        ){
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                CurrencyAmount currencyAmount = new CurrencyAmount(line);
                handler.save(currencyAmount);
            }
        }catch (IOException e){
            System.out.println("文件读取错误");
            e.printStackTrace();
        }
        handler.save(new CurrencyAmount( "USD 99"));
        handler.save(new CurrencyAmount( "HKD 97"));
        handler.save(new CurrencyAmount( "CNY 98"));
        ConcurrentHashMap<String, Integer> payment = AmountHolder.getPayment();
        boolean cny = payment.get(CurrencyEnum.CNY.getCurrencyName()).intValue() == 100;
        boolean hkd = payment.get(CurrencyEnum.HKD.getCurrencyName()).intValue() == 100;
        boolean usd = payment.get(CurrencyEnum.USD.getCurrencyName()).intValue() == 100;
        Assert.assertTrue(cny);
        Assert.assertTrue(hkd);
        Assert.assertTrue(usd);
        System.out.println("======JUNIT TEST END======");
    }

}
