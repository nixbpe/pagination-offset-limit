package com.bluebik.demo.pageable;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class DemoController {

    @GetMapping("/demo")
    public Pageable demo(Pageable pageable) {
        return pageable;
    }

}
