package io.ticloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.ticloud.entity.User;
import io.ticloud.utils.GetUrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wx")
public class WxController {
    private static final String APP_ID = "wx4de3b98101fdc2b9";
    private static final String SECRET = "dc52077dcba30effaa9a28b9c3cb3989";
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/userInfo/{code}")
    public User userInfo(@PathVariable String code){
        log.info("开始请求");
        User user = null;
        try {
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + "wxd707c87c8298d571" + "&secret=" + "a2fa7912493dd4870604d394885afe74" + "&code=" + code + "&grant_type=authorization_code";
            JSONObject urlJson = GetUrlUtils.getUrl(url);
            String accessToken = urlJson.getString("access_token");
            String openid = urlJson.getString("openid");
            log.info(openid);
            String getUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";
            JSONObject usrJson = GetUrlUtils.getUrl(getUserInfoUrl);
            user = JSONObject.toJavaObject(usrJson, User.class);
            log.info(JSON.toJSONString(user));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

}
