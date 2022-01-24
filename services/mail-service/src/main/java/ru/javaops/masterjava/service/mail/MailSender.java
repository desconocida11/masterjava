package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.service.dao.MailDao;
import ru.javaops.masterjava.service.model.MailState;

import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MailSender {

    private static final MailDao dao;

    static {
        Config db = Configs.getConfig("persist.conf", "db");
        initDBI(db.getString("url"), db.getString("user"), db.getString("password"));
        dao = DBIProvider.getDao(MailDao.class);
    }

    private static void initDBI(String dbUrl, String dbUser, String dbPassword) {
        DBIProvider.init(() -> {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("PostgreSQL driver not found", e);
            }
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        });
    }

    static Email buildEmail(String subject, String body, List<String> to, List<String> cc) throws EmailException {
        Email email = new SimpleEmail();
        email.setHostName(MailConfig.getString("host"));
        email.setSmtpPort(MailConfig.getInt("port"));
        email.setAuthenticator(new DefaultAuthenticator(
                MailConfig.getString("username"),
                MailConfig.getString("password")));
        email.setSSLOnConnect(MailConfig.getBoolean("useSSL"));
        email.setSubject(subject);
        email.setFrom("khalitovaae@yandex.ru");
        email.setMsg(body);
        for (String adr: to) {
            email.addTo(adr);
            for (String copy: cc) {
                email.addCc(copy);
            }
        }
        return email;
    }

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        final List<String> toList = to == null ? Collections.emptyList() : to.stream().map(Addressee::getEmail).collect(Collectors.toList());
        final List<String> ccList = cc == null ? Collections.emptyList() : cc.stream().map(Addressee::getEmail).collect(Collectors.toList());

        try {
            buildEmail(subject, body, toList, ccList).send();
        } catch (EmailException e) {
            log.info("Failed sending with exception {}, \nto \n{}, \ncc \n{}, \nsubject={}, body={}",
                    e.getMessage(), toList, ccList, subject, (log.isDebugEnabled() ? "\n" + body : ""));
            DBIProvider.getDBI().useTransaction((conn, status) ->
                    dao.insert(new MailState(String.join(", ", toList), String.join(", ", ccList),
                            subject, "failed", LocalDateTime.now())));
            return;
        }
        log.info("Send mail to '" + to + "' cc '" + cc + "' subject '" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        DBIProvider.getDBI().useTransaction((conn, status) ->
                dao.insert(new MailState(String.join(", ", toList), String.join(", ", ccList),
                        subject, "success", LocalDateTime.now())));
    }
}
