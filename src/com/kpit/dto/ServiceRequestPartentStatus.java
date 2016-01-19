package com.kpit.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ServiceStatusParent")
public class ServiceRequestPartentStatus {
	private List<ServiceRequestStatus> serviceRequestStatus;

	@XmlElement
	public List<ServiceRequestStatus> getServiceRequestStatus() {
		return serviceRequestStatus;
	}

	public void setServiceRequestStatus(
			List<ServiceRequestStatus> serviceRequestStatus) {
		this.serviceRequestStatus = serviceRequestStatus;
	}

}
