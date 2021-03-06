/*******************************************************************************
 * Copyright (c) 2013 Pronoia Health LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pronoia Health LLC - initial API and implementation
 *******************************************************************************/
package com.pronoiahealth.olhie.server.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.pronoiahealth.olhie.client.shared.constants.RequestFormAuthorApproval;
import com.pronoiahealth.olhie.client.shared.constants.SecurityRoleEnum;
import com.pronoiahealth.olhie.client.shared.events.admin.AddNewsItemEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.AuthorPendingRequestEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.AuthorPendingResponseEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.AuthorRequestStatusChangeEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.FindUserByLastNameRequestEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.FindUserByLastNameResponseEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.NewsItemsRequestEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.NewsItemsResponseEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.RemoveNewsItemRequestEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.UpdateNewsItemActiveRequestEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.UserChangeRoleEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.UserEmailChangeRequestEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.UserOrganizationChangeRequestEvent;
import com.pronoiahealth.olhie.client.shared.events.admin.UserResetPWEvent;
import com.pronoiahealth.olhie.client.shared.events.errors.ServiceErrorEvent;
import com.pronoiahealth.olhie.client.shared.vo.NewsItem;
import com.pronoiahealth.olhie.client.shared.vo.RegistrationForm;
import com.pronoiahealth.olhie.client.shared.vo.User;
import com.pronoiahealth.olhie.server.dataaccess.DAO;
import com.pronoiahealth.olhie.server.security.SecureAccess;
import com.pronoiahealth.olhie.server.security.ServerUserToken;
import com.pronoiahealth.olhie.server.services.dbaccess.NewsItemDAO;
import com.pronoiahealth.olhie.server.services.dbaccess.RegistrationFormDAO;
import com.pronoiahealth.olhie.server.services.dbaccess.UserDAO;

/**
 * AdminService.java<br/>
 * Responsibilities:<br/>
 * 1.
 * 
 * @author John DeStefano
 * @version 1.0
 * @since Jan 25, 2014
 * 
 */
@RequestScoped
public class AdminService {
	@Inject
	private Logger log;

	@Inject
	private ServerUserToken userToken;

	@Inject
	private Event<ServiceErrorEvent> serviceErrorEvent;

	@Inject
	private Event<AuthorPendingResponseEvent> authorPendingResponseEvent;

	@Inject
	private Event<FindUserByLastNameResponseEvent> findUserByLastNameResponseEvent;

	@Inject
	private Event<NewsItemsResponseEvent> newsItemsResponseEvent;

	@Inject
	@DAO
	private RegistrationFormDAO regDao;

	@Inject
	@DAO
	private UserDAO userDao;

	@Inject
	@DAO
	private NewsItemDAO newsDao;

	/**
	 * Constructor
	 * 
	 */
	public AdminService() {
	}

