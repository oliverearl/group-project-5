/**
 * 
 */
package uk.ac.aber.cs221.group5.gui;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.text.html.parser.Element;

import uk.ac.aber.cs221.group5.logic.MemberList;
import uk.ac.aber.cs221.group5.logic.Task;
import uk.ac.aber.cs221.group5.logic.TaskList;
import uk.ac.aber.cs221.group5.logic.TaskStatuses;
import uk.ac.aber.cs221.group5.logic.Database;
import uk.ac.aber.cs221.group5.logic.DbStatus;

/**
 * @author David (daf5) Provides a wrapper for common window functions such as
 *         creating and destroying the main window
 * 
 */


public class MainWindow {


	private static MainWindowGUI childWindow;


	private TaskList taskList     = new TaskList();
	//TaskList for holding Tasks that cannot be sent to the database
	private TaskList pendingList  = new TaskList();
	private MemberList memberList = new MemberList();

	private static Database databaseObj;

	private static final String DB_CONFIG_PATH = "connSaveFile.txt";
	private static final String MEMBERS_SAVE_PATH = "memberSaveFile.txt";
	private static final String TASK_SAVE_PATH = "taskSaveFile.txt";
	private static final String PENDING_SAVE_PATH = "pendingSaveFile.txt";
	
	private static long connTime; // The time when CLI last synced with the Database

	public TaskList getTaskList() {
		return this.taskList;
	}

