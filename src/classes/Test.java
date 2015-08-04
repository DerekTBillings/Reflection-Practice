package classes;

public class Test extends Notification {

	public Test() {}
	
	public Test(Integer i) {
		notify(i);
	}
	
	public Test(Integer i, StringBuffer sb) {
		sb.reverse();
		notify("your text reversed is "+sb+"\r\n your number is "+i);
	}
	
	public void yell(String text) {
		notify(text);
	}
	
	public void yell() {
		notify("This is the default yell method");
	}
}
