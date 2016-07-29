package com.nv.agent.rs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.nv.platform.base.service.BaseServiceException;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Joshi Manjila
 *
 */
public class ServiceExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ServiceExceptionHandler.class);

	/**
	 * @param se
	 * @return errorMessage
	 */
	@ExceptionHandler(BaseServiceException.class)
	@ResponseBody String handleBadRequest(HttpServletResponse response, BaseServiceException ex) {
		response.setStatus(ex.getStatus().getStatusCode());
		return  ex.getMessage();
	}
	
	


}
