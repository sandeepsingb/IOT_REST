package com.kpit.dto;

import java.sql.Time;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This DTO represent the SERVICEREQUEST TABLE in database
 * 
 * @author ROHANL1
 * 
 */
@XmlRootElement(name = "ServiceRequests")
public class ServiceRequestDTO {
	private String code;
	private String desc;
	private String priority;
	private String status;
	private String faultCode;
	private String faultDesc;
	private Date raiseDate;
	private String genSetName;
	private String genSetModel;
	private String genSetSerial;
	private String genSetSoftVer;
	private String genSetLoc;
	private String actualResolution;
	private Date aptDate;
	private Date dateFrom;
	private Date dateTo;
	private Time aptTime;
	private String aptSlot;
	
	@XmlElement
	public Date getAptDate() {
		return aptDate;
	}
 
	public void setAptDate(Date aptDate) {
		this.aptDate = aptDate;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	
	@XmlElement
	public Time getAptTime() {
		return aptTime;
	}

	public void setAptTime(Time aptTime) {
		this.aptTime = aptTime;
	}

	
	@XmlElement
	public String getActualResolution() {
		return actualResolution;
	}

	public void setActualResolution(String actualResolution) {
		this.actualResolution = actualResolution;
	}

	@XmlElement
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@XmlElement
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@XmlElement
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@XmlElement
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlElement
	public String getFaultCode() {
		return faultCode;
	}

	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}

	@XmlElement
	public String getFaultDesc() {
		return faultDesc;
	}

	public void setFaultDesc(String faultDesc) {
		this.faultDesc = faultDesc;
	}

	@XmlElement
	public Date getRaiseDate() {
		return raiseDate;
	}

	public void setRaiseDate(Date raiseDate) {
		this.raiseDate = raiseDate;
	}

	@XmlElement
	public String getGenSetName() {
		return genSetName;
	}

	public void setGenSetName(String genSetName) {
		this.genSetName = genSetName;
	}

	@XmlElement
	public String getGenSetModel() {
		return genSetModel;
	}

	public void setGenSetModel(String genSetModel) {
		this.genSetModel = genSetModel;
	}

	@XmlElement
	public String getGenSetSerial() {
		return genSetSerial;
	}

	public void setGenSetSerial(String genSetSerial) {
		this.genSetSerial = genSetSerial;
	}

	@XmlElement
	public String getGenSetSoftVer() {
		return genSetSoftVer;
	}

	public void setGenSetSoftVer(String genSetSoftVer) {
		this.genSetSoftVer = genSetSoftVer;
	}

	@XmlElement
	public String getGenSetLoc() {
		return genSetLoc;
	}

	public void setGenSetLoc(String genSetLoc) {
		this.genSetLoc = genSetLoc;
	}
	
	@XmlElement
	public String getAptSlot() {
		return aptSlot;
	}

	public void setAptSlot(String aptSlot) {
		this.aptSlot = aptSlot;
	}
}
