package org.yws.doggie.worker;

/**
 * Created by ywszjut on 15/10/1.
 */

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateRender {
    private static Logger log = LoggerFactory.getLogger(DateRender.class);
    private static Pattern pt = Pattern.compile("\\$\\{zdt.*?\\}");

    static {
        try {
            Velocity.init();
        } catch (Exception e) {
            log.error("Velocity init fail", e);
        }
    }

    public static String render(String template) {
        if (template == null) {
            return null;
        }
        Matcher matcher = pt.matcher(template);
        while (matcher.find()) {
            String m = template.substring(matcher.start(), matcher.end());
            StringWriter sw = new StringWriter();
            try {
                VelocityContext context = new VelocityContext();
                context.put("zdt", new DateTool());
                Velocity.evaluate(context, sw, "", m);
                if (m.equals(sw.toString())) {
                    //渲染后和原数据一样，则直接跳出，如果不跳出会导致死循环
                    log.error("render fail with target:" + m);
                    break;
                }
            } catch (Exception e) {
                log.error("zdt render error", e);
                break;//防止死循环
            }
            template = template.replace(m, sw.toString());
            matcher = pt.matcher(template);
        }
        //${yesterday}变量替换
        template = template.replace("${yesterday}", new DateTool().addDay(-1).format("yyyyMMdd"));
        return template;
    }

}
