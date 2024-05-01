package tn.dymes.store.services;


public interface ITimerService {
    void addTimer(long idProduct, float nbHours);
    void deleteTimer(long idTimer);
}
