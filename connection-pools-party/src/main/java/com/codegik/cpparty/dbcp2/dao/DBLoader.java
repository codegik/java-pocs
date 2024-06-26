package com.codegik.cpparty.dbcp2.dao;

import com.codegik.cpparty.dbcp2.service.PersonService;
import com.codegik.cpparty.dbcp2.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DBLoader {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PersonService service;

    public void insertData() {
        try{
            jdbcTemplate.execute("INSERT INTO person(first_name,last_name) VALUES('Victor', 'Hugo')");
            jdbcTemplate.execute("INSERT INTO person(first_name,last_name) VALUES('Dante', 'Alighieri')");
            jdbcTemplate.execute("INSERT INTO person(first_name,last_name) VALUES('Stefan', 'Zweig')");
            jdbcTemplate.execute("INSERT INTO person(first_name,last_name) VALUES('Oscar', 'Wilde')");

            Person p = new Person();
            p.setFirstName("Diego");
            p.setLastName("Pacheco");
            service.save(p);

        }catch(Exception e){
            System.out.println(e);
        }
    }

}
