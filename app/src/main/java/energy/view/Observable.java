package energy.view;

public interface Observable {
    void notifyObservers();
    void addObserver(Observer observer);
}
