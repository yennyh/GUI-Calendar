//import java.io.Serializable;
//
///**
// * HW1: To create a calendar like the one in the phone. Models an Event class to
// * create events and save events, with few attributes of name, day and times.
// * 
// * @author yen_my_huynh 09/16/2017
// */
//public class Event implements Serializable {
//	private String title;
//	private String date;
//	private String startTime;	
//	private String endTime;
//
//	// constants for months view.
//	enum MONTHSVIEW {
//		January, February, March, April, May, June, July, August, September, October, November, December;
//	}
//
//	// constants for days view.
//	enum DAYS {
//		Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
//	}
//
//	/**
//	 * Constructs an event with a name, day, and times.
//	 * @param title the name of the event
//	 * @param day the day of the event
//	 * @param times the starting and ending times of the event
//	 */
//	public Event(String title, String date, String startTime, String endTime) {
//		this.title = title;
//		this.date = date;
//		this.startTime = startTime;
//		this.endTime = endTime;
//
//	}
//
//	/**
//	 * Gets the event name.
//	 * @return the event name
//	 */
//	public String getTitle() {
//		return title;
//	}
//	public String getDate() {
//		return date;
//	}
//	public String getStartTime() {
//		return startTime;
//	}
//	public String getEndTime() {
//		return endTime;
//	}
//
//	/**
//	 * Gets the event with year, day, month, date, times, and name.
//	 * @return the event in a certain format
//	 */
//	public String toString(){
//		if (endTime.equals("")) {
//			return title + " ~~~ " + startTime;
//		}
//		return title + " ~~~ " + startTime + " - " + endTime;
//	}
//}

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Models an Event class to
 * create events and save events, with few attributes of name, day and times.
 * 
 * @author yen_my_huynh 11/20/2017
 */
public class Event implements Comparable<Event>, Serializable {
	private String title;
	private Calendar day;
	private Date[] times;

	// constants for months view.
	enum MONTHSVIEW {
		January, February, March, April, May, June, July, August, September, October, November, December;
	}

	// constants for days view.
	enum DAYS {
		Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
	}

	/**
	 * Constructs an event with a name, day, and times.
	 * @param title the name of the event
	 * @param day the day of the event
	 * @param times the starting and ending times of the event
	 */
	public Event(String title, Calendar day, Date[] times) {
		this.title = title;
		this.day = day;
		this.times = times;

	}

	/**
	 * Gets the event name.
	 * @return the event name
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the whole event list of this date.
	 * @return the whole list of this date for comparison later
	 */
	public Date[] getDate() {
		return times;
	}

	/**
	 * Gets the start time of the event.
	 * @return the start time of the event
	 */
	public Date getDateTime() {
		return times[0];
	}

	/**
	 * Gets the ending time of the event.
	 * @return the ending time of the event
	 */
	public Date getDateTime2() {
		return times[1];
	}

	/**
	 * Compares the order of the events by dates, start times and end times.
	 * @param x the other date
	 * @return the order of events in the map
	 */
	public int compareTo(Event x) {
		Event other = (Event) x;
		return this.getDateTime().compareTo(other.getDateTime());
	}

	/**
	 * Check if two events are equal to each other.
	 * @param x the other event
	 * @return true if two events are equal, if not, false
	 */
	public boolean equals(Object x) {
		Event other = (Event) x;
		return this.day.compareTo(other.day) == 0;
	}

	/**
	 * Prints out the starting and ending times for certain events. If any
	 * events don't have an ending time, all other events to be added later will
	 * be cancelled.
	 */
	public String toString(){
		String time1 = new SimpleDateFormat("HH:mm").format(getDateTime());
		String time2 = new SimpleDateFormat("HH:mm").format(getDateTime2());
		return time1 + " - " + time2 + " ~~~ " +getTitle();
	}
}
