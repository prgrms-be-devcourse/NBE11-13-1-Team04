package com.springbeans.cafemenumanagement.global.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaviconController {

    @GetMapping("favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
        // 브라우저가 favicon 요청 시 404 에러 대신 아무것도 없는 200 OK를 내려주어 에러를 방지함
    }
}