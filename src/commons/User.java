package commons;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = -2926305862149034310L;
	private String name, surname, email, password, photoPath;

	
	public User(String name, String surname, String email, String photoPath) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = null;
		this.photoPath = photoPath;
	}
	
	public User(String name, String surname, String email, String password, String photoPath) {
		this(name, surname, email, photoPath);
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", surname=" + surname + ", email=" + email + ", password=" + password + ", photoPath=" + photoPath + "]";
	}
	
/*	Questo metodo serve nei seguenti metodi delle seguenti classi per evitare che il client riceva oggetti User contenenti
 * 	le password valide per accedere al sistema:
 * 		EventsRegistryJSON		getEvents()
 * 		EventJSON				getEvent()
 * 		EventUserJSON			getUser()
 * 
 * 		UsersRegistryJSON		getUsers()
 * 		UserJSON				getUser()
 */
	public User cloneWithoutPassword() {
		User user = new User(name, surname, email, photoPath);
		
		return user;
	}
}
