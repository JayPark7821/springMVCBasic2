package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//@Controller -> String으로 반환하면 뷰리졸버를 찾는다.....
@Slf4j
@RestController // 문자열로 반환하면 문자열 그대로 반환한다.
public class LogTestController {
//    private final Logger log = LoggerFactory.getLogger(getClass()); //LogTestController.class

    @RequestMapping("/log-test")
    public String logTest(){
        String name = "Spring";
        log.trace("trace log={}", name);
        log.debug("debug log={}", name);
        log.info("info log ={}", name);
        log.warn("warn log={}", name);
        log.error("error log={}", name);


        return "ok";
    }
}
