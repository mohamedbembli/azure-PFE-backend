package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.Attribute;
import tn.dymes.store.entites.Element;
import tn.dymes.store.repositories.AttributeRepository;
import tn.dymes.store.repositories.ElementRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ElementServiceImpl implements IElementService{

    @Autowired
    AttributeRepository attributeRepository;

    @Autowired
    ElementRepository elementRepository;

    @Override
    public Element getElementByName(String name) {
        List<Element> elements = (List<Element>) elementRepository.findAll();
        for (Element element: elements) {
            if (element.getName().toLowerCase().equals(name.toLowerCase()))
               return  element;
        }
        return null;
    }

    @Override
    public void addElement(long attributeID, String name, String reference) {
        Attribute attribute = attributeRepository.findById(attributeID).orElse(null);
        Element element = new Element();
        element.setName(capitalizeFirstLetter(name.toLowerCase()));
        element.setReference(capitalizeFirstLetter(reference.toLowerCase()));
        element.setAttribute(attribute);
        elementRepository.save(element);

    }

    @Override
    public void removeElement(long attributeID, String name, String reference) {
        List<Element> elements = (List<Element>) elementRepository.findAll();
        for (Element element: elements) {
            if (element.getAttribute().getId() == attributeID && element.getName().equals(capitalizeFirstLetter(name.toLowerCase())) && element.getReference().equals(capitalizeFirstLetter(reference.toLowerCase()))) {
                elementRepository.delete(element);
            }
        }
    }

    @Override
    public Element updateElement(long elementID, String name, String reference) {
        Element element = elementRepository.findById(elementID).orElse(null);
        if (element != null){
            element.setName(capitalizeFirstLetter(name.toLowerCase()));
            element.setReference(capitalizeFirstLetter(reference.toLowerCase()));
            elementRepository.save(element);
            return  element;
        }
        return null;
    }

    @Override
    public boolean isExist(long attributeID, String name, String reference) {
        List<Element> elements = (List<Element>) elementRepository.findAll();
        for (Element element: elements){
            if ( (element.getName().equals(capitalizeFirstLetter(name.toLowerCase())) && element.getReference().equals(capitalizeFirstLetter(reference.toLowerCase())) && element.getAttribute().getId() == attributeID) || (element.getName().equals(capitalizeFirstLetter(name.toLowerCase()))))
            {
                System.out.println("ELEMENT FOUND");
                return true;
            }
        }
        System.out.println("ELEMENT NOT FOUND");
        return false;
    }

    @Override
    public List<Element> getElementsByAttributeID(long attributeID) {
        List<Element> elements = (List<Element>) elementRepository.findAll();
        List<Element> filteredElements = new ArrayList<>();

        for (Element element: elements) {
            if (element.getAttribute().getId() == attributeID) {
                filteredElements.add(element);
            }
        }

        return filteredElements;
    }


    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
