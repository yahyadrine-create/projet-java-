package model;

public class Ordonnance {
	private Client clt;
	private int num_ord;
	private String date;
	public Ordonnance(Client clt,int num_ord,String date) {
		this.clt = clt;
		this.num_ord = num_ord;
		this.date=date;
	}
	public Ordonnance (int num_ord) {
		this.num_ord=num_ord;
	}
	public Ordonnance() {}
	public Client getClt() {
		return clt;
	}
	public void setClt(Client clt) {
		this.clt = clt;
	}
	
	public int getNum_ord() {
		return num_ord;
	}
	public void setNum_ord(int num_ord) {
		this.num_ord = num_ord;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "Ordonnance [clt=" + clt + ", num_ord=" + num_ord + ", date=" + date + "]";
	}
	
	
}
