package dz.mradel.emploiinterim.models;

public class Employeur {
    private String nomEntreprise,adresse,telephone, siteweb, linkedin, facebook , email, password, logo;

    public Employeur() {
    }

    public Employeur(String nomEntreprise, String adresse, String telephone, String siteweb, String linkedin, String facebook, String email, String password, String logo) {
        this.nomEntreprise = nomEntreprise;
        this.adresse = adresse;
        this.telephone = telephone;
        this.siteweb = siteweb;
        this.linkedin = linkedin;
        this.facebook = facebook;
        this.email = email;
        this.password = password;
        this.logo=logo;
    }

    public Employeur(String nomEntreprise, String adresse, String email, String logo) {
        this.nomEntreprise = nomEntreprise;
        this.adresse = adresse;
        this.email = email;
        this.logo=logo;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
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

    public String getSiteweb() {
        return siteweb;
    }

    public void setSiteweb(String siteweb) {
        this.siteweb = siteweb;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
