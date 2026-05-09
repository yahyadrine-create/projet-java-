package model;

public class Medicament {
    private int id_med;
    private String nom;
    private int quantite;
    private String dateExpiratino;
    private double prix;
    private String categorie;
    public Medicament(int id_med, String nom, int quantite,String dateExpiratino,double prix,String categorie) {
        super();
        this.id_med = id_med;
        this.nom = nom;
        this.quantite = quantite;
        this.dateExpiratino=dateExpiratino;
        this.prix=prix;
        this.categorie=categorie;
    }
    public Medicament () {}
    public Medicament (int id_med) {
    	this.id_med=id_med;
    }
    public int getId_med() {
        return id_med;
    }
    public void setId_med(int id_med) {
        this.id_med = id_med;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public int getQuantite() {
        return quantite;
    }
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getDateExpiratino() {
        return dateExpiratino;
    }
    public void setDateExpiratino(String dateExpiratino) {
        this.dateExpiratino = dateExpiratino;
    }

    public double getPrix() {
        return prix;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getCategorie() {
        return categorie;
    }
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    @Override
    public String toString() {
        return "Medicament [id_med=" + id_med + ", nom=" + nom + ", quantite=" + quantite + ", dateExpiratino="
                + dateExpiratino + ", prix=" + prix + ", categorie=" + categorie + "]";
    }

}