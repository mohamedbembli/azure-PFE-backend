package tn.dymes.store.services;

import tn.dymes.store.entites.Attribute;

import java.util.List;

public interface IAttributeService {
    void addAttribute(String name,String type);
    Attribute updateAttribute(long id, String name, String type);
    void removeAttribute(long id);

    List<Attribute> getAll();

    int getNbElementsByID(long id);
}
