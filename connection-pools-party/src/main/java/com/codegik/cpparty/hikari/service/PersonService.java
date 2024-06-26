package com.codegik.cpparty.hikari.service;

import com.codegik.cpparty.hikari.dao.PersonRepository;
import com.codegik.cpparty.hikari.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {

    @Autowired
    PersonRepository repository;

    public List<Person> getAllPeople(){
        List<Person> result = new ArrayList<Person>();
        repository.findAll().forEach(result::add);
        return result;
    }

    public void save(Person p){
        repository.save(p);
    }

}
