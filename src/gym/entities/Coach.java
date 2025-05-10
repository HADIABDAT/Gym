package gym.entities;

public class Coach {
    private int id;
    private String nom;
    private String prenom;
    private String adresse;
    private String nTel;
    private String etat;

    public Coach() {
    }

    public Coach(int id, String nom, String prenom, String adresse, String nTel, String etat) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.nTel = nTel;
        this.etat = etat;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNTel() {
        return nTel;
    }

    public void setNTel(String nTel) {
        this.nTel = nTel;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
}