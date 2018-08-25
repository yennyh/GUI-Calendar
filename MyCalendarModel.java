import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * To create a calendar model like the one in the phone.
 * Models a calendar class to load, view dates, create events, go to events,
 * delete and save events. (STORE DATA ONLY)
 * @author yen_my_huynh 11/20/2017
 */

public class MyCalendarModel implements Serializable {
	// constants for months view.
	enum MONTHSVIEW {
		January, February, March, April, May, June, July, August, September, October, November, December;
	}
	// constants for event list view.
	enum DAYS {
		Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
	}
	
	private Calendar cal;
	private TreeMap<Calendar, TreeSet<Event>> eventList;
	private ArrayList<ChangeListener> listeners = new ArrayList<>();
	private int selectedDay;
	private int totalDays;
	private boolean monthChanged = false;

	/** 
	 * Constructs a new calendar and a map to save all events.
	 */
	public MyCalendarModel() {
		cal = new GregorianCalendar();
		cal = Calendar.getInstance();
		eventList = new TreeMap<>();
		selectedDay = cal.get(Calendar.DATE);
		totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		// loads any files if any when calls on.
		try {
			load();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a change listener to the list of texts.
	 * @param listener the change listener to add
	 */
	public void attach(ChangeListener listener){
		listeners.add(listener);
	}
	
	/**
	 * Updates all ChangeListeners in arraylist.
	 */
	public void updateCal() {
		for (ChangeListener l : listeners) {
			l.stateChanged(new ChangeEvent(this));
		}
	}
	
	/**
	 * Resets the time to current time/date (month view).
	 */
	public void resetTime() {
		monthChanged = false;
	}
	
	/**
	 * Gets the current year.
	 * @return the current year
	 */
	public int getCurrentYear(){
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * Gets the current month.
	 * @return the current month
	 */
	public int getCurrentMonth(){
		return cal.get(Calendar.MONTH);
	}
	
	/**
	 * Gets the current day of week.
	 * @param month the month to get the day
	 * @return the current day of week
	 */
	public int getCurrentDayofWeek(int month){
		cal.set(Calendar.DAY_OF_MONTH, month);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * Gets the total days of entered month.
	 * @return the number of days of this month
	 */
	public int getMonthDays(){
		return totalDays;
	}
	
	/**
	 * Sets selected day of the month for highlighting.
	 * @param day the selected day
	 */
	public void setSelectedDay(int day){
		selectedDay = day;
	}
	
	/**
	 * Gets the selected day of the month.
	 * @return the selected day
	 */
	public int getSelectedDay(){
		return selectedDay;
	}
	
	/**
	 * Checks if the month has changed or not.
	 * @return true if month has changed, otherwise false
	 */
	public boolean isMonthChanged(){
		return monthChanged;
	}

	/** 
	 * Checks if any events are overlapping with each other.
	 * @param day the current day to check
	 * @param e the event to be checked
	 * @return true if overlapped, otherwise false
	 */
	public boolean isOverlapping(Calendar day, Event e) {
		if (!hasEvents(day)) {
			return false;
		}
		TreeSet<Event> events = eventList.get(day);
		for (Event event : events) {
			if ((null == e.getDateTime2() || event.getDateTime().before(e.getDateTime2()))
					&& (null == event.getDateTime2() || e.getDateTime().before(event.getDateTime2()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * First time loading will notify user that there's no text file,
	 * after running the first time and then saved data using 'quit()' 
	 * later. 
	 * When user loads for a second time after using 'quit()', pre-existing 
	 * events will appear.
	 * @throws IOException throws IOException
	 * @throws FileNotFoundException throws if file not found
	 */
	public void load() throws FileNotFoundException, IOException {
		ObjectInputStream fis = null;
		try {
			fis = new ObjectInputStream(new FileInputStream("events.txt"));
		} catch (IOException e) {
			eventList.clear();
		}
		try {
			if (fis != null) {
				FileInputStream streamIn = new FileInputStream("events.txt");
				fis = new ObjectInputStream(streamIn);
				TreeMap<Calendar, TreeSet<Event>> list = (TreeMap<Calendar, TreeSet<Event>>) fis
						.readObject();
				eventList = list;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}	
	
	/**
	 * Changes and updates to the previous month.
	 */
	public void prevMonth(){
		cal.add(Calendar.MONTH, -1);
		totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		monthChanged = true;
		updateCal();
	}
	
	/**
	 * Changes and updates to the next month.
	 */
	public void nextMonth(){
		cal.add(Calendar.MONTH, 1);
		totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		monthChanged = true;
		updateCal();
	}
	
	/**
	 * Changes and updates to the previous day.
	 */
	public void prevDay(){
		selectedDay--;
		if (selectedDay < 1) {
			prevMonth();
			selectedDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		updateCal();
	}
	
	/**
	 * Changes and updates to the next day.
	 */
	public void nextDay(){
		selectedDay++;
		if (selectedDay > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			nextMonth();
			selectedDay = 1;
		}
		updateCal();
	}

	/**
	 * Creates an event with or without existing events to a list.
	 * @param day the day of the event to be added
	 * @param e the existing event in the map if any
	 */
	public void createEvent(Calendar day, Event e) {
		if (this.eventList.get(day) == null) {
			TreeSet<Event> eventHolder = new TreeSet<>();
			eventHolder.add(e);
			eventList.put(day, eventHolder);
		}
		this.eventList.get(day).add(e);
	}

	/**
	 * Checks if there is an event on a selected date.
	 * @return true if there's an event, otherwise false
	 */
	public boolean hasEvents(Calendar day){
		return eventList.containsKey(day);
	}
	/**
	 * Views only events from this date.
	 * @param day the day to check if there are any events
	 */
	public String viewEventFromDay(Calendar day) {
		String events = "";
		if (eventList.get(day) != null) {
			for (Event e : eventList.get(day)) {
				events += e.toString() + "\n";
			}
		}
		return events;
	}
	
	/**
	 * Deletes an event from the list.
	 * @param day the day to delete existing events if any
	 */
	public void deleteEvent(Calendar day) {
		eventList.remove(day);
	}

	/**
	 * Quits and saves "events.txt" to populate the calendar the first time.
	 * Quits and saves "event.txt" again when called and added in more events.
	 * @throws IOException throws IOException
	 * @throws FileNotFoundException throws if no file is found
	 */
	public void quit() throws FileNotFoundException, IOException {
		try (FileOutputStream f = new FileOutputStream("events.txt")) {
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(eventList);
			s.close();
			f.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
}

	
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.TreeMap;
//
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;

//public class MyCalendarModel{
//
//	private int maxDays;
//	private int selectedDay;
//	private TreeMap<String, ArrayList<Event>> eventMap = new TreeMap<>();
//	private ArrayList<ChangeListener> listeners = new ArrayList<>();
//	private GregorianCalendar cal = new GregorianCalendar();
//	private boolean monthChanged = false;
//	
//	/**
//	 * Constructor
//	 */
//	public MyCalendarModel() {
//		maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//		selectedDay = cal.get(Calendar.DATE);
//		loadEvents();
//	}
//	
//	/**
//	 * Adds ChangeListeners to array.
//	 * @param l the ChangeListener
//	 */
//	public void attach(ChangeListener l) {
//		listeners.add(l);
//	}
//	
//	/**
//	 * Updates all ChangeListeners in array.
//	 */
//	public void update() {
//		for (ChangeListener l : listeners) {
//			l.stateChanged(new ChangeEvent(this));
//		}
//	}
//	
//	/**
//	 * Sets the user selected day.
//	 * @param day the day of the month
//	 */
//	public void setSelectedDate(int day) {
//		selectedDay = day;
//	}
//	
//	/**
//	 * Gets the user selected day.
//	 * @return the day of the month
//	 */
//	public int getSelectedDay() {
//		return selectedDay;
//	}
//
//	/**
//	 * Gets the current year the calendar is at.
//	 * @return the current year
//	 */
//	public int getCurrentYear() {
//		return cal.get(Calendar.YEAR);
//	}
//	
//	/**
//	 * Gets the current month the calendar is at.
//	 * @return the current month
//	 */
//	public int getCurrentMonth() {
//		return cal.get(Calendar.MONTH);
//	}
//	
//	/**
//	 * Gets the value representing the day of the week
//	 * @param i the day of the month
//	 * @return the day of the week (1-7)
//	 */
//	public int getDayOfWeek(int i) {
//		cal.set(Calendar.DAY_OF_MONTH, i);
//		return cal.get(Calendar.DAY_OF_WEEK);
//	}
//	
//	/**
//	 * Gets the max number of days in a month.
//	 * @return the max number of days in a month
//	 */
//	public int getMaxDays() {
//		return maxDays;
//	}
//
//	/**
//	 * Moves the calendar forward by one month.
//	 */
//	public void nextMonth() {
//		cal.add(Calendar.MONTH, 1);
//		maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//		monthChanged = true;
//		update();
//	}
//	
//	/**
//	 * Moves the calendar backward by one month.
//	 */
//	public void prevMonth() {
//		cal.add(Calendar.MONTH, -1);
//		maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//		monthChanged = true;
//		update();
//	}
//	
//	/**
//	 * Moves the selected day forward by one.
//	 */
//	public void nextDay() {
//		selectedDay++;
//		if (selectedDay > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
//			nextMonth();
//			selectedDay = 1;
//		}
//		update();
//	}
//	
//	/**
//	 * Moves the selected day backward by one.
//	 */
//	public void prevDay() {
//		selectedDay--;
//		if (selectedDay < 1) {
//			prevMonth();
//			selectedDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//		}
//		update();
//	}
//	
//	/**
//	 * Checks if the month has changed as a result of user interaction.
//	 * @return
//	 */
//	public boolean hasMonthChanged() {
//		return monthChanged;
//	}
//	
//	/**
//	 * Resets monthChanged to false.
//	 */
//	public void resetHasMonthChanged() {
//		monthChanged = false;
//	}
//	
//	/**
//	 * Creates an event on the currently selected date.
//	 * @param title the title of the event
//	 * @param startTime the start time of the event
//	 * @param endTime the end time of the event
//	 */
//	public void createEvent(String title, String startTime, String endTime) {
//		String date = (cal.get(Calendar.MONTH) + 1) + "/" + selectedDay + "/" + cal.get(Calendar.YEAR);
//		Event e = new Event(title, date, startTime, endTime);
//		ArrayList<Event> eventArray = new ArrayList<>();
//		if (hasEvent(e.getDate())) {
//			eventArray = eventMap.get(date);
//		}
//		eventArray.add(e);
//		eventMap.put(date, eventArray);
//	}
//	
//	/**
//	 * Checks if specified date has any events scheduled.
//	 * @param date the selected date in format MM/DD/YYYY
//	 * @return if the date has an event
//	 */
//	public Boolean hasEvent(String date) {
//		return eventMap.containsKey(date);
//	}
//
//	/**
//	 * Checks if a new event has a time conflict with an existing event.
//	 * @param timeStart the start time of the new event
//	 * @param timeEnd the end time of the new event
//	 * @return whether there is a conflict in time
//	 */
//	public Boolean hasEventConflict(String timeStart, String timeEnd) {
//		String date = (getCurrentMonth() + 1) + "/" + selectedDay + "/" + getCurrentYear();
//		if (!hasEvent(date)) {
//			return false;
//		}
//		
//		ArrayList<Event> eventArray = eventMap.get(date);
//		Collections.sort(eventArray, timeComparator());
//		
//		int timeStartMins = convertHourToMin(timeStart), timeEndMins = convertHourToMin(timeEnd);
//		for (Event e : eventArray) {
//			int eventStartTime = convertHourToMin(e.getStartTime()), eventEndTime = convertHourToMin(e.getEndTime());
//			if (timeStartMins >= eventStartTime && timeStartMins < eventEndTime) {
//				return true;
//			} else if (timeStartMins <= eventStartTime && timeEndMins > eventStartTime) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	/**
//	 * Gets a string of all events on a particular date.
//	 * @param date the date to get events for
//	 * @return a string of all events on specified date
//	 */
//	public String getEvents(String date) {
//		ArrayList<Event> eventArray = eventMap.get(date);
//		Collections.sort(eventArray, timeComparator());
//		String events = "";
//		for (Event e : eventArray) {
//			events += e.toString() + "\n";
//		}
//		return events;
//	}
//	
//	/**
//	 * Saves all events to "events.ser".
//	 */
//	public void saveEvents() {
//		if (eventMap.isEmpty()) {
//			return;
//		}
//		try {
//			FileOutputStream fOut = new FileOutputStream("events.ser");
//			ObjectOutputStream oOut = new ObjectOutputStream(fOut);
//			oOut.writeObject(eventMap);
//			oOut.close();
//			fOut.close();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	/**
//	 * Loads all events from "events.ser".
//	 */
//	@SuppressWarnings("unchecked")
//	private void loadEvents() {
//		try {
//			FileInputStream fIn = new FileInputStream("events.ser");
//			ObjectInputStream oIn = new ObjectInputStream(fIn);
//			HashMap<String, ArrayList<Event>> temp = (HashMap<String, ArrayList<Event>>) oIn.readObject();
//			for (String date : temp.keySet()) {
//				if (hasEvent(date)) {
//					ArrayList<Event> eventArray = eventMap.get(date);
//					eventArray.addAll(temp.get(date));
//				} else {
//					eventMap.put(date, temp.get(date));
//				}
//			}
//			oIn.close();
//			fIn.close();
//		} catch (IOException ioe) {
//		} catch (ClassNotFoundException c) {
//			System.out.println("Class not found");
//			c.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Converts 24:00 time to minutes
//	 * @param time the time in 24 hour format
//	 * @return the time converted to minutes
//	 */
//	private int convertHourToMin(String time) {
//		int hours = Integer.valueOf(time.substring(0, 2));
//		return hours * 60 + Integer.valueOf(time.substring(3));
//	}
//	/**
//	 * Comparator for comparing by time in format XX:XX.
//	 * @return The comparator.
//	 */
//	private static Comparator<Event> timeComparator() {
//		return new Comparator<Event>() {
//			@Override
//			public int compare(Event e1, Event e2) {
//				if (e1.getStartTime().substring(0, 2).equals(e2.getStartTime().substring(0, 2))) {
//					return Integer.parseInt(e1.getStartTime().substring(3, 5)) - Integer.parseInt(e2.getStartTime().substring(3, 5));
//				}
//				return Integer.parseInt(e1.getStartTime().substring(0, 2)) - Integer.parseInt(e2.getStartTime().substring(0, 2));
//			}
//		};
//	}
//}
