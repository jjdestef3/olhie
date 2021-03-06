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
package com.pronoiahealth.olhie.client.shared.vo;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import com.orientechnologies.orient.core.annotation.OId;
import com.orientechnologies.orient.core.annotation.OVersion;

/**
 * Book.java<br/>
 * Responsibilities:<br/>
 * 1. Book<br/>
 * 
 * @author John DeStefano
 * @version 1.0
 * @since Jun 7, 2013
 * 
 */
@Portable
@Bindable
public class Book {
	@OId
	private String id;

	@OVersion
	private Long version;

	@NotNull
	@Size(min = 1, max = 250, message = "Must be between 1 and 250 characters long.")
	private String bookTitle;

	@NotNull
	@Size(min = 10, message = "Must be at least 10 characters long.")
	private String introduction;

	@Size(max = 250)
	private String keywords;

	@NotNull
	@Size(min = 1, max = 50)
	private String category;

	@NotNull
	@Size(min = 1, max = 50)
	private String coverName;

	@NotNull
	private Date createdDate;

	@NotNull
	private Date lastUpdated;

	@NotNull
	private Boolean active = Boolean.FALSE;

	private Date solrUpdate;

	private Date actDate;

	@NotNull
	@Size(min = 6, max = 20, message = "Must be between 6 and 20 characters")
	private String authorId;

	private String base64FrontCover;
	
	private String base64SmallFrontCover;

	private String base64BackCover;

	private String base64LogoData;

	@Size(max = 250, message = "File names can only be 250 characters long.")
	private String logoFileName;

	@Size(max = 75, message = "Maximum of 75 characters.")
	private String interfacePlatform;

	@Size(max = 75, message = "Maximum of 75 characters.")
	private String interfaceSendingSystem;

	@Size(max = 75, message = "Maximum of 75 characters.")
	private String interfaceRecievingSystem;
	
	private byte[] backCoverBytes;
	
	private byte[] frontCoverBytes;
	
	private byte[] logoBytes;
	
	private byte[] smallFrontCoverBytes;

	private List<Bookassetdescription> bookDescriptions;

	/**
	 * Constructor
	 * 
	 */
	public Book() {
	}

	/**
	 * Constructor
	 *
	 * @param bookTitle
	 * @param introduction
	 * @param keywords
	 * @param category
	 * @param coverName
	 * @param interfacePlatform
	 * @param interfaceSendingSystem
	 * @param interfaceRecievingSystem
	 * @param active
	 */
	public Book(String bookTitle, String introduction, String keywords,
			String category, String coverName, String interfacePlatform,
			String interfaceSendingSystem, String interfaceRecievingSystem,
			boolean active) {
		super();
		this.bookTitle = bookTitle;
		this.introduction = introduction;
		this.keywords = keywords;
		this.category = category;
		this.coverName = coverName;
		this.interfacePlatform = interfacePlatform;
		this.interfaceRecievingSystem = interfaceRecievingSystem;
		this.interfaceSendingSystem = interfaceSendingSystem;
		this.active = active;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCoverName() {
		return coverName;
	}

	public void setCoverName(String coverName) {
		this.coverName = coverName;
	}

	public String getId() {
		return id;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public List<Bookassetdescription> getBookDescriptions() {
		return bookDescriptions;
	}

	public void setBookDescriptions(List<Bookassetdescription> bookDescriptions) {
		this.bookDescriptions = bookDescriptions;
	}

	public Date getActDate() {
		return actDate;
	}

	public void setActDate(Date actDate) {
		this.actDate = actDate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBase64LogoData() {
		return base64LogoData;
	}

	public void setBase64LogoData(String base64LogoData) {
		this.base64LogoData = base64LogoData;
	}

	public String getLogoFileName() {
		return logoFileName;
	}

	public void setLogoFileName(String logoFileName) {
		this.logoFileName = logoFileName;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Date getSolrUpdate() {
		return solrUpdate;
	}

	public void setSolrUpdate(Date solrUpdate) {
		this.solrUpdate = solrUpdate;
	}

	public String getBase64FrontCover() {
		return base64FrontCover;
	}

	public void setBase64FrontCover(String base64FrontCover) {
		this.base64FrontCover = base64FrontCover;
	}

	public String getBase64BackCover() {
		return base64BackCover;
	}

	public void setBase64BackCover(String base64BackCover) {
		this.base64BackCover = base64BackCover;
	}

	public String getInterfacePlatform() {
		return interfacePlatform;
	}

	public void setInterfacePlatform(String interfacePlatform) {
		this.interfacePlatform = interfacePlatform;
	}

	public String getInterfaceSendingSystem() {
		return interfaceSendingSystem;
	}

	public void setInterfaceSendingSystem(String interfaceSendingSystem) {
		this.interfaceSendingSystem = interfaceSendingSystem;
	}

	public String getInterfaceRecievingSystem() {
		return interfaceRecievingSystem;
	}

	public void setInterfaceRecievingSystem(String interfaceRecievingSystem) {
		this.interfaceRecievingSystem = interfaceRecievingSystem;
	}

	public String getBase64SmallFrontCover() {
		return base64SmallFrontCover;
	}

	public void setBase64SmallFrontCover(String base64SmallFrontCover) {
		this.base64SmallFrontCover = base64SmallFrontCover;
	}

	public byte[] getBackCoverBytes() {
		return backCoverBytes;
	}

	public void setBackCoverBytes(byte[] backCoverBytes) {
		this.backCoverBytes = backCoverBytes;
	}

	public byte[] getFrontCoverBytes() {
		return frontCoverBytes;
	}

	public void setFrontCoverBytes(byte[] frontCoverBytes) {
		this.frontCoverBytes = frontCoverBytes;
	}

	public byte[] getLogoBytes() {
		return logoBytes;
	}

	public void setLogoBytes(byte[] logoBytes) {
		this.logoBytes = logoBytes;
	}

	public byte[] getSmallFrontCoverBytes() {
		return smallFrontCoverBytes;
	}

	public void setSmallFrontCoverBytes(byte[] smallFrontCoverBytes) {
		this.smallFrontCoverBytes = smallFrontCoverBytes;
	}
}
