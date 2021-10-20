package ru.javaops.masterjava.uploadpage;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public class ThymeleafAppUtil {
    private static final TemplateEngine templateEngine;

    static {
        ServletContextTemplateResolver templateResolver =
                new ServletContextTemplateResolver();
        templateResolver.setTemplateMode("XHTML");
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(3600000L);
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    public static TemplateEngine getTemplateEngine() {
        return templateEngine;
    }
} 