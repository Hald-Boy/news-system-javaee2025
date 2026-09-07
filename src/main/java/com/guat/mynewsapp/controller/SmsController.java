package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.SmsSendDTO;
import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 短信接口
 */
@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    /**
     * 发送短信验证码（模拟实现，验证码打印在控制台日志）
     * @param smsSendDTO 封装手机号和使用场景参数
     * @return 统一返回格式
     */
    @PostMapping("/send")
    public Result send(@RequestBody SmsSendDTO smsSendDTO) {
        smsService.sendCode(smsSendDTO.getPhone(), smsSendDTO.getScene());
        return Result.success();
    }
}
