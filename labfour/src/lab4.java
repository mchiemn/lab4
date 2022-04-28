import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class lab4 {
	public static void main(String[] args) throws Exception{
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee", "lab4", "Kimo161513?");
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
		//Scanner keyboard = new Scanner(System.in);
		boolean done = false; // Boolean to stop menu
		while(!done) {
			System.out.println("Choose an action to be performed:\n" +
								"1: Display schedule of trip\n" +
								"2: Edit trip offerings\n"+
								"3: Display stops\n"+
								"4: Display weekley schedule of driver\n"+
								"5: Add a drive\n"+
								"6: Add a bus\n"+
								"7: Delete a bus\n"+
								"8: Record actual data of given trip offering");
			int input = Integer.parseInt(keyboard.readLine());
			switch(input) {
			case 1:
				displaySchedule(keyboard, con);
				break;
			case 2:
				editSchedule(keyboard, con);
				break;
			case 3:
				displayStop(keyboard, con);
				break;
			case 4:
				displayWeeklySchedule(keyboard, con);
				break;
			}
			System.out.println("Finished? (Y/N)");
			if(keyboard.readLine().equalsIgnoreCase("Y")) {
				done = true;
			}
		}
		
		// Example from notes
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM Persons WHERE city = \"Los Angeles\""); 
		rs.next();
		
		String LastName = rs.getString("LastName"); 
		String FirstName = rs.getString("FirstName"); 
		String City = rs.getString("City"); 
		         
		System.out.println(FirstName + " " + LastName + " " + City); 
		
		//Close the statement and result set when complete
		st.close(); 
		con.close();
	}
	
	static void displaySchedule(BufferedReader keyboard, Connection con) throws Exception {
		System.out.print("Provide start location name:");
		String startLocationName = keyboard.readLine();
		System.out.print("\nProvide destination name:");
		String destinationName = keyboard.readLine();
		System.out.print("\nProvide date (YYYY-MM-DD):");
		String date = keyboard.readLine();
		
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT * "
				+ "FROM tripoffering A, trip B "
				+ "WHERE A.TripNumber = B.TripNumber "
				+ "AND B.StartLocationName = \"" + startLocationName + "\" "
				+ "AND B.DestinationName = \"" + destinationName + "\" "
				+ "AND A.Date = \"" + date + "\" ");
		StringBuilder build = new StringBuilder();
		while(rs.next()) {
			build.append(rs.getString("StartLocationName"));
			build.append(" ");
			build.append(rs.getString("DestinationName"));
			build.append(" ");
			build.append(rs.getString("Date"));
			build.append(" ");
			build.append(rs.getString("ScheduledStartTime"));
			build.append(" ");
			build.append(rs.getString("ScheduledArrivalTime"));
			build.append(" ");
			build.append(rs.getString("DriverName"));
			build.append(" ");
			build.append(rs.getString("BusID"));
			build.append("\n");
		}
		String output = build.toString();
		if(output.isEmpty()) {
			System.out.println("There is no data!");
		}
		System.out.println(output);
	}
	
	static void editSchedule(BufferedReader keyboard, Connection con) throws Exception{
		System.out.println("How would you like to edit the trip offering?\n"
							+ "1.Delete trip 2.Add trip"
							+ "3.Change driver 4.Change bus");
		int input = Integer.parseInt(keyboard.readLine());
		System.out.print("Provide trip number: ");
		int tripNum = Integer.parseInt(keyboard.readLine());
		System.out.print("\nProvide date (YYYY-MM-DD): ");
		String date = keyboard.readLine();
		System.out.print("\nProvide scheduled start time: ");
		String startTime = keyboard.readLine();
		switch(input) {
		case 1:
			deleteTrip(keyboard, con, tripNum, date, startTime);
			break;
		case 2:
			addTrip(keyboard, con, tripNum, date, startTime);
			break;
		case 3:
			changeDriver(keyboard, con, tripNum, date, startTime);
			break;
		case 4:
			changeBus(keyboard, con, tripNum, date, startTime);
			break;
		}
	}
	
	// User wants to edit tripoffering by deleting
	static void deleteTrip(BufferedReader keyboard, Connection con, int tripNum, String date, String startTime) throws Exception{
		Statement st = con.createStatement();
		int rows = st.executeUpdate("DELETE "
				+ "FROM tripoffering A "
				+ "WHERE A.TripNumber = " + tripNum
				+ " AND A.Date = \"" + date + "\" "
				+ "AND A.ScheduledStartTime = \"" + startTime + "\" ");
		if(rows > 0)
			System.out.println("Successfully deleted");
		else
			System.out.println("Data could not be found!");
	}
	
	// User wants to edit tripoffering by adding trips
	static void addTrip(BufferedReader keyboard, Connection con, int tripNum, String date, String startTime) throws Exception, SQLException{
		System.out.print("\nProvide scheduled arrival time: ");
		String arriveTime = keyboard.readLine();
		System.out.print("\nProvide driver's name: ");
		String driverName = keyboard.readLine();
		System.out.print("\nProvide bus ID: ");
		int busID = Integer.parseInt(keyboard.readLine());
		
		// Create statement and execute query
		Statement st = con.createStatement();
		
		// Try adding the information, but if duplicate, catch and tell user it already exists
		try {
			st.execute("INSERT INTO tripoffering(`TripNumber`, `Date`, `ScheduledStartTime`, "
					+ "`ScheduledArrivalTime`, `DriverName`, `BusID`) "
					+ "VALUES ('" + tripNum + "', \"" + date + "\", \"" + startTime + "\", \"" + arriveTime + "\", \"" + driverName + "\", '" + busID + "')" );
			System.out.println("Added!");
		} catch(SQLException e) {
			System.out.println("This data already exists");
		}
		
		System.out.println("Add more? (Y/N)");
		String answer = keyboard.readLine();
		boolean doneAdding = false;
		if(answer.equalsIgnoreCase("N"))
			doneAdding = true;
		
		// Loop to keep adding into table
		while(!doneAdding) {
			System.out.print("Provide trip number: ");
			tripNum = Integer.parseInt(keyboard.readLine());
			System.out.print("\nProvide date (YYYY-MM-DD): ");
			date = keyboard.readLine();
			System.out.print("\nProvide scheduled start time: ");
			startTime = keyboard.readLine();
			System.out.print("\nProvide scheduled arrival time: ");
			arriveTime = keyboard.readLine();
			System.out.print("\nProvide driver's name: ");
			driverName = keyboard.readLine();
			System.out.print("\nProvide bus ID: ");
			busID = Integer.parseInt(keyboard.readLine());
			
			//
			try {
				st.execute("INSERT INTO tripoffering(`TripNumber`, `Date`, `ScheduledStartTime`, "
						+ "`ScheduledArrivalTime`, `DriverName`, `BusID`) "
						+ "VALUES ('" + tripNum + "', \"" + date + "\", \"" + startTime + "\", \"" + arriveTime + "\", \"" + driverName + "\", '" + busID + "')" );
				System.out.println("Added!");
			} catch(SQLException e) {
				System.out.println("This data already exists");
			}
			
			System.out.println("Add more? (Y/N)");
			answer = keyboard.readLine();
			if(answer.equalsIgnoreCase("N"))
				doneAdding = true;
		}
	}
	
	// User wants to edit tripoffering by changing driver
	static void changeDriver(BufferedReader keyboard, Connection con, int tripNum, String date, String startTime) throws Exception{
		System.out.print("Provide new driver name:");
		String newDriver = keyboard.readLine();
		Statement st = con.createStatement();
		
		int rows = st.executeUpdate("UPDATE tripoffering "
				+ "SET DriverName = \"" + newDriver +"\" "
				+ "WHERE TripNumber = " + tripNum
				+ " AND Date = \"" + date + "\" "
				+ "AND ScheduledStartTime = \"" + startTime + "\" ");
		if(rows > 0)
			System.out.println("Successfully updated");
		else
			System.out.println("Data could not be found!");
	}
	
	// User wants to edit tripoffering by changing bus ID
	static void changeBus(BufferedReader keyboard, Connection con, int tripNum, String date, String startTime) throws Exception{
		System.out.print("Provide new bus ID:");
		int newBus = Integer.parseInt(keyboard.readLine());
		Statement st = con.createStatement();
		
		int rows = st.executeUpdate("UPDATE tripoffering "
				+ "SET BusID = " + newBus
				+ " WHERE TripNumber = " + tripNum
				+ " AND Date = \"" + date + "\" "
				+ "AND ScheduledStartTime = \"" + startTime + "\" ");
		if(rows > 0)
			System.out.println("Successfully updated");
		else
			System.out.println("Data could not be found!");
	}
	
	static void displayStop(BufferedReader keyboard, Connection con) throws Exception{
		System.out.print("Provide trip number: ");
		int tripNum = Integer.parseInt(keyboard.readLine());
		
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT * "
				+ "FROM tripstopinfo "
				+ "WHERE TripNumber = " + tripNum);
		StringBuilder build = new StringBuilder();
		while(rs.next()) {
			build.append(rs.getString("TripNumber"));
			build.append(" ");
			build.append(rs.getString("StopNumber"));
			build.append(" ");
			build.append(rs.getString("SequenceNumber"));
			build.append(" ");
			build.append(rs.getString("DrivingTime"));
			build.append("\n");
		}
		String output = build.toString();
		if(output.isEmpty()) {
			System.out.println("There is no data!");
		}
		else
			System.out.println(output);
	}
	
	static void displayWeeklySchedule(BufferedReader keyboard, Connection con) throws Exception {
		System.out.print("Provide driver's name: ");
		String driverName = keyboard.readLine();
		System.out.print("Provide date: ");
		String date = keyboard.readLine();
		
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT * "
				+ "FROM tripoffering "
				+ "WHERE DriverName = \"" + driverName + "\" "
				+ "AND Date BETWEEN Date(\"" + date + "\") "
				+ "AND DATE_ADD(\"" + date + "\", INTERVAL 1 week)");
		StringBuilder build = new StringBuilder();
		while(rs.next()) {
			build.append(rs.getString("TripNumber"));
			build.append(" ");
			build.append(rs.getString("Date"));
			build.append(" ");
			build.append(rs.getString("ScheduledStartTime"));
			build.append(" ");
			build.append(rs.getString("ScheduledArrivalTime"));
			build.append(" ");
			build.append(rs.getString("DriverName"));
			build.append(" ");
			build.append(rs.getString("BusID"));
			build.append("\n");
		}
		String output = build.toString();
		if(output.isEmpty()) {
			System.out.println("There is no data!");
		}
		else
			System.out.println(output);
	}
}
