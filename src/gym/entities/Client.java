package gym.entities;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String adresse;
    private String dateDeNaissance;
    private String nTel;
    private String email;
    private String motDePasse;
    private String sexe;

    public Client() {
    }

    public Client(int id, String nom, String prenom, String adresse, String dateDeNaissance, String nTel, String email, String motDePasse, String sexe) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.dateDeNaissance = dateDeNaissance;
        this.nTel = nTel;
        this.email = email;
        this.motDePasse = motDePasse;
        this.sexe = sexe;
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

    public String getDateDeNaissance() {
        return dateDeNaissance;
    }

    public void setDateDeNaissance(String dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    public String getNTel() {
        return nTel;
    }

    public void setNTel(String nTel) {
        this.nTel = nTel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }
}