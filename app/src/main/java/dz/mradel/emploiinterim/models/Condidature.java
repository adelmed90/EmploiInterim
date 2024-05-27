package dz.mradel.emploiinterim.models;

import java.io.Serializable;

public class Condidature implements Serializable {
    private Emploi emploi;
    private Demandeur demandeur;
    private boolean enCours;
    private boolean acceptByEmployeur;
    private boolean acceptByDemandeur;
    private boolean refuseByEmployeur;
    private boolean cancelByDemandeur;

    public Condidature() {
    }

    public Condidature(Emploi emploi, Demandeur demandeur, boolean enCours, boolean acceptByEmployeur, boolean acceptByDemandeur, boolean refuseByEmployeur, boolean cancelByDemandeur) {
        this.emploi = emploi;
        this.demandeur = demandeur;
        this.enCours = enCours;
        this.acceptByEmployeur = acceptByEmployeur;
        this.acceptByDemandeur = acceptByDemandeur;
        this.refuseByEmployeur = refuseByEmployeur;
        this.cancelByDemandeur = cancelByDemandeur;
    }

    public Emploi getEmploi() {
        return emploi;
    }

    public void setEmploi(Emploi emploi) {
        this.emploi = emploi;
    }

    public Demandeur getDemandeur() {
        return demandeur;
    }

    public void setDemandeur(Demandeur demandeur) {
        this.demandeur = demandeur;
    }

    public boolean isEnCours() {
        return enCours;
    }

    public void setEnCours(boolean enCours) {
        this.enCours = enCours;
    }

    public boolean isAcceptByEmployeur() {
        return acceptByEmployeur;
    }

    public void setAcceptByEmployeur(boolean acceptByEmployeur) {
        this.acceptByEmployeur = acceptByEmployeur;
    }

    public boolean isAcceptByDemandeur() {
        return acceptByDemandeur;
    }

    public void setAcceptByDemandeur(boolean acceptByDemandeur) {
        this.acceptByDemandeur = acceptByDemandeur;
    }

    public boolean isRefuseByEmployeur() {
        return refuseByEmployeur;
    }

    public void setRefuseByEmployeur(boolean refuseByEmployeur) {
        this.refuseByEmployeur = refuseByEmployeur;
    }

    public boolean isCancelByDemandeur() {
        return cancelByDemandeur;
    }

    public void setCancelByDemandeur(boolean cancelByDemandeur) {
        this.cancelByDemandeur = cancelByDemandeur;
    }
}
