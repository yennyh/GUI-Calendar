import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * View and controller for Calendar.
 * Extra methods for fun.
 * 
 * @author yen_my_huynh 11/20/2017
 */
public class MyCalendarView implements ChangeListener {
// constants for months view.
enum MONTHSVIEW {
	January, February, March, April, May, June, July, August, September, October, November, December;
}
// constants for event list view.
enum DAYS {
	Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
}

	private MyCalendarModel model;
	private DAYS[] arrayOfDays = DAYS.values();
	private MONTHSVIEW[] arrayOfMonths = MONTHSVIEW.values();
	private int prevHighlight = -1;
	private int maxDays;

	private JFrame frame = new JFrame("My Calendar");
	private JPanel monthViewPanel = new JPanel();
	private JLabel monthLabel = new JLabel();
	private JButton create = new JButton("Create");
	private JButton nextDay = new JButton(">");
	private JButton prevDay = new JButton("<");
	private JTextPane dayTextPane = new JTextPane();
	private ArrayList<JButton> dayBtns = new ArrayList<JButton>();

	/**
	 * Constructs the calendar.
	 * @param model the model that stores and manipulates calendar data
	 */
	public MyCalendarView(MyCalendarModel model) {
		this.model = model;
		maxDays = model.getMonthDays();
		monthViewPanel.setLayout(new GridLayout(0, 7));
		dayTextPane.setPreferredSize(new Dimension(300, 150));
		dayTextPane.setEditable(false);

		// calendar month view buttons
		
		addBlankButtons();
		createDayButtons();
		addDayButtons();
		showDate(model.getSelectedDay());
		highlightSelectedDate(model.getSelectedDay() - 1);
		
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createEventDialog();
			}
		});
		// for month views
		JButton prevMonth = new JButton("<<");
		prevMonth.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				model.prevMonth();
				create.setEnabled(false);
				nextDay.setEnabled(false);
				prevDay.setEnabled(false);
				dayTextPane.setText("");
			}
		});
		JButton nextMonth = new JButton(">>");
		nextMonth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.nextMonth();
				create.setEnabled(false);
				nextDay.setEnabled(false);
				prevDay.setEnabled(false);
				dayTextPane.setText("");
			}
		});
		
		JPanel monthContainer = new JPanel();
		monthContainer.setLayout(new BorderLayout());
		monthLabel.setText(arrayOfMonths[model.getCurrentMonth()] + " " + model.getCurrentYear());
		monthContainer.add(monthLabel, BorderLayout.NORTH);
		monthContainer.add(new JLabel("        S                 M                 T                W                T                 F                 S"), BorderLayout.CENTER);
		monthContainer.add(monthViewPanel, BorderLayout.SOUTH);
		
		JPanel dayViewPanel = new JPanel();
		dayViewPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		JScrollPane dayScrollPane = new JScrollPane(dayTextPane);
		dayScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		dayViewPanel.add(dayScrollPane, c);
		JPanel btnsPanel = new JPanel();
		nextDay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.nextDay();
			}
		});
		prevDay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.prevDay();
			}
		});
		btnsPanel.add(prevDay);
		create.setBackground(Color.RED);
		create.setOpaque(true);
		create.setForeground(Color.WHITE);
		create.setBorderPainted(false);
		btnsPanel.add(nextDay);
		btnsPanel.add(create);
		btnsPanel.add(prevMonth);
		btnsPanel.add(nextMonth);;
		c.gridx = 0;
		c.gridy = 1;
		dayViewPanel.add(btnsPanel, c);

		// creates a quit button to saves all events.
		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					model.quit();
					JDialog deleteDialog = new JDialog();
					deleteDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
					deleteDialog.setLayout(new GridLayout(2, 0));
					deleteDialog.add(new JLabel("Everything is saved in 'events.txt.' file."));
					JButton okButton = new JButton("Okay");
					okButton.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							deleteDialog.dispose();
						}
					});
					deleteDialog.add(okButton);
					deleteDialog.pack();
					deleteDialog.setVisible(true);
					System.exit(0);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		// creates a delete button to delete all events of a selected day.
		JButton delete = new JButton("Delete");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Calendar day = new GregorianCalendar(model.getCurrentYear(), model.getCurrentMonth(), model.getSelectedDay());
				model.deleteEvent(day);
				JDialog deleteDialog = new JDialog();
				deleteDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
				deleteDialog.setLayout(new GridLayout(2, 0));
				deleteDialog.add(new JLabel("Everything is clear for this day."));
				JButton okButton = new JButton("Okay");
				okButton.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						deleteDialog.dispose();
					}
				});
				deleteDialog.add(okButton);
				deleteDialog.pack();
				deleteDialog.setVisible(true);
			}
		});
		frame.add(monthContainer);
		frame.add(dayViewPanel);
		frame.add(quit);
		frame.add(delete, BorderLayout.SOUTH);
		frame.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Changes the state of day view after a button is clicked.
	 */
	public void stateChanged(ChangeEvent e) {
		if (model.isMonthChanged()) {
			maxDays = model.getMonthDays();
			dayBtns.clear();
			monthViewPanel.removeAll();
			monthLabel.setText(arrayOfMonths[model.getCurrentMonth()] + " " + model.getCurrentYear());
			createDayButtons();
			addBlankButtons();
			addDayButtons();
			prevHighlight = -1;
			model.resetTime();
			frame.pack();
			frame.repaint();
		} else {
			showDate(model.getSelectedDay());
			highlightSelectedDate(model.getSelectedDay()-1);
		}
	}
	
	/**
	 * Creates an event on the selected date through user input.
	 */
	private void createEventDialog() {
		final JDialog eventDialog = new JDialog();
		eventDialog.setTitle("Create An Event");
		eventDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		final JTextField eventText = new JTextField(30);
		final JTextField startTime = new JTextField(10);
		final JTextField endTime = new JTextField(10);
		JButton save = new JButton("SAVE");
		save.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (eventText.getText().isEmpty()) {
					return;
				}
				String pattern = "HH:mm";
				String name = eventText.getText();
				Calendar day = new GregorianCalendar(model.getCurrentYear(), model.getCurrentMonth(), model.getSelectedDay());
				String[] wholeDate = new String[2];
				wholeDate[0] = startTime.getText();
				wholeDate[1] = endTime.getText();
				Date[] dateHolder = new Date[2];
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
				for (int i = 0; i < wholeDate.length; i++) {
					Date dateParse = null;
					try {
						dateParse = simpleDateFormat.parse(wholeDate[i]);
					} catch (ParseException e2) {
						e2.printStackTrace();
					}

					if (wholeDate[1] == null) {
						try {
							dateParse = simpleDateFormat.parse(wholeDate[0]);
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
					dateHolder[i] = dateParse;
				}
				Event event = new Event(name, day, dateHolder);
				
				// checks for correct format as specified
				if ((!eventText.getText().isEmpty() && (startTime.getText().isEmpty() || endTime.getText().isEmpty()))
						|| startTime.getText().length() != 5
						|| endTime.getText().length() != 5
						|| !startTime.getText().matches("([01][0-9]|2[0-3]):[0-5][0-9]")
						|| !endTime.getText().matches("([01][0-9]|2[0-3]):[0-5][0-9]")) {
					JDialog timeErrorDialog = new JDialog();
					timeErrorDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
					timeErrorDialog.setLayout(new GridLayout(2, 0));
					timeErrorDialog.add(new JLabel("Please enter start and end time in format HH:MM."));
					JButton okButton = new JButton("Okay");
					okButton.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							timeErrorDialog.dispose();
						}
					});
					timeErrorDialog.add(okButton);
					timeErrorDialog.pack();
					timeErrorDialog.setVisible(true);
					
					// checks for overlapping events
				} else if (!eventText.getText().equals("")) {
					if (model.isOverlapping(day, event)) {
						JDialog conflictDialog = new JDialog();
						conflictDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
						conflictDialog.setLayout(new GridLayout(2, 0));
						conflictDialog.add(new JLabel("There's already an existing event during this time. Choose another date/time."));
						JButton okButton = new JButton("Okay");
						okButton.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent e) {
								conflictDialog.dispose();
							}
						});
						conflictDialog.add(okButton);
						conflictDialog.pack();
						conflictDialog.setVisible(true);
					} else {
						eventDialog.dispose();
						model.createEvent(day, event);
						showDate(model.getSelectedDay());
					}
				}
			}
		});
		eventDialog.setLayout(new GridBagLayout());
		JLabel date = new JLabel();
		date.setText(model.getCurrentMonth() + 1 + "/" + model.getSelectedDay() + "/" + model.getCurrentYear());
		date.setBorder(BorderFactory.createEmptyBorder());

		// layers out the create box.
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.gridx = 0;
		c.gridy = 0;
		eventDialog.add(date, c);
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		eventDialog.add(new JLabel("Event Name:"), c);
		c.gridy = 2;
		eventDialog.add(eventText, c);
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		eventDialog.add(new JLabel("Time Starts (00:00)"), c);
		c.anchor = GridBagConstraints.CENTER;
		eventDialog.add(new JLabel("Time Ends (24:00)"), c);
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		eventDialog.add(startTime, c);
		c.anchor = GridBagConstraints.CENTER;
		eventDialog.add(endTime, c);
		c.anchor = GridBagConstraints.LINE_END;
		eventDialog.add(save, c);
		eventDialog.pack();
		eventDialog.setVisible(true);
	}
	
	/**
	 * Shows the selected date and events on that date.
	 * @param date the selected date
	 */
	private void showDate(int date) {
		model.setSelectedDay(date);
		String dayOfWeek = arrayOfDays[model.getCurrentDayofWeek(date) - 1] + "";
		Calendar day = new GregorianCalendar(model.getCurrentYear(), model.getCurrentMonth(), date);
		String dateString = (model.getCurrentMonth() + 1) + "/" + date;
		String events = "";
		if (model.hasEvents(day)) {
			events += model.viewEventFromDay(day);
		}
		dayTextPane.setText(dayOfWeek + " " + dateString + "\n"+ events);
		dayTextPane.setCaretPosition(0);
	}

	/**
	 * Highlights the currently selected date.
	 * @param d the currently selected date
	 */
	private void highlightSelectedDate(int date) {
		Border border = new LineBorder(Color.ORANGE, 2);
		dayBtns.get(date).setBorder(border);
		if (prevHighlight != -1) {
			dayBtns.get(prevHighlight).setBorder(new JButton().getBorder());
		}
		prevHighlight = date;
	}

	/**
	 * Creates buttons representing days of the current month and adds them to an array list.
	 */
	private void createDayButtons() {
		for (int i = 1; i <= maxDays; i++) {
			final int d = i;
			JButton day = new JButton(Integer.toString(d));
			day.setBackground(Color.WHITE);
	
			day.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent ae) {
					showDate(d);
					highlightSelectedDate(d - 1);
					create.setEnabled(true);
					nextDay.setEnabled(true);
					prevDay.setEnabled(true);
				}
			});
			dayBtns.add(day);
		}
	}

	/**
	 * Adds days buttons of the month to the panel.
	 */
	private void addDayButtons() {
		for (JButton day : dayBtns) {
			monthViewPanel.add(day);
		}
	}

	/**
	 * Adds dummy buttons to align the date to correct day in the calendar.
	 */
	private void addBlankButtons() {
		for (int i = 1; i < model.getCurrentDayofWeek(1); i++) {
			JButton blank = new JButton();
			blank.setEnabled(false);
			monthViewPanel.add(blank);
		}
	}
}

