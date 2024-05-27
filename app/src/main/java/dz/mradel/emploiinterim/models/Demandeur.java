package dz.mradel.emploiinterim.models;

import java.io.Serializable;

public class Demandeur implements Serializable {

    private String nomPrenom;
    private String email;
    private String password;
    private String adresse;
    private String telephone;
    private String nationalite;
    private String dateDeNaissance;
    private String commentaire;
    private String imageURL;
    private String pdfURL;

    public Demandeur() {
    }

    public Demandeur(String nomPrenom, String email, String password, String adresse, String telephone, String nationalite, String dateDeNaissance, String commentaire, String imageURL, String pdfURL) {
        this.nomPrenom = nomPrenom;
        this.email = email;
        this.password = password;
        this.adresse = adresse;
        this.telephone = telephone;
        this.nationalite = nationalite;
        this.dateDeNaissance = dateDeNaissance;
        this.commentaire = commentaire;
        this.imageURL = imageURL;
        this.pdfURL = pdfURL;
    }

    public String getNomPrenom() {
        return nomPrenom;
    }

    public void setNomPrenom(String nomPrenom) {
        this.nomPrenom = nomPrenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public String getDateDeNaissance() {
        return dateDeNaissance;
    }

    public void setDateDeNaissance(String dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPdfURL() {
        return pdfURL;
    }

    public void setPdfURL(String pdfURL) {
        this.pdfURL = pdfURL;
    }
}
