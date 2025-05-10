package gym.entities;

public class Equipement {
    private int id;
    private String nom;
    private String type;
    private String marque;
    private String status;

    public Equipement() {
    }

    public Equipement(int id, String nom, String type, String marque, String status) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.marque = marque;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}