//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dialog;
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.GridLayout;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.ArrayList;
//
//import javax.swing.BorderFactory;
//import javax.swing.JDialog;
//import javax.swing.JFrame;
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextField;
//import javax.swing.JTextPane;
//import javax.swing.border.Border;
//import javax.swing.border.LineBorder;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//
//public class MyCalendarView implements ChangeListener {
//
////constants for months view.
//enum MONTHSVIEW {
//	January, February, March, April, May, June, July, August, September, October, November, December;
//}
////constants for event list view.
//enum DAYS {
//	Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
//}
//	private MyCalendarModel model;
//	private DAYS[] arrayOfDays = DAYS.values();
//	private MONTHSVIEW[] arrayOfMonths = MONTHSVIEW.values();
//	private int prevHighlight = -1;
//	private int maxDays;
//
//	private JFrame frame = new JFrame("Simple Calendar");
//	private JPanel monthViewPanel = new JPanel();
//	private JLabel monthLabel = new JLabel();
//	private JButton create = new JButton("Create");
//	private JButton nextDay = new JButton(">");
//	private JButton prevDay = new JButton("<");
//	private JTextPane dayTextPane = new JTextPane();
//	private ArrayList<JButton> dayBtns = new ArrayList<JButton>();
//
//	/**
//	 * Constructs the calendar.
//	 * @param model the  model that stores and manipulates calendar data
//	 */
//	public MyCalendarView(MyCalendarModel model) {
//		this.model = model;
//		maxDays = model.getMaxDays();
//		monthViewPanel.setLayout(new GridLayout(0, 7));
//		dayTextPane.setPreferredSize(new Dimension(300, 150));
//		dayTextPane.setEditable(false);
//
//		createDayBtns();
//		addBlankButtons();
//		addDayBtns();
//		showDate(model.getSelectedDay());
//		highlightSelectedDate(model.getSelectedDay() - 1);
//
//		create.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				createEventDialog();
//			}
//		});
//	
//		
//		JPanel monthContainer = new JPanel();
//		monthContainer.setLayout(new BorderLayout());
//		monthLabel.setText(arrayOfMonths[model.getCurrentMonth()] + " " + model.getCurrentYear());
//		monthContainer.add(monthLabel, BorderLayout.NORTH);
//		monthContainer.add(new JLabel("        S                M                 T                 W                T                 F                 S"), BorderLayout.CENTER);
//		monthContainer.add(monthViewPanel, BorderLayout.SOUTH);
//		
//		JPanel dayViewPanel = new JPanel();
//		dayViewPanel.setLayout(new GridBagLayout());
//		GridBagConstraints c = new GridBagConstraints();
//		c.fill = GridBagConstraints.HORIZONTAL;
//		JScrollPane dayScrollPane = new JScrollPane(dayTextPane);
//		dayScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//		dayViewPanel.add(dayScrollPane, c);
//		JPanel btnsPanel = new JPanel();
//		nextDay.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				model.nextDay();
//			}
//		});
//		prevDay.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				model.prevDay();
//			}
//		});
//		btnsPanel.add(prevDay);
//		btnsPanel.add(create);
//		btnsPanel.add(nextDay);
//		c.gridx = 0;
//		c.gridy = 1;
//		dayViewPanel.add(btnsPanel, c);
//
//		JButton quit = new JButton("Quit");
//		quit.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				model.saveEvents();
//				System.exit(0);
//			}
//		});
//		frame.add(monthContainer);
//		frame.add(dayViewPanel);
//		frame.add(quit);
//		frame.setLayout(new FlowLayout());
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setVisible(true);
//	}
//
//	@Override
//	public void stateChanged(ChangeEvent e) {
//		if (model.hasMonthChanged()) {
//			maxDays = model.getMaxDays();
//			dayBtns.clear();
//			monthViewPanel.removeAll();
//			monthLabel.setText(arrayOfMonths[model.getCurrentMonth()] + " " + model.getCurrentYear());
//			createDayBtns();
//			addBlankButtons();
//			addDayBtns();
//			prevHighlight = -1;
//			model.resetHasMonthChanged();
//			frame.pack();
//			frame.repaint();
//		} else {
//			showDate(model.getSelectedDay());
//			highlightSelectedDate(model.getSelectedDay() - 1);
//		}
//	}
//
//	/**
//	 * Creates an event on the selected date through user input.
//	 */
//	private void createEventDialog() {
//		final JDialog eventDialog = new JDialog();
//		eventDialog.setTitle("Create An Event");
//		eventDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
//		final JTextField eventText = new JTextField(30);
//		final JTextField timeStart = new JTextField(10);
//		final JTextField timeEnd = new JTextField(10);
//		JButton save = new JButton("SAVE");
//		save.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if (eventText.getText().isEmpty()) {
//					return;
//				}
//				if ((!eventText.getText().isEmpty() && (timeStart.getText().isEmpty() || timeEnd.getText().isEmpty()))
//						|| timeStart.getText().length() != 5
//						|| timeEnd.getText().length() != 5
//						|| !timeStart.getText().matches("([01][0-9]|2[0-3]):[0-5][0-9]")
//						|| !timeEnd.getText().matches("([01][0-9]|2[0-3]):[0-5][0-9]")) {
//					JDialog timeErrorDialog = new JDialog();
//					timeErrorDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
//					timeErrorDialog.setLayout(new GridLayout(2, 0));
//					timeErrorDialog.add(new JLabel("Please enter appropriate start and end time in the format HH:MM."));
//					JButton okButton = new JButton("Okay");
//					okButton.addActionListener(new ActionListener() {
//
//						@Override
//						public void actionPerformed(ActionEvent e) {
//							timeErrorDialog.dispose();
//						}
//					});
//					timeErrorDialog.add(okButton);
//					timeErrorDialog.pack();
//					timeErrorDialog.setVisible(true);
//				} else if (!eventText.getText().equals("")) {
//					if (model.hasEventConflict(timeStart.getText(), timeEnd.getText())) {
//						JDialog conflictDialog = new JDialog();
//						conflictDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
//						conflictDialog.setLayout(new GridLayout(3, 10));
//						conflictDialog.add(new JLabel("There's already an existing event during this time. "
//								+ "Choose another date/time."));
//						JButton okButton = new JButton("Okay");
//						okButton.addActionListener(new ActionListener() {
//
//							@Override
//							public void actionPerformed(ActionEvent e) {
//								conflictDialog.dispose();
//							}
//						});
//						conflictDialog.add(okButton);
//						conflictDialog.pack();
//						conflictDialog.setVisible(true);
//					} else {
//						eventDialog.dispose();
//						model.createEvent(eventText.getText(), timeStart.getText(), timeEnd.getText());
//						showDate(model.getSelectedDay());
//					}
//				}
//			}
//		});
//		eventDialog.setLayout(new GridBagLayout());
//		JLabel date = new JLabel();
//		date.setText(model.getCurrentMonth() + 1 + "/" + model.getSelectedDay() + "/" + model.getCurrentYear());
//		date.setBorder(BorderFactory.createEmptyBorder());
//
//		GridBagConstraints c = new GridBagConstraints();
//		c.insets = new Insets(2, 2, 2, 2);
//		c.gridx = 0;
//		c.gridy = 0;
//		eventDialog.add(date, c);
//		c.gridy = 1;
//		c.weightx = 1.0;
//		c.anchor = GridBagConstraints.LINE_START;
//		eventDialog.add(new JLabel("Event Name: "), c);
//		c.gridy = 2;
//		eventDialog.add(eventText, c);
//		c.gridy = 3;
//		c.weightx = 0.0;
//		c.anchor = GridBagConstraints.LINE_START;
//		eventDialog.add(new JLabel("Time Start (00:00)"), c);
//		c.anchor = GridBagConstraints.CENTER;
//		eventDialog.add(new JLabel("Time End (24:00)"), c);
//		c.gridy = 4;
//		c.anchor = GridBagConstraints.LINE_START;
//		eventDialog.add(timeStart, c);
//		c.anchor = GridBagConstraints.CENTER;
//		eventDialog.add(timeEnd, c);
//		c.anchor = GridBagConstraints.LINE_END;
//		eventDialog.add(save, c);
//		eventDialog.pack();
//		eventDialog.setVisible(true);
//	}
//
//	/**
//	 * Shows the selected date and events on that date.
//	 * @param d the selected date
//	 */
//	private void showDate(final int d) {
//		model.setSelectedDate(d);
//		String dayOfWeek = arrayOfDays[model.getDayOfWeek(d) - 1] + "";
//		String date = (model.getCurrentMonth() + 1) + "/" + d + "/" + model.getCurrentYear();
//		String dateForShow = (model.getCurrentMonth() + 1) + "/" + d;
//		String events = "";
//		if (model.hasEvent(date)) {
//			events += model.getEvents(date);
//		}
//		dayTextPane.setText(dayOfWeek + " " + dateForShow + "\n" + events);
//		dayTextPane.setCaretPosition(0);
//	}
//
//	/**
//	 * Highlights the currently selected date.
//	 * @param d the currently selected date
//	 */
//	private void highlightSelectedDate(int d) {
//		Border border = new LineBorder(Color.ORANGE, 2);
//		dayBtns.get(d).setBorder(border);
//		if (prevHighlight != -1) {
//			dayBtns.get(prevHighlight).setBorder(new JButton().getBorder());
//		}
//		prevHighlight = d;
//	}
//
//	/**
//	 * Creates buttons representing days of the current month and adds them to an array list.
//	 */
//	private void createDayBtns() {
//		for (int i = 1; i <= maxDays; i++) {
//			final int d = i;
//			JButton day = new JButton(Integer.toString(d));
//			day.setBackground(Color.WHITE);
//	
//			day.addActionListener(new ActionListener() {
//	
//				@Override
//				public void actionPerformed(ActionEvent arg0) {
//					showDate(d);
//					highlightSelectedDate(d - 1);
//					create.setEnabled(true);
//					nextDay.setEnabled(true);
//					prevDay.setEnabled(true);
//				}
//			});
//			dayBtns.add(day);
//		}
//	}
//
//	/**
//	 * Adds the buttons representing the days of the month to the panel.
//	 */
//	private void addDayBtns() {
//		for (JButton d : dayBtns) {
//			monthViewPanel.add(d);
//		}
//	}
//
//	/**
//	 * Adds filler buttons before the start of the month to align calendar.
//	 */
//	private void addBlankButtons() {
//		for (int j = 1; j < model.getDayOfWeek(1); j++) {
//			JButton blank = new JButton();
//			blank.setEnabled(false);
//			monthViewPanel.add(blank);
//		}
//	}
//}