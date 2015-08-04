package main;

import java.lang.reflect.Constructor;

import java.lang.reflect.Method;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 * 
 * @author BillingsD
 * @Date 07 July 2015
 */
public class Main extends Application {
	
	public static void main(String[] args) {
		launch(args); //calls JavaFX Application's launch
	}
	
	//declare FX objects
	private TextField classInput;
	private TextField methodInput;
	private Button submit;
	private Button view;
	private TextArea result;
	private Label notify;
	private Label classLabel;
	private Label methodLabel;
	
	//Global objects
	private Class selectedClass;
	private Object instantiation;
	
	//final strings that are used to grab the object type out of strings
	private final String TYPEFIRSTCHAR = "(";
	private final String TYPELASTCHAR = ")";
	private final String SEPARATOR = ":";
	
	
	//This is a class that is required by JavaFX's Application
	public void start(Stage window) {
		
		//Instantiate the nodes on application startup
		VBox box = new VBox(10);
		HBox btnBox = new HBox(10);
		HBox classBox = new HBox(10);
		HBox methodBox = new HBox(10);
		classInput = new TextField();
		methodInput = new TextField();
		submit = new Button();
		view = new Button();
		result = new TextArea();
		notify = new Label();
		classLabel = new Label();
		methodLabel = new Label();

		//Initial settings that aren't changed elsewhere in the application
		result.setEditable(false);
		submit.setText("Call method");
		classLabel.setText("Class name:");
		methodLabel.setText("Method name:");
		//The following is a lambda expression with a simple safety check
		submit.setOnAction(e -> {
			if (instantiation == null) //instantiation is an instantiated object at the user's request
				notify("You need to view a Class first");
			else
				executeMethod();
		});
		
		//Initial settings that need to regularly reset
		reset();
		
		//Adding child nodes to their respective containers
		classBox.getChildren().addAll(classLabel, classInput);
		methodBox.getChildren().addAll(methodLabel, methodInput);
		btnBox.getChildren().addAll(view, submit);
		box.getChildren().addAll(notify, classBox, methodBox, btnBox, result);
		
		//Setting pane alignments
		box.setAlignment(Pos.CENTER);
		btnBox.setAlignment(Pos.CENTER);
		classBox.setAlignment(Pos.CENTER);
		methodBox.setAlignment(Pos.CENTER);
		
		//padding
		box.setPadding(new Insets(10, 10, 10, 10));
		
		//display new window
		window.setScene(new Scene(box));
		window.show();
	}
	
	
	/**
	 *  Sets the global class equal to the class requested and 
	 *  creates an instantiated version of the class.
	 */
	private void setClassInfo() {
		try {
			//Splits the class input based on the provided separator
			String[] args = trimList(classInput.getText().split(SEPARATOR));
			
			if (args.length == 0) {
				notify("You didn't submit anything");
			} else {
				selectedClass = Class.forName("classes."+args[0]);
				
				//Pulls a list of Objects and Object Classes required for Class Constructor
				Class[] cList = getCList(args);
				Object[] oList = getOList(args);
				
				if (cList == null || oList == null) 
					return;
				
				//Instantiate the requested Object
				Constructor constructs = selectedClass.getConstructor(cList);
				instantiation = constructs.newInstance(oList);
				
				//Pull and provide a list of all Methods available for the provided Object
				StringBuffer methodList = new StringBuffer();
				for (Method m :selectedClass.getMethods()) {
					if (methodList.length() > 0) 
						methodList.append("\r\n");
					methodList.append(m.toString());
				}
				result.setText(methodList.toString());
				
				//Prepare program for the next step
				notify("Enter method name and parameters seperated with a '"+SEPARATOR+"'.\r\n"+
						"To cast, enter type prior to value in the format (type) value.\r\n"+
						"Example: MethodName : (Integer) 2");
				methodInput.setDisable(false);
				submit.setDisable(false);
				classInput.setDisable(true);
				methodInput.requestFocus();
				view.setText("Change Class");
				view.setOnAction(e -> reset());
			}
		} catch (ClassNotFoundException e) {
			//reset the functionality and tell the user what happened
			reset(); 
			notify("Class not found, try again.");
		} catch (Exception e) {
			notify("Oops, try creating your Object again");
		}
	}
	
	
	/**
	 * Executes a Method associated with the established Object
	 */
	private void executeMethod() {
		//Gets a list of inputs from the user
		String[] list = trimList(methodInput.getText().split(SEPARATOR));
		
		if (list.length == 0) {
			notify("you didn't insert anything");
		} else {
			
			try {
				//Get a list of Objects and Object Classes needed for the requested Method
				Class[] cList = getCList(list);
				Object[] oList = getOList(list);
				
				//Invoke the requested Method
				String methodName = list[0];
				Method method = selectedClass.getMethod(methodName, cList);
				method.invoke(instantiation, oList);
				
				//provide a success message to the user
				notify("Success. Feel free to call a new Method or change your Class.");
			}catch(Exception e) {
				notify("Something went wrong calling your method, try again.");
			}
		}
	}
	
	
	/**
	 * This method returns a list of java.lang Objects
	 * @param String[]
	 * @return Object[]
	 */
	private Object[] getOList(String[] args) {
		//Establis oList with size of args-1 because args[0] is the Method or Class name used elsewhere
		Object[] oList = new Object[args.length-1];
		
		for (int i=1; i<args.length; i++) {
			int x = i-1; //Honestly, didn't want to type i-1 everywhere
			if (containsType(args[i])) { //String Objects don't need to be cast to anything
				
				String type = getType(args[i]); //Self explanatory, pulls Class type from text
				String text = getText(args[i]); //Pulls String value from text 
				
				try {					
					oList[x] = Class.forName("java.lang."+type) //Grabs the casting Object's Class
							.getConstructor(java.lang.String.class) //Grabs Constructor. Must accept String.
							.newInstance(text); //Instantiate Class with a value converted from a String
				} catch (Exception e) {
					notify("Something went wrong when casting your object");
					return null;
				} 
				
			} else { //String Objects don't need to be cast
				oList[x] = args[i];
			}
		}
		return oList;
			
	}
	
	
	/**
	 * Returns a list of Classes. Classes are based on text found in-between the TypeChars
	 * @param String[]
	 * @return Class[]
	 */
	private Class[] getCList(String[] args) {
		//Instantiate a cList[] at args length-1 because the value of args[0] is the Method or Class Name
		Class[] cList = new Class[args.length-1];
		
		for (int i=1; i<args.length; i++) {
			int x = i-1; //So that I don't need to write i-1 everywhere
			
			if (containsType(args[i])) {//Checks to see if there is a provided casting class
				String type = getType(args[i]); //Retrieves the Class name from the provided text
				
				try {
					cList[x] = Class.forName("java.lang."+type); //Attempts to add the provided Class to the list
				} catch(ClassNotFoundException e) {
					notify("Oops, something went wrong creating your Class");
					return null;
				}
			} else { //Strings don't need to be cast by the user
				cList[x] = java.lang.String.class;
			}
		}
		return cList;
	}
	
	
	/**
	 * Calls .trim() on each element in the provided String[]
	 * @param String[]
	 * @return String[]
	 */
	private String[] trimList(String[] list) {
		for (int i=0; i<list.length; i++) {
			list[i] = list[i].trim();
		}
		return list;
	}
	
	
	/**
	 * Returns a String that has been substringed to pull the Class type from the list 
	 * @param String
	 * @return String
	 */
	private String getType(String arg) {
		return arg.substring(arg.indexOf(TYPEFIRSTCHAR)+1, arg.indexOf(TYPELASTCHAR)).trim();
	}
	
	
	/**
	 * Return a String containing the value of the new Object
	 * @param String
	 * @return String
	 */
	private String getText(String arg) {
		return arg.substring(arg.indexOf(TYPELASTCHAR)+1).trim();
	}
	
	
	/**
	 * This method exists because I grew tired of writing .setText everywhere
	 * @param String
	 */
	private void notify(String arg) {
		notify.setText(arg);
	}
	
	
	/**
	 * Returns a true if the provided String contains the required TypeChars
	 * @param String
	 * @return boolean
	 */
	private boolean containsType(String arg) {
		return (arg.contains(TYPEFIRSTCHAR) && arg.contains(TYPELASTCHAR));
	}
	
	
	/**
	 * This method resets the program back to it's original state
	 */
	private void reset() {
		notify("Enter Class and constructor args seperated with a '"+SEPARATOR+"'.\r\n"+
				"To cast, enter type prior to value in the format (type) value.\r\n"+
				"Example: TestClass : (Integer) 2");
		view.setText("View Class");
		result.clear();
		classInput.clear();
		methodInput.clear();
		methodInput.setDisable(true);
		classInput.setDisable(false);
		submit.setDisable(true);
		view.setOnAction(e -> setClassInfo());
	}
	
	
}


















