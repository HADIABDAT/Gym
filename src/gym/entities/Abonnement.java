package gym.entities;

import java.util.Date;

public class Abonnement {
    private int id;
    private String type;
    private Date dateDeDebut;
    private Date dateDuFin;
    private int prix;
    private String status;

    public Abonnement() {
    }

    public Abonnement(int id, String type, Date dateDeDebut, Date dateDuFin, int prix, String status) {
        this.id = id;
        this.type = type;
        this.dateDeDebut = dateDeDebut;
        this.dateDuFin = dateDuFin;
        this.prix = prix;
        this.status = status;
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

    public Date getDateDeDebut() {
        return dateDeDebut;
    }

    public void setDateDeDebut(Date dateDeDebut) {
        this.dateDeDebut = dateDeDebut;
    }

    public Date getDateDuFin() {
        return dateDuFin;
    }

    public void setDateDuFin(Date dateDuFin) {
        this.dateDuFin = dateDuFin;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}