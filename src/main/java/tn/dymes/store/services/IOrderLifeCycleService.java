package tn.dymes.store.services;

import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.entites.OrderLifeCycle;

import java.io.IOException;
import java.util.List;

public interface IOrderLifeCycleService {

    void addStep(String stepName, String action, MultipartFile logo) throws IOException;

    void deleteStep(long stepID);

    void updatePosition(int oldPosition, int newPosition);

    void updateStep(long stepID, String stepName, String action, MultipartFile logo) throws IOException;

    byte[] getPublicPhoto(long idStep) throws IOException;

    void updateStatus(long stepID, boolean status);

    boolean isExist (String stepName);

    List<OrderLifeCycle> findAll();

    String uploadStepLogo(MultipartFile file) throws IOException;
}
