package org.yws.doggieweb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by wangshu.yang on 2015/7/28.
 */

@Controller
public class IndexController {

    @RequestMapping("/")
    public String index() {
       return "index";
    }
}
