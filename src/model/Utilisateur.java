package model;

public class Utilisateur {
	private int cin;
	private String nom;
	private String prenom;
	private String passwd;
	private int num_tel;
	private String login;
	private String role;
	public Utilisateur(int cin, String nom, String prenom, String passwd,int num_tel,String login,String role) {
		this.cin = cin;
		this.nom = nom;
		this.prenom = prenom;
		this.passwd = passwd;
		this.num_tel=num_tel;
		this.login=login;
		this.role=role;
	}
	public Utilisateur(int cin) {
		this.cin=cin;
	}
	public int getCin() {
		return cin;
	}
	public void setCin(int cin) {
		this.cin = cin;
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
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public int getNum_tel() {
		return num_tel;
	}
	public void setNum_tel(int num_tel) {
		this.num_tel = num_tel;
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "Utilisateur [cin=" + cin + ", nom=" + nom + ", prenom=" + prenom + ", num_tel=" + num_tel + "]";
	}
	
	
}
