package io.ticloud.controller;


import com.github.wxpay.sdk.MyConfig;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import io.ticloud.utils.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class WxPayController {

    @GetMapping("/pay")
    public Map <String, String> wxPayFunction(HttpServletRequest request, @RequestParam("openid") String openid, @RequestParam("money") Double money) throws Exception {

        MyConfig config = new MyConfig();
        WXPay wxPay = new WXPay(config);
        //设置参数
        Map <String, String> params = new HashMap <>(16);
        params.put("appid", config.getAppID());
        params.put("mch_id", config.getMchID());
        params.put("body", "TiJOSDemo");
        params.put("out_trade_no", String.valueOf(new IdWorker().nextId()));
        String s = WXPayUtil.generateSignature(params, config.getKey());
        params.put("sign", s);
        params.put("total_fee", "1");
        params.put("spbill_create_ip", getIp2(request));
        System.out.println("IP："+getIp2(request));
        params.put("notify_url", "www.baidu.com");
        params.put("trade_type", "JSAPI");
        params.put("openid", openid);
        //设置随机字符串
        params.put("nonce_str", String.valueOf(new IdWorker().nextId()));
        params.put("sign_type", "MD5");



        Map <String, String> respData = wxPay.unifiedOrder(params);
        System.out.println("返回参数：" + respData);
        if (respData.get("return_code").equals("SUCCESS")) {
            //返回给APP端的参数，APP端再调起支付接口
            Map <String, String> repData = new HashMap <>(16);
            repData.put("appId", respData.get("appid"));
            repData.put("package", "prepay_id=" + respData.get("prepay_id"));
            repData.put("nonceStr", respData.get("nonce_str"));
            repData.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            repData.put("signType", "MD5");
            System.out.println("签名生成参数："+ repData);
            //签名
            String paySign = WXPayUtil.generateSignature(repData, config.getKey());
            repData.put("paySign", paySign);

            respData.put("appId", config.getAppID());
            respData.put("timestamp", repData.get("timestamp"));
            respData.put("package", "prepay_id=" + respData.get("prepay_id"));
            respData.put("signType", "MD5");
            return repData;
        }
        throw new RuntimeException(respData.get("return_msg"));
    }

    public static String getIp2(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if(StringUtils.isNotBlank(ip) && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                System.out.println(ip.substring(0,index));
                return ip.substring(0,index);
            }else{
                System.out.println(ip);
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            System.out.println(ip);
            return ip;
        }
        return request.getRemoteAddr();
    }
}
