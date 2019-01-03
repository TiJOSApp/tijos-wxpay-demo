package io.ticloud.servlet.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.ticloud.servlet.entity.AccessTokenEnity;
import io.ticloud.servlet.utils.SignUtil;
import io.ticloud.servlet.utils.UrlUtils;
import io.ticloud.servlet.utils.WeCharHandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping()
public class WeiXinCoreControler {

    private static final long serialVersionUID = -5021188348833856475L;
    private static Logger logger = LoggerFactory.getLogger(WeiXinCoreControler.class);

    private static final String APP_ID = "wxd707c87c8298d571";
    private static final String SECRET = "a2fa7912493dd4870604d394885afe74";
    private AccessTokenEnity accessToken = null;

    @RequestMapping("wx")
    public String doGet(HttpServletRequest request) throws Exception {
        //获取微信加密签名
        String signature = request.getParameter("signature");
        //时间戳
        String timestamp = request.getParameter("timestamp");
        //随机数
        String nonce = request.getParameter("nonce");
        //随机字符串
        String echostr = request.getParameter("echostr");

        logger.info("signature is :" + signature + " timestamp is：" + timestamp + "  nonce is： :" + nonce);
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            System.out.println("-----------验证微信服务号结束------------");
            Map <String, String> requestXmlMap = WeCharHandlerUtil.parseXml(request);
            for (Map.Entry <String, String> entry : requestXmlMap.entrySet()) {
                System.out.println("属性名：" + entry.getKey() + "属性值：" + entry.getValue());
            }
            if (requestXmlMap.get("MsgType").equalsIgnoreCase("event")) {
                if (requestXmlMap.get("Event").equalsIgnoreCase("subscribe")) {
                    //获取全部openId
                    accessToken = getAccessToken();
                    String accessToken = this.accessToken.getAccessToken();
                    System.out.println(accessToken);

                    String getUserListUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + accessToken + "&next_openid=";
                    JSONObject userList = UrlUtils.getUrl(getUserListUrl);
                    JSONObject data = userList.getJSONObject("data");
                    JSONArray openidList = data.getJSONArray("openid");
                    boolean flag = openidList.contains(requestXmlMap.get("FromUserName"));
                    if (flag) {
                        logger.info("这是个老用户");
                    } else {
                        logger.info("这是个新用户");
                    }
                }
            }

            return echostr;
        } else {
            // 此处可以实现其他逻辑
            logger.warn("不是微信服务器发过来的请求，请小心！");
            return null;
        }
    }

    public AccessTokenEnity getAccessToken() throws Exception {
        if (accessToken == null || !accessToken.getExpireTime()) {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APP_ID + "&secret=" + SECRET;
            JSONObject accessTokenJson = UrlUtils.getUrl(url);
            logger.info(JSON.toJSONString(accessTokenJson));
            if (accessTokenJson == null && (accessTokenJson.getInteger("errcode") != null) && accessTokenJson.getInteger("errcode") == 40001) {
                logger.error("accessToken获取失败");
            } else {
                AccessTokenEnity accessTokenEnity = accessTokenJson.toJavaObject(AccessTokenEnity.class);
                this.accessToken = accessTokenEnity;
            }
        }
        return accessToken;
    }

    @RequestMapping("/create_QRCode")
    public String createQRCode(@RequestParam("devicekey") String devicekey, @RequestParam("productkey") String productkey) {
        try {
            this.accessToken = getAccessToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("accessToken :" + JSON.toJSONString(accessToken));

        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + this.accessToken.getAccessToken();
        Map <String, Object> params = new HashMap <>(16);
        params.put("action_name", "QR_LIMIT_STR_SCENE");
        Map <String, Object> actionInfo = new HashMap <>(16);
        Map <String, Object> scene = new HashMap <>(16);
        scene.put("scene_str", "/" + productkey + "/" + devicekey);
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);
        String paramsStr = JSON.toJSONString(params);
        try {
            JSONObject jsonObject = UrlUtils.httpRequest(url,"POST", paramsStr);
            if (jsonObject == null) {
                throw new RuntimeException("获取二维码失败");
            }
            String s = JSON.toJSONString(jsonObject);
            logger.info(s);
            String qRCodeUrl = jsonObject.getString("url");
            return qRCodeUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
