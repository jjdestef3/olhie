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

import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.pronoiahealth.olhie.client.shared.constants.SecurityRoleEnum;
import com.pronoiahealth.olhie.client.shared.constants.UserBookRelationshipEnum;
import com.pronoiahealth.olhie.client.shared.events.book.BookFindResponseEvent;
import com.pronoiahealth.olhie.client.shared.events.book.FindAuthorsBookByIdEvent;
import com.pronoiahealth.olhie.client.shared.events.errors.ServiceErrorEvent;
import com.pronoiahealth.olhie.client.shared.vo.BookDisplay;
import com.pronoiahealth.olhie.server.dataaccess.DAO;
import com.pronoiahealth.olhie.server.security.SecureAccess;
import com.pronoiahealth.olhie.server.security.ServerUserToken;
import com.pronoiahealth.olhie.server.services.dbaccess.BookDAO;

/**
 * BookFindService.java<br/>
 * Responsibilities:<br/>
 * 1. Use to return a book based on the books id<br/>
 * 
 * <p>
 * This class will enforce the rule that if the book is not yet published it may
 * only be returned to the creator of the book or a co-author.
 * </p>
 * 
 * @author John DeStefano
 * @version 1.0
 * @since Jun 8, 2013
 * 
 */
@RequestScoped
public class BookFindService {
	@Inject
	private Logger log;

	@Inject
	private ServerUserToken userToken;

	@Inject
	private Event<BookFindResponseEvent> bookFindResponseEvent;

	@Inject
	private Event<ServiceErrorEvent> serviceErrorEvent;

	@Inject
	private TempThemeHolder holder;

	@Inject
	@DAO
	private BookDAO bookDAO;

	/**
	 * Constructor
	 * 
	 */
	public BookFindService() {
	}

	/**
	 * Finds a book and then fires a response with the found book. "Finding" a
	 * book will bring it into view for the user. This means the
	 * UserBookRelationship's lastViewedDT must be updated. This method will
	 * return a servi e exception if the loggedin user is not the author or
	 * co-author of the book.
	 * 
	 * @param bookFindByIdEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN, SecurityRoleEnum.AUTHOR, SecurityRoleEnum.REGISTERED })
	protected void observesBookNewFindByIdEvent(
			@Observes FindAuthorsBookByIdEvent bookFindByIdEvent) {
		try {
			String bookId = bookFindByIdEvent.getBookId();
			String userId = userToken.getUserId();

			// Get the book display
			BookDisplay bookDisplay = bookDAO.getBookDisplayById(bookId,
					userId, holder, true);

			// Get the user relations
			Set<UserBookRelationshipEnum> rels = bookDAO
					.getActiveBookRealtionshipForUser(userId,
							userToken.getLoggedIn(), bookId);

			// The logged in user must be the author or co-author of the book
			if (!(rels.contains(UserBookRelationshipEnum.CREATOR) == true)
					&& !(rels.contains(UserBookRelationshipEnum.COAUTHOR) == true)) {
				throw new Exception(
						"Must be the book author or co-author to edit it!");
			}

			// Update the last viewed date on the user book relationship
			bookDAO.setLastViewedOnUserBookRelationship(userId, bookId,
					new Date());

			// Is the user asking for the book the author or co-author
			boolean authorSelected = bookDAO.isAuthorSelected(userId, bookId,
					rels);

			// Fire the event
			bookFindResponseEvent.fire(new BookFindResponseEvent(bookDisplay,
					rels, authorSelected));
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
			serviceErrorEvent.fire(new ServiceErrorEvent(errMsg));
		}

	}

}
