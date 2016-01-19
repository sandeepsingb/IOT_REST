package com.kpit.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the wrapper DTO. Used to add root element to JSON string
 * 
 */
@XmlRootElement(name = "ServiceRequestsParent")
public class ServiceRequestParentDTO {
	private List<ServiceRequestDTO> serviceRequestDTO;

	@XmlElement
	public List<ServiceRequestDTO> getServiceRequestDTO() {
		return serviceRequestDTO;
	}

	public void setServiceRequestDTO(List<ServiceRequestDTO> serviceRequestDTO) {
		this.serviceRequestDTO = serviceRequestDTO;
	}

}
