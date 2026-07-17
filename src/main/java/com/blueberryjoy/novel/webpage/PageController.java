package com.blueberryjoy.novel.webpage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping(value = {
            "/",
            "/{path:^(?!api|assets|.*\\..*$).*$}",
            "/**/{path:^(?!api|assets|.*\\..*$).*$}"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