	/**
	 * Receives AuthorPendingRequestEvent
	 * 
	 * @param authorPendingRequestEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN })
	protected void observersAuthorPendingRequestEvent(
			@Observes AuthorPendingRequestEvent authorPendingRequestEvent) {
		try {
			// Get list
			List<RegistrationForm> forms = regDao.findAuthorPendingForms(true);

			// Fire return event
			authorPendingResponseEvent.fire(new AuthorPendingResponseEvent(
					forms));
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}
	}

	/**
	 * Make changes based on the Admin chaning the authStatus of a user from the
	 * AuthorRequestWidget
	 * 
	 * @param authorRequestStatusChangeEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN })
	protected void observersAuthorRequestStatusChangeEvent(
			@Observes AuthorRequestStatusChangeEvent authorRequestStatusChangeEvent) {
		try {
			String adminId = userToken.getUserId();
			String reqId = authorRequestStatusChangeEvent.getReqId();
			String newStatus = authorRequestStatusChangeEvent.getNewStatus();

			// Get current Request by id
			RegistrationForm form = regDao.findRegistrationFormById(reqId);

			// Is the status different?, if so then update it
			if (!form.getAuthorStatus().equals(newStatus)) {
				boolean acceptReject = false;
				if (newStatus.equals(RequestFormAuthorApproval.APPROVED
						.toString())) {
					acceptReject = true;
				}
				regDao.acceptRejectAuthorApplication(form.getId(), adminId,
						acceptReject, newStatus, true);
			}

		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}
	}

	/**
	 * Takes request from the UserManagement admin screen
	 * 
	 * @param userChangeRoleEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN })
	protected void observersUserChangeRoleEvent(
			@Observes UserChangeRoleEvent userChangeRoleEvent) {
		try {
			String userId = userChangeRoleEvent.getUserId();
			String newRole = userChangeRoleEvent.getNewRole();
			userDao.updateUserRole(userId, SecurityRoleEnum.valueOf(newRole));
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}
	}

	/**
	 * Takes request from the UserManagement admin screen
	 * 
	 * @param userResetPWEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN })
	protected void observersUserResetPWEvent(
			@Observes UserResetPWEvent userResetPWEvent) {
		try {
			String userId = userResetPWEvent.getUserId();
			boolean newResetPw = userResetPWEvent.isReset();
			userDao.updateUserResetPw(userId, newResetPw);
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}
	}

	/**
	 * 
	 * 
	 * @param findUserByLastNameRequestEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN })
	protected void observersFindUserByLastNameRequestEvent(
			@Observes FindUserByLastNameRequestEvent findUserByLastNameRequestEvent) {
		try {
			String lastName = findUserByLastNameRequestEvent.getLastNameQry();
			List<User> users = userDao.findUserByLastName(lastName, true);
			findUserByLastNameResponseEvent
					.fire(new FindUserByLastNameResponseEvent(users));
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}
	}

	/**
	 * @param userEmailChangeRequestEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN })
	protected void observersUserEmailChangeRequestEvent(
			@Observes UserEmailChangeRequestEvent userEmailChangeRequestEvent) {
		try {
			String eMail = userEmailChangeRequestEvent.getEmail();
			String userId = userEmailChangeRequestEvent.getUserId();
			userDao.updateUserEmail(userId, eMail);
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}
	}

	/**
	 * @param userOrganizationChangeRequestEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN })
	protected void observersUserOrganizationChangeRequestEvent(
			@Observes UserOrganizationChangeRequestEvent userOrganizationChangeRequestEvent) {
		try {
			String org = userOrganizationChangeRequestEvent.getOrganization();
			String userId = userOrganizationChangeRequestEvent.getUserId();
			userDao.updateOrganization(userId, org);
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}
	}

	/**
	 * @param addNewsItemEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN })
	protected void observersAddNewsItemEvent(
			@Observes AddNewsItemEvent addNewsItemEvent) {
		try {
			NewsItem item = addNewsItemEvent.getNewsItem();
			newsDao.addNewsItems(item);
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}
	}

	/**
	 * @param addNewsItemEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN })
	protected void observersRemoveNewsItemEvent(
			@Observes RemoveNewsItemRequestEvent removeNewsItemEvent) {
		try {
			String removeId = removeNewsItemEvent.getNewsItemId();
			newsDao.removeNewsItems(removeId);
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}
	}

	/**
	 * @param newsItemsRequestEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN })
	protected void observersNewsItemsRequestEvent(
			@Observes NewsItemsRequestEvent newsItemsRequestEvent) {
		try {
			List<NewsItem> items = newsDao.getAllNewsItems();
			newsItemsResponseEvent.fire(new NewsItemsResponseEvent(items));
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}
	}

	/**
	 * @param updateNewsItemActiveRequestEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN })
	protected void observersUpdateNewsItemActiveRequestEvent(
			@Observes UpdateNewsItemActiveRequestEvent updateNewsItemActiveRequestEvent) {
		try {
			String newsItemId = updateNewsItemActiveRequestEvent
					.getNewsItemId();
			boolean activeStatus = updateNewsItemActiveRequestEvent.isActive();
			newsDao.updateNewsItemsActiveStatus(newsItemId, activeStatus);
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}
	}
}
