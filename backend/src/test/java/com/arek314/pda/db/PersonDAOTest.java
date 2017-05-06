package com.arek314.pda.db;

import com.arek314.pda.db.dao.PersonDAO;
import com.arek314.pda.db.mapper.PersonMapper;
import com.arek314.pda.db.model.Person;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class PersonDAOTest extends DAOTest {
    private PersonDAO personDAO;

    @Override
    @BeforeTest
    public void buildDatabase() {
        super.buildDatabase();
        personDAO = dbi.onDemand(PersonDAO.class);
    }

    @Override
    @BeforeMethod
    public void loadContent() throws Exception {
        super.loadContent();
    }

    private List<Person> getPeopleFromDatabase() throws Exception {
        return getAllEntities(Person.class, PersonMapper.class, "people");
    }

    @Override
    @AfterTest
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void getAllPeople() throws Exception {
        List<Person> peopleFromDataBase = getPeopleFromDatabase();
        List<Person> people = peopleFromDataBase.stream().collect(Collectors.toList());

        assertThat(personDAO.getAllPeople()).containsAll(people);
    }

    @Test
    public void getAllPeopleWithouthUser() throws Exception {
        List<Person> peopleFromDataBase = getPeopleFromDatabase();
        Person person = peopleFromDataBase.get(0);
        int id = person.getId();
        List<Person> people = peopleFromDataBase.stream().filter(personFromDB -> id != personFromDB.getId()).collect(Collectors.toList());

        assertThat(personDAO.getAllPeopleWithouthUser(id)).isSubsetOf(people);
        assertThat(personDAO.getAllPeopleWithouthUser(id)).doesNotContain(person);
    }

    @Test
    public void getSinglePerson() throws Exception {
        Person personFromDb = getPeopleFromDatabase().get(0);
        final Person person = personDAO.getPerson(personFromDb.getId());
        assertThat(person).isEqualTo(personFromDb);
    }

    @Test
    public void getPeopleOnlineWithouthUser() throws Exception {
        List<Person> peopleFromDatabase = getPeopleFromDatabase();
        List<Person> onlinePeople = peopleFromDatabase.stream().filter(personDromDb -> personDromDb.getIsOnline() == true).collect(Collectors.toList());
        Person person = onlinePeople.get(0);
        List<Person> onlinePeopleWithouthUser = onlinePeople.stream().filter(onlinePerson -> person.getId() != onlinePerson.getId()).collect(Collectors.toList());

        assertThat(personDAO.getOnlinePeopleWithouthUser(person.getId())).isSubsetOf(onlinePeople);
        assertThat(personDAO.getOnlinePeopleWithouthUser(person.getId())).doesNotContain(person);
    }

    @Test
    public void createPerson() throws Exception {
        final Person person = new Person(123456, 45.6845, 68.9987, false, "normal");
        personDAO.createPerson(person);
        assertThat(personDAO.getAllPeople().contains(person));
    }

    @Test
    public void updatePerson() throws Exception {
        Person person = getPeopleFromDatabase().get(0);
        Person updatedPerson = new Person(person.getId(), 11.1111, 22.2222, true, "updatedLabel");

        personDAO.updatePerson(updatedPerson);

        assertThat(getPeopleFromDatabase()).doesNotContain(person);
        assertThat(getPeopleFromDatabase()).contains(updatedPerson);
    }

    @Test
    public void deletePerson() throws Exception {
        final Person person = getPeopleFromDatabase().get(0);
        personDAO.deletePerson(person.getId());

        assertThat(getPeopleFromDatabase()).doesNotContain(person);
    }

    @Test(priority = 1)
    public void deleteAllPeople() throws Exception {
        List<Person> noPeople = new ArrayList<>();
        personDAO.deleteAllPeople();

        assertThat(getPeopleFromDatabase()).isSubsetOf(noPeople);
    }


}
