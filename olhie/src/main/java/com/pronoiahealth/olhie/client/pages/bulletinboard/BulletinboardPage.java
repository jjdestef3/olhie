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
package com.pronoiahealth.olhie.client.pages.bulletinboard;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.pronoiahealth.olhie.client.pages.AbstractPage;
import com.pronoiahealth.olhie.client.pages.bulletinboard.widgets.CurrentStatusWidget;
import com.pronoiahealth.olhie.client.widgets.FlowPanelWithSpacer;

/**
 * BulletinboardPage.java<br/>
 * Responsibilities:<br/>
 * 1. Serves as default page in Errai Navigation system<br/>
 * 
 * @author John DeStefano
 * @version 1.0
 * @since May 26, 2013
 * 
 */
@Templated("#root")
@Page(role = { DefaultPage.class })
public class BulletinboardPage extends AbstractPage {

	@DataField
	private FlowPanelWithSpacer col1 = new FlowPanelWithSpacer();

	@DataField
	private FlowPanelWithSpacer col2 = new FlowPanelWithSpacer();
	
	@Inject
	private CurrentStatusWidget statusWidget;

	public BulletinboardPage() {
	}

	/**
	 * Inject the custom resources and create the main gui via uiBinder
	 */
	@PostConstruct
	private void postConstruct() {
		col1.add(statusWidget);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		setPageBackgroundClass("ph-BulletinBoard-Background");
	}
}
