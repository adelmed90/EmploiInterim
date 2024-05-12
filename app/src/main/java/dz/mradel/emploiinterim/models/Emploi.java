package dz.mradel.emploiinterim.models;

public class Emploi {

    private String dataTitle;
    private String dataDesc;
    private Employeur employeur;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public Employeur getEmployeur() {
        return employeur;
    }

    public void setEmployeur(Employeur employeur) {
        this.employeur = employeur;
    }

    public Emploi(String dataTitle, String dataDesc, Employeur employeur) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.employeur=employeur;
    }
    public Emploi(){

    }
}