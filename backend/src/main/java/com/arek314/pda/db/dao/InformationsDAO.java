package com.arek314.pda.db.dao;

import com.arek314.pda.db.mapper.InformationMapper;
import com.arek314.pda.db.model.Information;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Collection;

@RegisterMapper(InformationMapper.class)
public abstract class InformationsDAO {

    @SqlQuery("select id, \"mapURL\" from informations")
    public abstract Collection<Information> getAllInformations();

    @SqlUpdate("insert into informations (\"mapURL\") values (:mapURL)")
    public abstract void createInformation(@BindBean Information information);

    @SqlUpdate("update informations set \"mapURL\" = :mapURL where id in (select id from informations order by id " + "desc limit 1)")
    public abstract void updateInformation(@BindBean Information information);

    @SqlQuery("select id, \"mapURL\" from informations order by id desc limit 1")
    public abstract Information getInformation();

    @SqlUpdate("delete from informations")
    abstract void removeAll();

    @SqlUpdate("alter table informations alter column id restart with 1")
    abstract void resetIdCounter();

    public void deleteAll() {
        removeAll();
    }

}
