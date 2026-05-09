package model;

public class Client {
	private int id_clt;
	private String nom;
	private String prenom;
	private int age;
	private double credit;
	private int num_tel;
	
	public Client(int id_clt, String nom, String prenom, int age, double credit, int l) {
		this.id_clt = id_clt;
		this.nom = nom;
		this.prenom = prenom;
		this.age = age;
		this.credit = credit;
		this.num_tel = l;
	}
	public Client(int id_clt) {
		this.id_clt=id_clt;
	}
	public int getId_clt() {
		return id_clt;
	}
	public void setId_clt(int id_clt) {
		this.id_clt = id_clt;
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
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	
	public int getNum_tel() {
		return num_tel;
	}
	public void setNum_tel(int num_tel) {
		this.num_tel = num_tel;
	}
	@Override
	public String toString() {
		return "Client [id_clt=" + id_clt + ", nom=" + nom + ", prenom=" + prenom + ", age=" + age + ", credit="
				+ credit + ", num_tel=" + num_tel + "]";
	}
	
	
	
}
