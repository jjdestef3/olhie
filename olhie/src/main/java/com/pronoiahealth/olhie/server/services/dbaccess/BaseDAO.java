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
package com.pronoiahealth.olhie.server.services.dbaccess;

import com.pronoiahealth.olhie.client.shared.vo.User;

/**
 * BaseDAO.java<br/>
 * Responsibilities:<br/>
 * 1.
 *
 * @author John DeStefano
 * @version 1.0
 * @since Oct 2, 2013
 *
 */
public interface BaseDAO {
	/**
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public User getUserByUserId(String userId) throws Exception;

}
