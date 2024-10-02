package com.knu.subway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdmobController {
    @GetMapping("/app-ads.txt")
    public String app_ads(){
        return "google.com, pub-9460510334927836, DIRECT, f08c47fec0942fa0";
    }
}
