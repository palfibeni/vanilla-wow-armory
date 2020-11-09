package com.palfib.vanilla.wow.armory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeartbeatController {

    @GetMapping("/heart-beat")
    public String getHeartBeat() {
        return "Stayin' Alive";
    }
}
