package com.hsbc.controller.demo;

import com.hsbc.entity.CurrencyAmount;
import com.hsbc.handler.AmountHolder;
import com.hsbc.handler.RecordHandler;
import io.muserver.Method;
import io.muserver.MuServer;

import java.util.concurrent.ConcurrentHashMap;

import static io.muserver.MuServerBuilder.httpServer;


/**
 * 主程序
 * @author wangziquan
 * @date 2022/6/22 20:48
 */
public class Hello {


    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> payment = AmountHolder.getPayment();
        RecordHandler handler = new RecordHandler();

        // 新增muServer接口服务
        MuServer server = httpServer()
                .withHttpPort(5000)
                // 获取全部货币金额信息
                .addHandler(Method.GET, "/recoder/all", (req, resp, pathParams) -> {
                    resp.write(handler.getPrintPaymentString(payment));
                })
                // 根据货币获取金额信息
                .addHandler(Method.GET, "/recoder/{currency}", (req, resp, pathParams) -> {
                    String currency = pathParams.get("currency");
                    resp.write(handler.getPrintPaymentStringByCurrency(payment,currency));
                })
                // 货币加减金额
                .addHandler(Method.GET, "/recoder/{currency}/{operator:reduce|plus}/{amount : [0-9]+}", (req, resp, pathParams) -> {
                    String currency = pathParams.get("currency");
                    String amount = pathParams.get("amount");
                    String operator = pathParams.get("operator");
                    if(operator.equals("reduce")){
                        operator = "-";
                    }else {
                        operator = "";
                    }
                    CurrencyAmount currencyAmount = new CurrencyAmount(currency + " " +operator+ amount);
                    String msg = handler.save(currencyAmount);
                    resp.write(msg+" \n "+handler.getPrintPaymentString(payment));
                })
                .start();
        System.out.println("Server started at " + server.uri().resolve("/recoder/all"));

        // 文件读取处理
        handler.handleFileData();
        // 控制台读取处理
        handler.startProcess();

    }

}
