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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.pronoiahealth.olhie.client.shared.constants.SecurityRoleEnum;
import com.pronoiahealth.olhie.server.security.SecureAccess;
import com.pronoiahealth.olhie.server.security.ServerUserToken;

/**
 * SolrSearchService.java<br/>
 * Responsibilities:<br/>
 * 1. Search the SOLR service for data<br/>
 * 
 * @author Alex Roman
 * @version 1.0
 * @since Jul 16, 2013
 */
@RequestScoped
public class SolrSearchService {

	private static final String SOLR_SERVER = "184.173.121.82";
	private static final int SOLR_PORT = 8080;
	
	private static final String SOLR_QUERY_ALL = "http://" + SOLR_SERVER + ":" + SOLR_PORT + "/solr/select?q=*:*&wt=xml";
	private static final String SOLR_QUERY_KEYWORDS = "http://" + SOLR_SERVER + ":" + SOLR_PORT + "/solr/select?q=keywords:";
	private static final String SOLR_QUERY_FULL = "http://" + SOLR_SERVER + ":" + SOLR_PORT + "/solr/select?q=";

	@Inject
	private Logger log;

	@Inject
	private ServerUserToken userToken;

	/**
	 * Constructor
	 * 
	 */
	public SolrSearchService() {
	}

	/**
	 * Run the solr query and parse the results.
	 * 
	 * @param bookFindByIdEvent
	 */
	@SecureAccess({ SecurityRoleEnum.ADMIN, SecurityRoleEnum.AUTHOR,
			SecurityRoleEnum.ANONYMOUS })
	protected List<String> searchSolr(String _text) {
		List<String> idList = null;
		try {
			String resultXml = querySolrKeywords(_text);
			idList = parseResults(resultXml);
		} catch (Exception e) {
			String errMsg = e.getMessage();
			log.log(Level.SEVERE, errMsg, e);
		}
		return idList;
	}
	
	/**
	 * @return
	 */
	private String querySolrAll()
	{
		return querySolr(SOLR_QUERY_ALL);
	}
	
	/**
	 * @param _query
	 * @return
	 */
	private String querySolrKeywords(String _query)
	{
		return querySolr(SOLR_QUERY_KEYWORDS + "*" + _query + "*");
	}
	
	/**
	 * @param _query
	 * @return
	 */
	private String querySolrFull(String _query)
	{
		return querySolr(SOLR_QUERY_FULL + _query);
	}
	
	/**
	 * @param _query
	 * @return
	 */
	private String querySolr(String _query)
	{
		StringBuffer result = new StringBuffer();
		
		BufferedReader reader = null;
		InputStream is = null;
		
        try {
        	URL url = new URL(_query);
            is = url.openConnection().getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = reader.readLine()) != null) {
            	result.append(line);
            }
            reader.close();
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	try {
        		is.close();
        	} catch (Exception exception) { }
        	try {
        		reader.close();
        	} catch (Exception exception) { }
        }
        
		return result.toString();
	}
	
	/**
	 * @param _resultXML
	 * @return
	 */
	private List<String> parseResults(String _resultXML)
	{
		Pattern regex = Pattern.compile("<str name=\"id\">(.*?)</str>", Pattern.DOTALL);
		Matcher matcher = regex.matcher(_resultXML);
		List<String> results = new ArrayList<String>();
		while (matcher.find()) {
		    String id = matcher.group(1);
		    results.add(id);
		}
		
		return results;
	}
	
	/**
	 * @param args
	 */
	public static void main(String [] args)
	{
		String resultXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <response> <lst name=\"responseHeader\"><int name=\"status\">0</int><int name=\"QTime\">11</int><lst name=\"params\"><str name=\"q\">keywords:*destefano*</str></lst></lst><result name=\"response\" numFound=\"6\" start=\"0\"><doc><str name=\"id\">#15:92</str><str name=\"keywords\">TEST BOOK FOR TODAY TEST BOOK FOR TODAY'S PRESENTATION   INTERFACE GREY DESIGNED JOHN DESTEFANO JDESTEF TEST FILE FOR TODAY'S DISCUSSION QUICKSTART_GUIDE.PDF</str><long name=\"_version_\">1440800724562739200</long></doc><doc><str name=\"id\">#15:93</str><str name=\"keywords\">THIS IS A TEST BOOK THIS IS A TEST BOOK   INTERFACE BLUE-GREY 1 JOHN DESTEFANO JDESTEF</str><long name=\"_version_\">1440800724563787776</long></doc><doc><str name=\"id\">#15:94</str><str name=\"keywords\">TEST BOOK 101 THIS IS TEST BOOK 101   LEGAL BLUE-GREY 1 JOHN DESTEFANO JDESTEF</str><long name=\"_version_\">1440800724563787777</long></doc><doc><str name=\"id\">#15:95</str><str name=\"keywords\">TEST BOOK 100 THIS IS TEST BOOK 100   INTERFACE BROWN 3 JOHN DESTEFANO JDESTEF</str><long name=\"_version_\">1440800724563787778</long></doc><doc><str name=\"id\">#15:96</str><str name=\"keywords\">TEST BOOK 3 THIS IS TEST BOOK 3   INTERFACE BROWN DESIGNED JOHN DESTEFANO JDESTEF</str><long name=\"_version_\">1440800724563787779</long></doc><doc><str name=\"id\">#15:98</str><str name=\"keywords\">TEST BOOK 1 THIS IS TEST BOOK 1   LEGAL MAUVE 1 JOHN DESTEFANO JDESTEF TEST FILE 2 TEST FILE DESCRIPTION QUICKSTART_GUIDE.PDF</str><long name=\"_version_\">1440800724563787781</long></doc></result> </response>";
		SolrSearchService solrService = new SolrSearchService();
		solrService.parseResults(resultXml);
	}

	
}
