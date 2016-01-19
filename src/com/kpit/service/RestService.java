package com.kpit.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.kpit.dto.ServiceRequestDTO;
import com.kpit.dto.ServiceRequestParentDTO;
import com.kpit.dto.ServiceRequestPartentStatus;
import com.kpit.dto.ServiceRequestStatus;
import com.kpit.dto.ServiceRequestUpdateDTO;

/**
 * This is the rest service exposing three methods.
 * 1. to get the SR count
 * 2. to get the SR details
 * 3. to Update SR status
 * It uses JDBC to fetch and insert the data in mysql database
 * @author ROHANL1
 *
 */
@Path("/rest")
public class RestService {

	@GET
	@Path("/getServiceRequestCount")
	@Produces("application/json")
	public ServiceRequestPartentStatus getServiceRequestCount() {

		ServiceRequestPartentStatus parent = new ServiceRequestPartentStatus();
		parent.setServiceRequestStatus(getSRStatus());

		return parent;
	}

	@GET
	@Path("/getAllServiceRequests")
	@Produces("application/json")
	public ServiceRequestParentDTO getAllServiceRequests() {
		ServiceRequestParentDTO serviceRequestParentDTO = new ServiceRequestParentDTO();
		serviceRequestParentDTO.setServiceRequestDTO(fetchAllServiceRequest());

		return serviceRequestParentDTO;
	}

	@POST
	@Path("/updateSRStatus")
	@Consumes("application/json")
	@Produces("application/json")
	public String updateSRStatus(ServiceRequestUpdateDTO serviceRequestDTO) {

		Integer i = updateSR(serviceRequestDTO);
		if (i != 0) {
			return "success";
		} else {
			return "update failed";
		}

	}
	
	@GET
	@Path("/getSearchResult")
	@Produces("application/json")

	public ServiceRequestParentDTO getSearchResult() {

		ServiceRequestParentDTO serviceRequestParentDTO = new ServiceRequestParentDTO();
		serviceRequestParentDTO.setServiceRequestDTO(FetchSearchData());

		return serviceRequestParentDTO;
	}
	
	@GET
	@Path("/getSearchResultForweek")
	@Produces("application/json")

	public ServiceRequestParentDTO getSearchResultForWeek() {

		ServiceRequestParentDTO serviceRequestParentDTO = new ServiceRequestParentDTO();
		serviceRequestParentDTO.setServiceRequestDTO(FetchSearchDataForWeek());

		return serviceRequestParentDTO;
	}
	private Integer updateSR(ServiceRequestUpdateDTO serviceRequestDTO) {

		if (serviceRequestDTO.getServiceRequestDTO().getStatus() == null || serviceRequestDTO.getServiceRequestDTO().getCode() == null) {
			return 0;
		}
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		Integer rowsAffected = null;
		StringBuffer sb = new StringBuffer();
		List<ServiceRequestDTO> ServiceRequestList = new ArrayList<ServiceRequestDTO>();
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/kpit");
			conn = ds.getConnection();
			String query = "UPDATE SERVICEREQUEST SET SRSTATUS=?,ACTUALRESOLUTION=? WHERE SRCODE=?";

			st = conn.prepareStatement(query);
			st.setString(1, serviceRequestDTO.getServiceRequestDTO().getStatus());
			st.setString(3, serviceRequestDTO.getServiceRequestDTO().getCode());
			st.setString(2, serviceRequestDTO.getServiceRequestDTO().getActualResolution());
			rowsAffected = st.executeUpdate();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rowsAffected;
	}

	private List<ServiceRequestDTO> fetchAllServiceRequest() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		List<ServiceRequestDTO> ServiceRequestList = new ArrayList<ServiceRequestDTO>();
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/kpit");
			conn = ds.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT SRCODE, SRDESC, SRPRIORITY, SRSTATUS, FAULTCODE, FAULTDESC, RAISEDDATE, GENSETNAME, GENSETMODEL, GENSETSERIAL, GENSETSOFTVER, GENSETLOC, ACTUALRESOLUTION FROM SERVICEREQUEST WHERE 1");

