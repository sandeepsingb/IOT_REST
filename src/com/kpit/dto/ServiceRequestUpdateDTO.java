package com.kpit.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the parent DTO for Status DTO.
 * 
 * @author ROHANL1
 * 
 */
@XmlRootElement(name = "ServiceRequestUpdateDTO")
public class ServiceRequestUpdateDTO {
	private ServiceRequestDTO serviceRequestDTO;

	@XmlElement
	public ServiceRequestDTO getServiceRequestDTO() {
		return serviceRequestDTO;
	}

	public void setServiceRequestDTO(ServiceRequestDTO serviceRequestDTO) {
		this.serviceRequestDTO = serviceRequestDTO;
	}
}
