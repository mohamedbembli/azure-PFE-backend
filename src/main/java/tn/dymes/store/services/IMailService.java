package tn.dymes.store.services;

public interface IMailService {
    void sendEmail(String emailDestination, String subject, String content);
}