			while (rs.next()) {
				ServiceRequestDTO s1 = new ServiceRequestDTO();
				s1.setCode(rs.getString("SRCODE"));
				s1.setDesc(rs.getString("SRDESC"));
				s1.setPriority(rs.getString("SRPRIORITY"));
				s1.setStatus(rs.getString("SRSTATUS"));
				s1.setFaultCode(rs.getString("FAULTCODE"));
				s1.setFaultDesc(rs.getString("FAULTDESC"));
				s1.setRaiseDate(rs.getDate("RAISEDDATE"));

				s1.setGenSetName(rs.getString("GENSETNAME"));
				s1.setGenSetModel(rs.getString("GENSETMODEL"));

				s1.setGenSetSerial(rs.getString("GENSETSERIAL"));
				s1.setGenSetSoftVer(rs.getString("GENSETSOFTVER"));
				s1.setGenSetLoc(rs.getString("GENSETLOC"));
				s1.setActualResolution(rs.getString("ACTUALRESOLUTION"));
				
				ServiceRequestList.add(s1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ServiceRequestList;
	}

	private List<ServiceRequestStatus> getSRStatus() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		StringBuffer sb = new StringBuffer();
		List<ServiceRequestStatus> serviceRequestStatus = new ArrayList<ServiceRequestStatus>();
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/kpit");
			conn = ds.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT COUNT(SRPRIORITY) AS CNT,SRPRIORITY FROM SERVICEREQUEST WHERE UPPER(SRSTATUS) <> 'CLOSED' GROUP BY SRPRIORITY");

			while (rs.next()) {
				ServiceRequestStatus s1 = new ServiceRequestStatus();

				s1.setCount(rs.getInt("CNT"));
				s1.setStatus(rs.getString("SRPRIORITY"));
				serviceRequestStatus.add(s1);
			}
			//For STATUS
			

			rs1 = st.executeQuery("SELECT COUNT(SRSTATUS) AS CNT,SRSTATUS FROM SERVICEREQUEST GROUP BY SRSTATUS");

			while (rs1.next()) {
				ServiceRequestStatus s1 = new ServiceRequestStatus();

				s1.setCount(rs1.getInt("CNT"));
				s1.setStatus(rs1.getString("SRSTATUS"));
				serviceRequestStatus.add(s1);
			}
			
			
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if(rs1!=null)
					rs1.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return serviceRequestStatus;
	}

		private List<ServiceRequestDTO> FetchSearchData() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		List<ServiceRequestDTO> ServiceRequestList = new ArrayList<ServiceRequestDTO>();
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/kpit");
			conn = ds.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT SRCODE, SRDESC, SRPRIORITY, SRSTATUS, FAULTCODE, FAULTDESC, GENSETNAME, GENSETMODEL, GENSETSERIAL, GENSETSOFTVER, GENSETLOC, ACTUALRESOLUTION FROM SERVICEREQUEST WHERE RAISEDDATE=(select t CURRENT_DATE from dual)");

			while (rs.next()) {
				ServiceRequestDTO s1 = new ServiceRequestDTO();
				s1.setCode(rs.getString("SRCODE"));
				s1.setDesc(rs.getString("SRDESC"));
				s1.setPriority(rs.getString("SRPRIORITY"));
				s1.setStatus(rs.getString("SRSTATUS"));
				s1.setFaultCode(rs.getString("FAULTCODE"));
				s1.setFaultDesc(rs.getString("FAULTDESC"));
				//s1.setRaiseDate(rs.getDate("RAISEDDATE"));

				s1.setGenSetName(rs.getString("GENSETNAME"));
				s1.setGenSetModel(rs.getString("GENSETMODEL"));

				s1.setGenSetSerial(rs.getString("GENSETSERIAL"));
				s1.setGenSetSoftVer(rs.getString("GENSETSOFTVER"));
				s1.setGenSetLoc(rs.getString("GENSETLOC"));
				s1.setActualResolution(rs.getString("ACTUALRESOLUTION"));
				
				ServiceRequestList.add(s1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ServiceRequestList;
	}

		private List<ServiceRequestDTO> FetchSearchDataForWeek() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		List<ServiceRequestDTO> ServiceRequestList = new ArrayList<ServiceRequestDTO>();
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/kpit");
			conn = ds.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT APTDATE,SRCODE, SRDESC, SRPRIORITY, SRSTATUS, FAULTCODE, FAULTDESC, GENSETNAME, GENSETMODEL, GENSETSERIAL, GENSETSOFTVER, GENSETLOC, ACTUALRESOLUTION FROM SERVICEREQUEST WHERE APTDATE between (SELECT CURRENT_DATE FROM DUAL) AND (SELECT CURRENT_DATE+5 FROM DUAL)");

			while (rs.next()) {
				ServiceRequestDTO s1 = new ServiceRequestDTO();
				s1.setCode(rs.getString("SRCODE"));
				s1.setDesc(rs.getString("SRDESC"));
				s1.setPriority(rs.getString("SRPRIORITY"));
				s1.setStatus(rs.getString("SRSTATUS"));
				s1.setFaultCode(rs.getString("FAULTCODE"));
				s1.setFaultDesc(rs.getString("FAULTDESC"));
				//s1.setRaiseDate(rs.getDate("RAISEDDATE"));

				s1.setGenSetName(rs.getString("GENSETNAME"));
				s1.setGenSetModel(rs.getString("GENSETMODEL"));

				s1.setGenSetSerial(rs.getString("GENSETSERIAL"));
				s1.setGenSetSoftVer(rs.getString("GENSETSOFTVER"));
				s1.setGenSetLoc(rs.getString("GENSETLOC"));
				s1.setActualResolution(rs.getString("ACTUALRESOLUTION"));
				
				ServiceRequestList.add(s1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ServiceRequestList;
	}


}
