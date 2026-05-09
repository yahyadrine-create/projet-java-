package model;

public class LigneOrd {
	private Ordonnance ord;
	private Medicament med;
	private int quantite;
	public LigneOrd(Ordonnance ord, Medicament med, int quantite) {
		this.ord = ord;
		this.med = med;
		this.quantite = quantite;
	}
	public Ordonnance getOrd() {
		return ord;
	}
	public void setOrd(Ordonnance ord) {
		this.ord = ord;
	}
	public Medicament getMed() {
		return med;
	}
	public void setMed(Medicament med) {
		this.med = med;
	}
	public int getQuantite() {
		return quantite;
	}
	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}
	@Override
	public String toString() {
		return "LigneOrd [ord=" + ord + ", med=" + med + ", quantite=" + quantite + "]";
	}
	
}
