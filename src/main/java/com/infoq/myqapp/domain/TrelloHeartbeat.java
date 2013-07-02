package com.infoq.myqapp.domain;

import java.util.Calendar;
import java.util.Date;

import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Member;

public class TrelloHeartbeat {

	private String cardName;
	private String userName;
	private String userLogin;
	private String userEmail;
	private Date heartbeatDate;

	public TrelloHeartbeat(String cardName, String userName, String userLogin, String userEmail,
			Date heartbeatDate) {
		super();
		this.cardName = cardName;
		this.userName = userName;
		this.userLogin = userLogin;
		this.userEmail = userEmail;
		this.heartbeatDate = heartbeatDate;
	}

	public TrelloHeartbeat(Card card, Member user, Date heartbeatDate) {
		this(card.getName(), user.getFullName(), user.getUsername(), user.getEmail(), heartbeatDate);
	}

	public TrelloHeartbeat() {
		super();
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public void setHeartbeatDate(Date heartbeatDate) {
		this.heartbeatDate = heartbeatDate;
	}

	public String getCardName() {
		return cardName;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public Date getHeartbeatDate() {
		return heartbeatDate;
	}

	public Status getHeartbeatStatus() {
		return Status.fromDate(getHeartbeatDate());
	}

	public static enum Status {

		OLD(21), STALE(14), FRESH(0);

		private Date applicableBefore;

		private Status(int daysApplicableBefore) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, -1 * daysApplicableBefore);
			applicableBefore = c.getTime();
		}

		public static Status fromDate(Date d) {
			for (Status s : Status.values()) {
				if (d.before(s.applicableBefore))
					return s;
			}
			return FRESH;
		}
	}
}
