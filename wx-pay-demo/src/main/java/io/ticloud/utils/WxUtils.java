package io.ticloud.utils;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;


public class WxUtils {

    private static final String APP_ID = "";
    private static final String SECRET = "";
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    public String getOpenId(String code){
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APP_ID + "&secret=" + SECRET + "&code=" + code + "&grant_type=authorization_code";
        String result = null;
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(get);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getUserInfo() {
        String openId = getOpenId("1234556");
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+"" +"&openid=" + openId + "&lang=zh_CN";
        HttpGet get = new HttpGet();
        return null;
    }

    /**
     * 生成用于获取access_token的Code的Url
     *
     * @param redirectUrl
     * @return
     */
    public String getRequestCodeUrl(String redirectUrl) {
        HttpsURLConnection httpUrlConn = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuffer buffer = null;
        try {
            //修改appID，secret
            String tokenUrl="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+""+"&secret="+"";
            //建立连接
            URL url = new URL(tokenUrl);
            httpUrlConn = (HttpsURLConnection) url.openConnection();

            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            httpUrlConn.setSSLSocketFactory(ssf);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);

            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod("GET");

            // 取得输入流
            inputStream = httpUrlConn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            //读取响应内容
            buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            System.out.println(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                // 释放资源
                if (inputStream != null) {
                    inputStream.close();
                }
                if (httpUrlConn != null) {
                    httpUrlConn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //输出返回结果
        return buffer.toString();
    }

    private String entityToString(HttpEntity entity) throws IOException {
        String result = null;
        if(entity != null)
        {
            long lenth = entity.getContentLength();
            if(lenth != -1 && lenth < 2048)
            {
                result = EntityUtils.toString(entity,"UTF-8");
            }else {
                InputStreamReader reader1 = new InputStreamReader(entity.getContent(), "UTF-8");
                CharArrayBuffer buffer = new CharArrayBuffer(2048);
                char[] tmp = new char[1024];
                int l;
                while((l = reader1.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }
                result = buffer.toString();
            }
        }
        return result;
    }
}
