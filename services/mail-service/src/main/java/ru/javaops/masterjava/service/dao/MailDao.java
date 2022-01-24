package ru.javaops.masterjava.service.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.dao.AbstractDao;
import ru.javaops.masterjava.service.model.MailState;

import java.util.Collection;
import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class MailDao implements AbstractDao {

    @SqlUpdate("TRUNCATE mail_hist CASCADE ")
    @Override
    public abstract void clean();

    @SqlQuery("SELECT * FROM mail_hist")
    public abstract List<MailState> getAll();

    @SqlUpdate("INSERT INTO mail_hist (list_to, list_cc, subject, state, datetime) " +
            "VALUES (:list_to, :list_cc, :subject, :state, :datetime)")
    public abstract void insert(@BindBean MailState mailState);

    @SqlUpdate("INSERT INTO mail_hist (list_to, list_cc, subject, state, datetime) " +
            "VALUES (:list_to, :list_cc, :subject, :state, :datetime)")
    public abstract void insertBatch(@BindBean Collection<MailState> mailStates);
}
