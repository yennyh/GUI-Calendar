/**
 * Models a SimpleCalendar class to access calendar model and 
 * runs the calendar controller/view.
 * 
 * @author yen_my_huynh 11/20/2017
 */
public class SimpleCalendar {
	public static void main(String[] args) {
		MyCalendarModel mcm = new MyCalendarModel();
		MyCalendarView mcv = new MyCalendarView(mcm);
		mcm.attach(mcv);
	}
}
