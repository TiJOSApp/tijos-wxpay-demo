package com.github.wxpay.sdk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class MyConfig extends WXPayConfig {

    private byte[] certData;

    public MyConfig() throws Exception {
        String certPath = "/path/to/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = getClass().getClassLoader().getResourceAsStream(certPath);
//        File file = ResourceUtils.getFile("classpath:path/to/apiclient_cert.p12");
//        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    @Override
    public String getAppID() {
        return "wxd707c87c8298d571";
    }

    @Override
    public String getMchID() {
        return "1521989221";
    }

    @Override
    public String getKey() {
        return "6QdZw7DViGh62bFe9MY9GaXHRriOE1IC";
    }

    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }


    @Override
    public IWXPayDomain getWXPayDomain() { // 这个方法需要这样实现, 否则无法正常初始化WXPay
        IWXPayDomain iwxPayDomain = new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }

            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new IWXPayDomain.DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        };
        return iwxPayDomain;

    }
}
