package dz.mradel.emploiinterim.models;

import java.io.Serializable;
import java.util.UUID;

public class Emploi implements Serializable {

    private String jobTitle;
    private String jobDesc;
    private Employeur employeur;
    private String key;

    public String getKey() {
        return key;
    }

    /*public void setKey(String key) {
        this.key = key;
    }*/

    public String getJobTitle() {
        return jobTitle;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public Employeur getEmployeur() {
        return employeur;
    }

    public void setEmployeur(Employeur employeur) {
        this.employeur = employeur;
    }

    public Emploi(String jobTitle, String jobDesc, Employeur employeur) {
        this.key= UUID.randomUUID().toString();
        this.jobTitle = jobTitle;
        this.jobDesc = jobDesc;
        this.employeur=employeur;
    }
    public Emploi(){

    }
}