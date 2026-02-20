package com.tradeops.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {
  @GetMapping("/ping")
  public ResponseEntity<String> hello(){
    return ResponseEntity.ok("hello");
  }
}
