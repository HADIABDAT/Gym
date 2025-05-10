package gym.entities;

import java.util.Date;

public class Paiment {
    private int id;
    private int clientId; // Changed 'Client' to 'clientId' to follow Java naming conventions
    private int montant;
    private Date date; // Changed 'date' to Date type

    public Paiment() {
    }

    public Paiment(int id, int clientId, int montant, Date date) {
        this.id = id;
        this.clientId = clientId;
        this.montant = montant;
        this.date = date;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}