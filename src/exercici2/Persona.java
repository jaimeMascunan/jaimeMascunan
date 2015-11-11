package exercici2;

public class Persona {
	private String nom;
	
	public Persona (String nouNom){
		this.nom= nouNom;
	}
	
	public String getNom(){
		return nom;
	}
	
	public String setNom(String nomSet){
		nom = nomSet;
		return nom;
	}

}
