package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.Attribute;
import tn.dymes.store.repositories.AttributeRepository;

import java.util.List;

@Service
@Slf4j
public class AttributeServiceImpl implements IAttributeService {

    @Autowired
    AttributeRepository attributeRepository;


    @Override
    public void addAttribute(String name, String type) {
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setType(type);
        attributeRepository.save(attribute);
    }

    @Override
    public Attribute updateAttribute(long id, String name, String type) {
        Attribute attribute = attributeRepository.findById(id).orElse(null);
        if (attribute!=null){
            attribute.setName(name);
            attribute.setType(type);
            attributeRepository.save(attribute);
            return attribute;
        }
        return  null;
    }

    @Override
    public void removeAttribute(long id) {
        attributeRepository.deleteById(id);
    }

    @Override
    public List<Attribute> getAll() {
        List<Attribute> attributes = attributeRepository.findAll();
        if (attributes.size() == 0)
            return null;
        else
            return attributes;
    }

    @Override
    public int getNbElementsByID(long id) {
        Attribute attribute = attributeRepository.findById(id).orElse(null);
        if (attribute!= null){
            System.out.println("Number of elements in attribute id = "+id+" is "+ attribute.getElements().size());
            return attribute.getElements().size();
        }
        return -1;
    }
}
