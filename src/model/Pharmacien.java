package model;

public class Pharmacien {
	private Utilisateur ult;
	private String poste;
	
	public Pharmacien(Utilisateur ult, String poste) {
		this.ult = ult;
		this.poste = poste;
	}
	public Pharmacien () {}

	public Utilisateur getUlt() {
		return ult;
	}

	public void setUlt(Utilisateur ult) {
		this.ult = ult;
	}

	public String getPoste() {
		return poste;
	}

	public void setPoste(String poste) {
		this.poste = poste;
	}

	@Override
	public String toString() {
		return "Pharmacien [ult=" + ult + ", poste=" + poste + "]";
	}
	
}
