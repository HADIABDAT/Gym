package gym.entities;

import java.util.Date;

public class Seance {
    private int id;
    private String type;
    private String heure;
    private Date date; // Changed 'Date' to Date type
    private int coachId; // Changed 'Coach' to 'coachId' to follow Java naming conventions

    public Seance() {
    }

    public Seance(int id, String type, String heure, Date date, int coachId) {
        this.id = id;
        this.type = type;
        this.heure = heure;
        this.date = date;
        this.coachId = coachId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCoachId() {
        return coachId;
    }

    public void setCoachId(int coachId) {
        this.coachId = coachId;
    }
}