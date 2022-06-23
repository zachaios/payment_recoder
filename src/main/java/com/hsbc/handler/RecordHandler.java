package com.hsbc.handler;

import com.hsbc.entity.CurrencyAmount;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * 金额记录处理器
 * @author wangziquan
 * @date 2022/6/22 21:11
 */
public class RecordHandler {

    /**
     * 1、开启控制台监听
     * 2、根据控制台键入路径读取并保存文件数据
     * 3、开启定时打印方法
     */
    public void handleFileData(){
        String filePath = openScan("请输入一个txt文件全路径，如：C:\\wzq\\project\\payment_recoder\\src\\main\\resources\\data.txt，输入“pass”跳过");
        if(filePath.equals("pass")){
            // 调用定时打印方法
            startPrintPayment();
            return;
        }
        List<String> list = null;
        File file = new File(filePath);
        try(
                FileReader fileReader = new FileReader(file);
                BufferedReader br = new BufferedReader(fileReader);
        ){
            list = br.lines().collect(Collectors.toList());
        }catch (IOException e){
            System.out.println("文件读取错误:"+e.getMessage());
            System.exit(1);
        }
        if(list.size() > 0){
            try {
                list.stream().forEach(line->{
                        // 生成货币金额对象，并校验
                        CurrencyAmount currencyAmount = new CurrencyAmount(line);
                        // 保存数据
                        save(currencyAmount);
                    }
                );
            }catch (Exception e){
                System.out.println("文件内容格式有误，请仔细检查，每行的格式为： USD 1000");
                System.exit(1);
            }
            // 调用定时打印方法
            startPrintPayment();
        }
    }

    /**
     * 开始执行控制台监听处理程序
     */
    public void startProcess(){
        // 开启控制台监听
        String currencyAndAmount = openScan("您可以继续输入，如 USD 1000");
        // 监听是否要退出程序
        if(currencyAndAmount.equals("quit")){
            System.exit(1);
        }
        // 处理控制台的数据
        try{
            // 生成货币金额对象，并在构造器中校验
            CurrencyAmount currencyAmount = new CurrencyAmount(currencyAndAmount);
            // 保存数据
            save(currencyAmount);
        }catch (Exception e){
            System.out.println("格式错误，请重新输入，如 USD 1000");
        }finally {
            // 控制台输入参数格式错误，允许重新输入
            startProcess();
        }
    }

    /**
     * 开启控制台监听
     * @param msg 控制台提示信息
     * @return 控制台输入的信息
     */
    public String openScan(String msg){
        System.out.println(msg);
        // 监听控制台数据
        Scanner sc = new Scanner(System.in);
        //读取字符串型输入
        return sc.nextLine();
    }

    /**
     * 校验、保存数据
     */
    public void save(CurrencyAmount currencyAndAmount){
        // 获取存储的map
        ConcurrentHashMap<String, Integer> payment = AmountHolder.getPayment();
        // 判断货币是否存在
        if(payment.containsKey(currencyAndAmount.getCurrency())){
            // 如果货币存在，则金额相加
            Integer amountAll = payment.get(currencyAndAmount.getCurrency());
            int newAmountAll = amountAll.intValue() + currencyAndAmount.getAmount();
            if(newAmountAll < 0){
                System.out.println(currencyAndAmount.getCurrency()+"货币金额不足，请重新键入");
            }else{
                payment.put( currencyAndAmount.getCurrency(),  newAmountAll);
            }
        }else{
            // 货币不存在，则存入
            payment.put( currencyAndAmount.getCurrency(),currencyAndAmount.getAmount() );
        }
    }


    /**
     * 打印货币金额
     */
    public void startPrintPayment(){
        ConcurrentHashMap<String, Integer> payment = AmountHolder.getPayment();
        // 开启打印输出任务
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(getPrintPaymentString(payment));
            }
        },10000,60000);
    }

    /**
     * 获取金额打印字符串
     * @param payment
     * @return
     */
    public String getPrintPaymentString(ConcurrentHashMap<String, Integer> payment){
        StringBuilder paymentStr = new StringBuilder();
        paymentStr.append("=== "+new Date(System.currentTimeMillis()).toString()+" 最新金额统计如下  ===");
        payment.entrySet().stream().forEach(e->{
            if(e.getValue().intValue()!=0){
                paymentStr.append("\r\n");
                paymentStr.append(e.getKey()+"总额："+e.getValue());
            }
        });
        return paymentStr.toString();
    }

    /**
     * 获取金额打印字符串
     * @param payment
     * @param currency
     * @return
     */
    public String getPrintPaymentStringByCurrency(ConcurrentHashMap<String, Integer> payment,String currency){
        StringBuilder paymentStr = new StringBuilder();
        if(payment.containsKey(currency)){
            Integer amount = payment.get(currency);
            if(amount.intValue() >0 ){
                paymentStr.append(currency);
                paymentStr.append(":");
                paymentStr.append(amount);
            }
        }
        return paymentStr.toString();
    }
}
