package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.config.Configs;

/**
 * @author A.Khalitova
 * 24-Jan-2022
 */
@Slf4j
@UtilityClass
public class MailConfig {
    private static final Config config;

    static {
        config = Configs.getConfig("mail.conf", "mail");
        if (config == null || config.isEmpty()) {
            log.info("Mail Config is not resolved");
        }
    }

    String getString(String parameter) {
        return config.getString(parameter);
    }

    boolean getBoolean(String parameter) {
        return config.getBoolean(parameter);
    }

    int getInt(String parameter) {
        return config.getInt(parameter);
    }
}
