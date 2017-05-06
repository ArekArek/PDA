package com.arek314.pda.db;

import com.arek314.pda.db.dao.InformationsDAO;
import com.arek314.pda.db.mapper.InformationMapper;
import com.arek314.pda.db.model.Information;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class InformationsDAOTest extends DAOTest {
    private InformationsDAO informationsDAO;

    @Override
    @BeforeTest
    public void buildDatabase() {
        super.buildDatabase();
        informationsDAO = dbi.onDemand(InformationsDAO.class);
    }

    @Override
    @BeforeMethod
    public void loadContent() throws Exception {
        super.loadContent();
    }

    private List<Information> getInformationsFromDatabase() throws Exception {
        return getAllEntities(Information.class, InformationMapper.class, "informations");
    }

    @Override
    @AfterTest
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void getAllInformations() throws Exception {
        List<Information> informationsFromDatabase = getInformationsFromDatabase();
        List<Information> informations = informationsFromDatabase.stream().collect(Collectors.toList());

        assertThat(informationsDAO.getAllInformations()).containsAll(informations);
    }

    @Test
    public void createInformation() throws Exception {
        Information information = new Information("test");
        informationsDAO.createInformation(information);

        List<Information> informationsFromDatabase = informationsDAO.getAllInformations().stream().collect(Collectors.toList());
        Information lastInformation = informationsFromDatabase.get(informationsFromDatabase.size() - 1);

        assertThat(lastInformation.getMapURL()).isEqualTo(information.getMapURL());
    }

    @Test
    public void updateInformation() throws Exception {
        List<Information> beforeInformations = informationsDAO.getAllInformations().stream().collect(Collectors.toList());
        Information lastInformation = beforeInformations.get(beforeInformations.size() - 1);

        Information information = new Information("testUpdate");
        informationsDAO.updateInformation(information);

        List<Information> afterInformations = informationsDAO.getAllInformations().stream().collect(Collectors.toList());
        Information newLastInformation = afterInformations.get(afterInformations.size() - 1);

        assertThat(afterInformations.size()).isEqualTo(beforeInformations.size());
        assertThat(newLastInformation.getMapURL()).isEqualTo(information.getMapURL());
        assertThat(newLastInformation.getId()).isEqualTo(lastInformation.getId());
    }

    @Test
    public void getInformation() throws Exception {
        Information information = informationsDAO.getInformation();

        List<Information> informationsFromDatabase = getInformationsFromDatabase().stream().collect(Collectors.toList());
        Information lastInformation = informationsFromDatabase.get(informationsFromDatabase.size() - 1);

        assertThat(lastInformation).isEqualToComparingFieldByField(information);
    }

    @Test(priority = 1)
    public void deleteAll() throws Exception {
        informationsDAO.deleteAll();
        assertThat(getInformationsFromDatabase()).isEmpty();
    }

    @Test(priority = 2)
    public void getInformationFromEmpty() throws Exception {
        Information information = informationsDAO.getInformation();

        assertThat(information).isNull();
    }
}
