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
package com.pronoiahealth.olhie.client.navigation;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.Navigation;
import org.jboss.errai.ui.nav.client.local.TransitionTo;

import com.google.common.collect.Multimap;
import com.google.gwt.user.client.ui.Widget;
import com.pronoiahealth.olhie.client.clientfactories.DefaultAppPage;
import com.pronoiahealth.olhie.client.clientfactories.PageRoleMap;
import com.pronoiahealth.olhie.client.pages.bookcase.BookCasePage;
import com.pronoiahealth.olhie.client.pages.bookreview.BookReviewPage;
import com.pronoiahealth.olhie.client.pages.bulletinboard.BulletinboardPage;
import com.pronoiahealth.olhie.client.pages.search.SearchPage;
import com.pronoiahealth.olhie.client.shared.constants.NavEnum;
import com.pronoiahealth.olhie.client.shared.constants.SecurityRoleEnum;
import com.pronoiahealth.olhie.client.shared.events.local.ShowCommentsModalEvent;
import com.pronoiahealth.olhie.client.shared.events.local.ShowLoginModalEvent;
import com.pronoiahealth.olhie.client.shared.events.local.ShowRegisterModalEvent;
import com.pronoiahealth.olhie.client.shared.vo.ClientUserToken;

/**
 * Navigator <br/>
 * Responsibilities:<br/>
 * 1. Handle "Page" navigations<br/>
 * 2. Shows Login, Comments, and Register dialogs<br/>
 * 
 * @author John DeStefano
 * @version 1.0
 * @since May 26, 2013
 * 
 */
@ApplicationScoped
public class Navigator {

	@Inject
	private Navigation nav;

	@Inject
	@PageRoleMap
	private Map<String, Set<String>> pageRolesMap;

	@Inject
	@DefaultAppPage
	private String defAppPageName;

	@Inject
	private ClientUserToken clientUserToken;

	@Inject
	private TransitionTo<BulletinboardPage> showBulletinboardPage;

	@Inject
	private TransitionTo<BookCasePage> showBookCasePage;

	@Inject
	private TransitionTo<SearchPage> showSearchPage;

	@Inject
	private TransitionTo<BookReviewPage> showBookReviewPage;

	@Inject
	private Event<ShowLoginModalEvent> showLoginEvent;

	@Inject
	private Event<ShowRegisterModalEvent> showRegisterEvent;

	@Inject
	private Event<ShowCommentsModalEvent> showCommentsEvent;

	// private String defPageName;

	@Inject
	public Navigator() {
	}

	public Widget getNavContentPanel() {
		return nav.getContentPanel();
	}

	/**
	 * Creates the pageRolesMap. This will be used in the @PageShown annotated
	 * method to test that the page should be made visible for this user with a
	 * specific role.
	 */
	@PostConstruct
	// TODO: Need to find a better way to do this
	private void initPageRolesMap() {
	}

	public void performTransition(String navToPage, Multimap state) {
		NavEnum page = NavEnum.valueOf(navToPage);
		switch (page) {
		case BulletinboardPage: {
			if (state == null) {
				showBulletinboardPage.go();
			} else {
				showBulletinboardPage.go(state);
			}
			break;
		}

		case SearchPage: {
			if (state == null) {
				showSearchPage.go();
			} else {
				showSearchPage.go(state);
			}
			break;
		}

		case BookCasePage: {
			if (state == null) {
				showBookCasePage.go();
			} else {
				showBookCasePage.go(state);
			}
			break;
		}

		case BookReviewPage: {
			if (state == null) {
				showBookReviewPage.go();
			} else {
				showBookReviewPage.go(state);
			}
			break;
		}
		}
	}

	/**
	 * Shows the page with the role DefaultPage.class
	 */
	public void showDefaultPage() {
		nav.goTo(defAppPageName);
	}

	public void showLoginModalEvent() {
		showLoginEvent.fire(new ShowLoginModalEvent());
	}

	public void showCommentsModalEvent() {
		showCommentsEvent.fire(new ShowCommentsModalEvent());
	}

	public void showRegisterModalEvent() {
		showRegisterEvent.fire(new ShowRegisterModalEvent());
	}

	/**
	 * Based on the current users role and the role of the page return a boolean
	 * value. If the role is valid for the page return true other wise false.
	 * Important - This method should only be called in the @PageShown annotated
	 * method to assure the current page is the one your testing.
	 * <p>
	 * This might be a brittle method to allow showing pages. Look into other
	 * methods
	 * </p>
	 * 
	 * @return
	 */
	public boolean allowPageFromShownMethod() {
		String currentPageName = nav.getCurrentPage().name();

		// If trying to show the default page go for it
		//if (currentPageName.equalsIgnoreCase(defAppPageName)) {
		//	return true;
		//}

		if (clientUserToken.isLoggedIn()) {
			return pageRolesMap.get(
					SecurityRoleEnum.valueOf(clientUserToken.getRole())
							.getName()).contains(currentPageName);
		} //else if (pageRolesMap == null || pageRolesMap.size() == 0) {
			// By pass check when the map is null or has not roles it it
			//return false;
		 else {
			// Only show anonymous pages
			return pageRolesMap.get(SecurityRoleEnum.ANONYMOUS.getName())
					.contains(currentPageName);
		}
	}

	/**
	 * Get the default page name
	 * 
	 * @return
	 */
	// public String getDefaultPageName() {
	// if (defPageName == null || defPageName.length() == 0) {
	// defPageName = nav.getPagesByRole(DefaultPage.class).iterator()
	// .next().name();
	// }
	// return defPageName;
	// }
}
