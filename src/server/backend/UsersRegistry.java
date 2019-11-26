package server.backend;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.TreeMap;

import commons.InvalidUserEmailException;
import commons.User;

public class UsersRegistry {
	private TreeMap<String, User> users;

	public UsersRegistry() {
		users = new TreeMap<String, User>();
	}

	public int size() {
		return users.size();
	}

	public User get(String email) throws InvalidUserEmailException {
		User user = users.get(email);

		if (user != null)
			return user;

		throw new InvalidUserEmailException("Invalid user email: " + email);
	}

	public String[] emails() {
		return users.keySet().toArray(new String[users.keySet().size()]);
	}

	public void add(User user) throws InvalidUserEmailException {
		if (users.containsKey(user.getEmail())) 
			throw new InvalidUserEmailException("Duplicated user email: " + user.getEmail());
		
		users.put(user.getEmail(), user);
	}

	public void update(User newUser) throws InvalidUserEmailException {
		if (!users.containsKey(newUser.getEmail())) 
			throw new InvalidUserEmailException("Invalid user email: " + newUser.getEmail());
		
		User oldUser = users.get(newUser.getEmail());
		oldUser.setName(newUser.getName());
		oldUser.setSurname(newUser.getSurname());
		oldUser.setPassword(newUser.getPassword());
	}

	public void remove(String email) throws InvalidUserEmailException {
		if (!users.containsKey(email)) 
			throw new InvalidUserEmailException("Invalid user email: " + email);
		
		users.remove(email);			
	}

	public void save(String fileName) throws IOException {		
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		
		out.writeObject(users);
		
		out.close();
		fos.close();
	}

	@SuppressWarnings("unchecked")
	public void load(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(fileName);
		ObjectInputStream in = new ObjectInputStream(fis);
		
		users = (TreeMap<String, User>) in.readObject();
		
		in.close();
		fis.close();
	}
}
