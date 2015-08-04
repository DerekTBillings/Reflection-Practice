package classes;

public class Welcome extends Notification {

	public void welcome(String name, StringBuffer location) {
		notify("Welcome "+name+" to "+location.reverse());
	}
	
}
