package com.ctl.hyperconverse.response.model;

public class SenderActions {

	private Recipient recipient;
	private String sender_action;
	
	public SenderActions() {
		
	}
	public SenderActions(Recipient recipient, String sender_action) {
		super();
		this.recipient = recipient;
		this.sender_action = sender_action;
	}
	public Recipient getRecipient() {
		return recipient;
	}
	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}
	public String getSender_action() {
		return sender_action;
	}
	public void setSender_action(String sender_action) {
		this.sender_action = sender_action;
	}
	
	
}
