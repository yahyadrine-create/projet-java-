package model;

public class Administrateur {
	private Utilisateur ult;

	public Administrateur(Utilisateur ult) {
		super();
		this.ult = ult;
	}
	public Administrateur() {}
	public Utilisateur getUlt() {
		return ult;
	}

	public void setUlt(Utilisateur ult) {
		this.ult = ult;
	}

	@Override
	public String toString() {
		return "Administrateur [ult=" + ult + "]";
	}
	

}
