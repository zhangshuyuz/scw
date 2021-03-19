package com.yuu.scw.project.component;


import com.yuu.scw.common.vo.resp.AppResponse;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SmsTemplate {

    @Value("${sms.host}")
    String host;

    @Value("${sms.path}")
    String path;

    @Value("${sms.method}")
    String method;

    @Value("${sms.appcode}")
    String appcode;

    public AppResponse<String> sendMessage(Map<String, String> querys) {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);

        Map<String, String> bodys = new HashMap<String, String>();


        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
            return AppResponse.ok(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return AppResponse.fail(null);
        }

    }

}
