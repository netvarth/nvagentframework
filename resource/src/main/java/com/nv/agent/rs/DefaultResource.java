/**
 * 
 */
package com.nv.agent.rs;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.sendmsg.SendMsg;
import com.nv.platform.sendmsg.SendMsg.MailType;
import com.nv.platform.sendmsg.common.MessagingException;

/**
 * @author Mani
 *
 */
@Controller
public class DefaultResource extends ServiceExceptionHandler {
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String getHome(){
		System.out.println("Im in defaultresource");
		return "index";
	}
}
