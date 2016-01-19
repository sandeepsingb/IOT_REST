package com.kpit.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ServiceRequestUpdateDTO")

public class ServiceRequestFetchDTO {
	private ServiceRequestDTO serviceRequestDTO;

	@XmlElement
	public ServiceRequestDTO getServiceRequestDTO() {
		return serviceRequestDTO;
	}

	public void setServiceRequestDTO(ServiceRequestDTO serviceRequestDTO) {
		this.serviceRequestDTO = serviceRequestDTO;
	}
}
