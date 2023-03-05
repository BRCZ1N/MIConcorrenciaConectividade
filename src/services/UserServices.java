package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import resources.Administrator;
import resources.Consumer;
import resources.Consumption;
import resources.User;

public class UserServices {

	private static Map<String, User> usersMap;
	private long id = 0;

	public UserServices() {

		usersMap = new HashMap<String, User>();

	}

	public boolean addUser(User user) {

		if (getUser(user.getName()) == null) {

			user.setId(Long.toString(id));
			usersMap.put(Long.toString(id), user);
			id += 1;
			return true;

		}

		return false;

	}

	public boolean deleteClient(String id) {

		if (getUser(id) != null) {

			usersMap.containsKey(id);
			return true;

		}

		return false;

	}

	public Map<String, Consumer> getAllClients() {

		Map<String, Consumer> mapClients = new HashMap<String, Consumer>();

		for (Entry<String, User> user : usersMap.entrySet()) {

			if (user instanceof Consumer) {

				mapClients.put(user.getKey(), (Consumer) user.getValue());

			}

		}
		return mapClients;

	}

	public Map<String, Administrator> getAllAdmins() {

		Map<String, Administrator> mapAdmins = new HashMap<String, Administrator>();

		for (Entry<String, User> user : usersMap.entrySet()) {

			if (user instanceof Administrator) {

				mapAdmins.put(user.getKey(), (Administrator) user.getValue());

			}

		}
		return mapAdmins;

	}

	private User getUser(String id) {

		for (Entry<String, User> user : usersMap.entrySet()) {

			if (user.getKey().equals(id)) {

				return user.getValue();

			}

		}

		return null;

	}

	public static boolean userAuthentication(String id, String password) {

		for (Entry<String, User> user : usersMap.entrySet()) {

			if (user.getKey().equals(id) && user.getValue().getPassword().equals(password)) {

				return true;

			}

		}

		return false;

	}

	public double getConsumptionAccumulated(String id) {

		Consumer client = (Consumer) getUser(id);

		return client.getConsumptionAccumulated();

	}

	public ArrayList<Consumption> getAllHistoryOfConsumption(String id) {

		Consumer client = (Consumer) getUser(id);

		return client.getListConsumption();

	}

//	public ArrayList<Consumption> getConsumptionRange(String id, String dataInicial, String dataFinal){
//		
//		ArrayList<Consumption> consumption;
//		
//		for(Consumption csm:getUser(id).getListConsumption()) {
//			
//			if()
//			
//		}
//		
//	}

}