package tn.dymes.store.services;

import tn.dymes.store.entites.Element;

import java.util.List;

public interface IElementService {
    Element getElementByName(String name);
    void addElement(long attributeID, String name, String reference);
    void removeElement(long attributeID, String name, String reference);
    Element updateElement(long elementID,String name, String reference);

    boolean isExist(long attributeID, String name, String reference);

    List<Element> getElementsByAttributeID(long attributeID);
}
