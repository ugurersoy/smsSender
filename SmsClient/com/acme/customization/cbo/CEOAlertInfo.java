package com.acme.customization.cbo;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;


public class CEOAlertInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String userName = "";
	private String password = "";
	
	private boolean schedule = false;
	private boolean periodic = false;

	private int period = 0;
	private int alertRef = 0;
	
	private Calendar scheduleDate = null;
	private Calendar beginDate = null;
	private Calendar endDate = null;
	
	private ArrayList smsObjectList = new ArrayList();
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Calendar getScheduleDate() {
		return scheduleDate;
	}
	public void setScheduleDate(Calendar scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
	public boolean isPeriodic() {
		return periodic;
	}
	public void setPeriodic(boolean periodic) {
		this.periodic = periodic;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public Calendar getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Calendar beginDate) {
		this.beginDate = beginDate;
	}
	public Calendar getEndDate() {
		return endDate;
	}
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
	public ArrayList getSmsObjectList() {
		return smsObjectList;
	}
	public void setSmsObjectList(ArrayList smsObjectList) {
		this.smsObjectList = smsObjectList;
	}
	public boolean isSchedule() {
		return schedule;
	}
	public void setSchedule(boolean schedule) {
		this.schedule = schedule;
	}
	public int getAlertRef() {
		return alertRef;
	}
	public void setAlertRef(int alertRef) {
		this.alertRef = alertRef;
	}
}
