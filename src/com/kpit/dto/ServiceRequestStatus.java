package com.kpit.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This DTO used to return the current status of the service requests
 * 
 * @author ROHANL1
 * 
 */
@XmlRootElement(name = "ServiceStatus")
public class ServiceRequestStatus {

	private Integer count;
	private String status;

	@XmlElement
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@XmlElement
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
