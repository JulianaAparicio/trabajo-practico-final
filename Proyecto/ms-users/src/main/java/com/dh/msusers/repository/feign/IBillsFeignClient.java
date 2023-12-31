package com.dh.msusers.repository.feign;

import com.dh.msusers.configuration.feing.OAuthFeignConfig;
import com.dh.msusers.model.Bill;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ms-bills",configuration = OAuthFeignConfig.class)
public interface IBillsFeignClient {
  @GetMapping("/api/v1/bills/findById")
  ResponseEntity<List<Bill>> findByCustomer(@RequestParam String customerBill);

}