	public void setTaskList(TaskList list) {

		this.taskList = list;
		
		try {
			saveChange(TASK_SAVE_PATH);
			childWindow.populateTable(taskList);
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	public void setmemberList(MemberList list) {
		connTime = System.currentTimeMillis();
		
		this.memberList = list;
		try {
			saveChange(TASK_SAVE_PATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws InterruptedException, NumberFormatException, IOException {
		TaskList taskList = new TaskList();
		MemberList memberList = new MemberList();

		memberList.loadMembers(MEMBERS_SAVE_PATH);

		MainWindow mainWindow = new MainWindow();
		mainWindow.attachMainWindowToDb(mainWindow);
		readConfigToDb(DB_CONFIG_PATH);
		if (!mainWindow.doesGUIExist()) {
			mainWindow.createWindow();
		}
		
		childWindow.setVisible(false);
		
		LoginWindow loginWindow = new LoginWindow();
		loginWindow.passMemberList(memberList);
		loginWindow.createWindow();
		loginWindow.setLabelGUI(databaseObj.getConnStatus());		
	}


	
	private static void readConfigToDb(String dbFile) throws IOException {
		FileReader fileReader;
		try {
			fileReader = new FileReader(dbFile);
			BufferedReader read = new BufferedReader(fileReader);

			String dbName;
			String dbUsername;
			String dbPassword;
			String url;
			String dbPort;

			dbName = read.readLine();
			dbUsername = read.readLine();
			dbPassword = read.readLine();
			url = read.readLine();
			dbPort = read.readLine();

			read.close();
			
			callConnectOnDb(url, dbPort, dbUsername, dbPassword, dbName);
          
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
     
	public static void callConnectOnDb(String url, String dbPort, String dbUsername, String dbPassword, String dbName) {
		databaseObj.connect(url, dbPort, dbUsername, dbPassword, dbName);
	}
	
	

	private boolean doesGUIExist() {
		for (Frame frame : Frame.getFrames()) {
			if (frame.getTitle().equals("Main Window")) {
				return true;   
			}
		}
		return false;
	}

	public void setConnStatus(DbStatus connStatus) {
		if (childWindow!= null){
			MainWindow.childWindow.setConnStatusLabel(connStatus);
		}
	}
	
	public void setAutoTimer(boolean timerState){
		if(timerState){
			databaseObj.startAutoSync();
		}
		else{
			databaseObj.stopAutoSync();
		}
		
	}

	public static DbStatus getConnStatus() {
		return databaseObj.getConnStatus();
	}

	public static long getConnTime() {
		return MainWindow.connTime;
	}

	public MainWindow() {
		// Setup common window features
		super();

	}
	
	public void attachMainWindowToDb(MainWindow main){
		if (databaseObj != null) {
			databaseObj.setHostWindow(main);
		} else {
			databaseObj = new Database(MEMBERS_SAVE_PATH, this);
		}
		
	}

	
	public void createWindow() {
		// Get a new child window for super class
		childWindow = new MainWindowGUI(this);

		// Update local files with Task files from TaskerSRV if we are connected
		try {
			if (databaseObj.getConnStatus() == DbStatus.CONNECTED) {
				saveChange(TASK_SAVE_PATH);
				loadTasks(TASK_SAVE_PATH);
				MainWindow.childWindow.populateTable(this.taskList);
			} else {
				// If disconnected load then save
				loadTasks(TASK_SAVE_PATH);
				MainWindow.childWindow.populateTable(this.taskList);
			}
		} catch (FileNotFoundException e) {
			this.displayWarning("Tasks not found locally, you need to connect to database for tasks");
			blankFile(TASK_SAVE_PATH);
		} catch (Exception e1) {
			this.displayError("Task file empty - Please connect to database for tasks", "Task File empty");
			blankFile(TASK_SAVE_PATH);
		}

		// Load the members into the Member List
		try {
			memberList.loadMembers(MEMBERS_SAVE_PATH);
		} catch (NumberFormatException | IOException e) {
			this.displayError("Error loading member list, you need to connect to the database",
					"Error Loading members");
			blankFile(MEMBERS_SAVE_PATH);
		}

		// Ask parent to setup window for us and pass
		// this class's methods for it to work on
		//setupWindowLaunch(this);

		childWindow.setConnStatusLabel(databaseObj.getConnStatus());
	}

	public void destroyWindow() {
		databaseObj.closeDbConn();

	}
	
	public void updateTask(Task updatedTask){
		databaseObj.updateDbTask(updatedTask);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.aber.cs221.group5.logic.WindowInterface#setTitleText(java.lang.
	 * String)
	 */
	
	
		// TODO Auto-generated method stub

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.aber.cs221.group5.logic.WindowInterface#displayError(java.lang.
	 * String)
	 */
	
	public void displayError(String errorText, String errorType) {
		JOptionPane.showMessageDialog(null, errorText, errorType, JOptionPane.ERROR_MESSAGE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.aber.cs221.group5.logic.WindowInterface#displayWarning(java.lang.
	 * String)
	 */
	
	public void displayWarning(String warnText) {
		JOptionPane.showMessageDialog(null, warnText, "Warning", JOptionPane.WARNING_MESSAGE);

	}

	/*
	 * public TaskList getTaskList(){ return this.taskList; //TODO check if
	 * table is displaying }
	 * 
	 * public void setTaskList (TaskList list) { this.taskList = list; }
	 * 
	 * public MemberList getMemberList(){ return this.memberList; } public void
	 * setMemberList (MemberList list){ this.memberList = list;
	 * 
	 * }
	 */

	public void loadTasks(String filename) throws Exception {
		FileReader fileReader = new FileReader(filename);
		BufferedReader read = new BufferedReader(fileReader);
		ArrayList<String> extractedElementIds = new ArrayList<String>();
		// New Task List to prevent loading the same Tasks multiple times.
		TaskList newList = new TaskList();
		int numOfTasks = 0;
		String taskID = null;
		String elementID = null;
		String elements = null;
		String taskName = null;
		TaskStatuses taskStatus = null;
		String assigned = null;
		String startDate = null;
		String endDate = null;

		try {
			// First read in the number of tasks
			numOfTasks = Integer.parseInt(read.readLine());
			// Load data and create Task objects
			for (int loopCount = 0; loopCount < numOfTasks; loopCount++) {
				int elementNum = 0;
				taskID = read.readLine();
				elementID = read.readLine();
				extractedElementIds = getElementIndexes(elementID);
				elements = read.readLine();
				taskName = read.readLine();
				taskStatus = TaskStatuses.valueOf(TaskStatuses.class, read.readLine());
				assigned = read.readLine();
				startDate = read.readLine();
				endDate = read.readLine();
				Task task = new Task(taskID, taskName, startDate, endDate, assigned, taskStatus);
				if (!elements.equals(",|")) {
					String elementPair[] = { "", "" };
					while (elementPair != null) {
						if(elements.length() > 2){	//Evaluates true if the element has something in it other than the seperator characters
							if(elements.charAt(0) == '|'){
								elements = elements.substring(1);	//Remove the element seperator character from the begining of the elements
							}
							elementPair[0] = elements.substring(0, elements.indexOf(","));
							elementPair[1] = elements.substring(elements.indexOf(",") + 1, elements.indexOf("|")); //Exception on this line
							task.addElement(elementPair[0], elementPair[1], extractedElementIds.get(elementNum));
							elementNum++;
							elements = removePair(elements);
						}
						else{
							newList.addTask(task);
							elementPair = null;
						}
					}
				} else{
					newList.addTask(task);
				}
				
			}
			read.close();
			this.taskList = newList;
		} catch (Exception e) {
			read.close();
			e.printStackTrace();
			throw e;
		}
	}

	public void saveChange(String filename) throws IOException {
		ArrayList<uk.ac.aber.cs221.group5.logic.Task.Element> elements;
		Task writeTask;
		FileWriter fileWriter = new FileWriter(filename);
		BufferedWriter write = new BufferedWriter(fileWriter);
		int numOfTasks = this.taskList.getListSize();
		int numOfElements = 0; // The number of Elements in a single Task
		write.write(numOfTasks + "\n");
		for (int loopCount = 0; loopCount < numOfTasks; loopCount++) {
			writeTask = taskList.getTask(loopCount);
			elements = writeTask.getAllElements();
			write.write(writeTask.getID() + "\n");
			// Elements
			numOfElements = writeTask.getNumElements();
			elements = writeTask.getAllElements();
			if (numOfElements == 0) {
				write.write("0\n");
				write.write(",|");
			} else {
				for (int idCount = 0; idCount < writeTask.getNumElements(); idCount++){
					write.write(writeTask.getElement(idCount).getIndex()+",");
				}
				write.write("\n");
				for (int i = 0; i < writeTask.getNumElements(); i++) {
					uk.ac.aber.cs221.group5.logic.Task.Element writeElement = elements.get(i);
					write.write(writeElement.getName()+","+writeElement.getComment()+"|");
				}
			}
			write.write("\n");
			write.write(writeTask.getName() + "\n");
			write.write(writeTask.getStatus() + "\n");
			write.write(writeTask.getMembers() + "\n");
			write.write(writeTask.getStart() + "\n");
			write.write(writeTask.getEnd() + "\n");
		}
		write.close();
		fileWriter.close();
	}
	
	public void writePendingTask(Task pendingTask) throws IOException{
		FileWriter fileWriter = new FileWriter(PENDING_SAVE_PATH, true);
		BufferedWriter write = new BufferedWriter(fileWriter);
		write.write(pendingTask.getID());
		int numOfElements = pendingTask.getNumElements();
		ArrayList<uk.ac.aber.cs221.group5.logic.Task.Element> elements = pendingTask.getAllElements();
		if (numOfElements == 0) {
			write.write("0\n");
			write.write(",|");
		} else {
			for (int idCount = 0; idCount < pendingTask.getNumElements(); idCount++){
				write.write(pendingTask.getElement(idCount).getIndex()+",");
			}
			write.write("\n");
			for (int i = 0; i < pendingTask.getNumElements(); i++) {
				uk.ac.aber.cs221.group5.logic.Task.Element writeElement = elements.get(i);
				write.write(writeElement.getName()+","+writeElement.getComment()+"|");
			}
		}
		write.write("\n");
		write.write(pendingTask.getName() + "\n");
		write.write(pendingTask.getStatus() + "\n");
		write.write(pendingTask.getMembers() + "\n");
		write.write(pendingTask.getStart() + "\n");
		write.write(pendingTask.getEnd() + "\n");
	}

	public void updateLocalFiles(String taskFile) {
		databaseObj.getTasks("");
	}

	//// Methods to deal with loading Task Elements

	public ArrayList<String[]> getElements(int tableIndex) {
		ArrayList<String[]> elementPairs;

		Task selectedTask = taskList.getTask(tableIndex);

		elementPairs = selectedTask.getAllElementPairs();
		if (elementPairs.size() == 0) {
			final String[] blankComment = { "No Elements", "No Comments" };
			elementPairs.add(blankComment);
		}

		return elementPairs;
	}

	private void blankFile(String filePath) {

		try {
			FileWriter blankFile = new FileWriter(filePath, false);
			blankFile.append("");
		} catch (IOException e) {
			displayError("Cannot blank file " + filePath, "Error blanking file");
			System.exit(200);
		}

	}

	public ArrayList<String[]> getElementsLocal(String filename, int tableIndex) throws IOException {
		ArrayList<String[]> elements = new ArrayList<String[]>();
		int elementLine = (7 * tableIndex) + 1; // Finds the line in the file
												// where the element(s) for the
												// selected task
												// are located
		FileReader fileReader = new FileReader(filename);
		BufferedReader reader = new BufferedReader(fileReader);
		String taskElements = new String();
		String[] elementPair = { "", "" }; // A single element name and comment
											// pair

		// Skip over the lines in the file that we don't need
		for (int lineCount = 0; lineCount <= elementLine; lineCount++) {
			reader.readLine();
		}
		taskElements = reader.readLine();
		reader.close();
		fileReader.close();

		elementPair = seperateElement(taskElements);
		if (elementPair != null) {
			while (elementPair != null) {
				elements.add(elementPair);
				taskElements = removePair(taskElements);
				elementPair = seperateElement(taskElements);
			}

		} else {
			String[] noElements = { "No Elements", "No Comments" };
			elements.add(noElements);
			return elements;
		}

		return elements;
	}

	private String[] seperateElement(String fileLine) {
		String elementName = new String();
		String elementComment = new String();
		String[] elementPair = { "", "" };
		int split; // The index of the character that seperates the element name
					// and the element comment
		int elementEnd; // The index of the character that indicates the end of
						// the element in the file

		// True if there are no elements for the Task
		if (fileLine.indexOf(',') == 0) {
			return null;
		}

		split = fileLine.indexOf(',');
		elementEnd = fileLine.indexOf('|');
		elementName = fileLine.substring(0, split);
		elementPair[0] = elementName;
		elementPair[1] = fileLine.substring(split + 1, elementEnd); // split+1
																	// so the
																	// seperator
																	// is not
																	// included
																	// in the
																	// comment

		return elementPair;

	}

	// Once a pair is loaded from the file, it is removed from the line so the
	// next to be loaded always starts
	// at position 0
	private String removePair(String fileLine) {
		char[] fileLineChar;
		fileLineChar = fileLine.toCharArray();
		String trimmed;

		// This evaluates True if there is only one element left in the line
		if (fileLine.charAt(0) == ',') {
			// Signifies there are no more elements. Stops the seperateElement
			// method from trying to seperate an element
			// that does not exist
			fileLine = ",|";
			trimmed = fileLine;
		} else {
			for (int charCount = 0; charCount < fileLine.indexOf('|'); charCount++) {
				fileLineChar[charCount] = ' ';
			}
			fileLine = String.copyValueOf(fileLineChar);
			trimmed = fileLine.trim();
		}

		return trimmed;
	}

	// Returns a single Task's elements without any editing
	private ArrayList<String> getUneditedElements(String filename) throws NumberFormatException, IOException {
		ArrayList<String> elements = new ArrayList<String>();
		FileReader fileReader = new FileReader(filename);
		BufferedReader read = new BufferedReader(fileReader);
		int numTasks = Integer.parseInt(read.readLine());
		// Skip to the first Tasks's element(s)
		read.readLine();
		// read.readLine();
		for (int elementCount = 0; elementCount < numTasks; elementCount++) {
			elements.add(read.readLine());
			// Skip over the rest of the Task data - we don't care about that
			// here
			for (int i = 0; i < 6; i++) {
				read.readLine();
			}
		}
		read.close();
		fileReader.close();
		return elements;
	}

	// Gets the System time from the Database of when it was last connected

	
////Methods for dealing with Task Element IDs
	private ArrayList<String> getElementIndexes(String indexes){
		ArrayList<String> extractedIndexes = new ArrayList<String>();
		
		for(int i = 0; i < indexes.length(); i=i+2){
			//This line takes the i'th Element ID from the line, effectively removing the seperators
			extractedIndexes.add(indexes.substring(i, i+1));
		}
		
		return extractedIndexes;
	}
}
