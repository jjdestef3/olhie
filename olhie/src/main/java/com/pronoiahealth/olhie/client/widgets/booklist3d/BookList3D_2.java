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
package com.pronoiahealth.olhie.client.widgets.booklist3d;

import static com.google.gwt.query.client.GQuery.$;

import java.util.List;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.css.CSS;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.pronoiahealth.olhie.client.shared.vo.Book;
import com.pronoiahealth.olhie.client.shared.vo.BookDisplay;
import com.pronoiahealth.olhie.client.shared.vo.Bookassetdescription;
import com.pronoiahealth.olhie.client.utils.Utils;
import com.pronoiahealth.olhie.client.widgets.rating.StarRating;

/**
 * BookList3D.java<br/>
 * Responsibilities:<br/>
 * 1.
 * 
 * @author John DeStefano
 * @version 1.0
 * @since Jun 29, 2013
 * 
 */
public class BookList3D_2 extends Widget {
	private BookSelectCallBack bookSelectCallBack;

	/**
	 * Some components that use this class will be ApplicationScoped. We don't
	 * want to do a re-bind of gQuery events on every call to this methods
	 * onLoad handler. We only want to do it once.
	 */
	private boolean hasBeenAttached = false;

	/**
	 * Root element
	 */
	private DivElement rootDiv;

	/**
	 * Root of list
	 */
	private UListElement ulElem;

	private int currentBookCnt;

	public BookList3D_2(List<BookDisplay> books,
			BookSelectCallBack bookSelectCallBack) {
		super();
		this.bookSelectCallBack = bookSelectCallBack;

		// Create root div
		Document doc = Document.get();
		this.rootDiv = doc.createDivElement();
		this.setElement(rootDiv);

		// There seems to be a bug in the current versions of Firefox when using
		// transformations.
		// The container with the transformed elements needs to have the opacity
		// set to prevent the transformed elements from showing outside a scroll
		// area
		rootDiv.setAttribute("style", "opacity: 0.99;");

		// Create the list
		this.ulElem = doc.createULElement();
		rootDiv.appendChild(ulElem);
		ulElem.setId("bk-list");
		ulElem.setClassName("bk-list clearfix");
		for (BookDisplay bookDisplay : books) {
			addBookDisplay(bookDisplay);
		}

		// Attach the vents to the books in the list
		attachEventsToLst();
	}

	/**
	 * Appends a book to the list
	 * 
	 * @param bookDisplay
	 */
	public DivElement addBookDisplay(BookDisplay bookDisplay) {
		Book book = bookDisplay.getBook();
		String catColor = bookDisplay.getBookCategory().getColor();
		String catColorTxt = bookDisplay.getBookCategory().getTextColor();

		// Document
		Document doc = Document.get();

		// Add the list element
		LIElement li = doc.createLIElement();
		this.ulElem.appendChild(li);

		// Add the main book div
		String bookId = book.getId();
		DivElement bookDiv = doc.createDivElement();
		li.appendChild(bookDiv);
		bookDiv.setClassName("bk-book book-1 bk-bookdefault");
		// Set the book id
		bookDiv.setAttribute("bookId", bookId);

		// Add the front and cover
		DivElement bookFrontDiv = doc.createDivElement();
		bookDiv.appendChild(bookFrontDiv);
		bookFrontDiv.setClassName("bk-front");

		// Now the cover
		DivElement bookCoverDiv = doc.createDivElement();
		bookFrontDiv.appendChild(bookCoverDiv);
		bookCoverDiv.setClassName("bk-cover");

		// Set the book cover image
		bookCoverDiv
				.setAttribute(
						"style",
						"background-image: url('"
								+ Utils.buildRestServiceForBookFrontCoverDownloadLink(bookId)
								+ "'); background-repeat: no-repeat; background-size: contain; overflow:auto;");

		// Back of cover
		DivElement bookBackCoverkDiv = doc.createDivElement();
		bookFrontDiv.appendChild(bookBackCoverkDiv);
		bookBackCoverkDiv.setClassName("bk-cover-back");
		bookBackCoverkDiv.setAttribute("style", "background-color: " + catColor
				+ ";");

		// Add the pages to the book
		DivElement bookPages = doc.createDivElement();
		bookDiv.appendChild(bookPages);
		bookPages.addClassName("bk-page");
		// boolean bookCurrentPageSet = false;
		List<Bookassetdescription> descs = bookDisplay
				.getBookAssetDescriptions();

		// Create a TOC and make it the current page
		DivElement bookContentkTOCDiv = doc.createDivElement();
		bookPages.appendChild(bookContentkTOCDiv);
		bookContentkTOCDiv.setClassName("bk-content bk-content-current bk-toc");
		DivElement bookContentTOCTitleDiv = doc.createDivElement();
		bookContentkTOCDiv.appendChild(bookContentTOCTitleDiv);
		bookContentTOCTitleDiv.setAttribute("style", "text-align: center;");
		bookContentTOCTitleDiv
				.setInnerHTML("<span style='text-decoration: underline; font-weight: bold;'>TOC</span>");

		// Create the pages
		if (descs != null && descs.size() > 0) {
			int counter = 0;
			for (Bookassetdescription desc : descs) {
				DivElement bookContentkDiv = doc.createDivElement();
				bookPages.appendChild(bookContentkDiv);
				bookContentkDiv.setClassName("bk-content");

				// Add to the Page
				String descStr = desc.getDescription();
				ParagraphElement pageContentElem = doc.createPElement();
				bookContentkDiv.appendChild(pageContentElem);
				pageContentElem.setInnerText(descStr);

				// Add buttons to page
				// Depends on bootstrap css being available, example:
				// <div class="btn-group">
				// <a id="gwt-uid-97" class="btn btn-mini btn-info"
				// href="javascript:;" bookassetid="#11:6"
				// data-original-title="Download" title="">
				// <i class="icon-cloud-download"></i>
				// </a>
				// <a id="gwt-uid-98" class="btn btn-mini btn-success"
				// href="javascript:;" bookassetid="#11:6" viewtype="HTML"
				// data-original-title="View item." title="">
				// <i class="icon-eye-open"></i>
				// </a>
				// </div>
				ParagraphElement pageButtonContainerElem = doc.createPElement();
				bookContentkDiv.appendChild(pageButtonContainerElem);
				DivElement buttonBarDiv = doc.createDivElement();
				pageButtonContainerElem.appendChild(buttonBarDiv);
				buttonBarDiv.setClassName("btn-group");

				// Downlaod button
				AnchorElement downLoadAnchorElem = doc.createAnchorElement();
				buttonBarDiv.appendChild(downLoadAnchorElem);
				downLoadAnchorElem.setClassName("btn btn-mini btn-info");
				downLoadAnchorElem.setHref("javascript:;");
				downLoadAnchorElem.setAttribute("bookassetid", desc.getId());
				downLoadAnchorElem.setAttribute("data-original-title",
						"Download");
				downLoadAnchorElem.setAttribute("title", "");
				downLoadAnchorElem.setAttribute("rel", "tooltip");
				Element iconElem = doc.createElement("i");
				downLoadAnchorElem.appendChild(iconElem);
				iconElem.setClassName("icon-cloud-download");

				// Add to TOC
				DivElement bookContentTOCItemDiv = doc.createDivElement();
				bookContentkTOCDiv.appendChild(bookContentTOCItemDiv);
				bookContentTOCItemDiv.setClassName("bk-toc-item");
				String currentRef = "" + (counter + 1);
				bookContentTOCItemDiv.setAttribute("item-ref", currentRef);
				bookContentTOCItemDiv.setInnerText("" + ++counter + ". "
						+ descStr);
			}
		}

		// Back of book now
		DivElement bookBackElem = doc.createDivElement();
		bookDiv.appendChild(bookBackElem);
		bookBackElem.setClassName("bk-back");
		bookBackElem
				.setAttribute(
						"style",
						"background-image: url('"
								+ Utils.buildRestServiceForBookBackCoverDownloadLink(bookId)
								+ "'); background-repeat: no-repeat; background-size: contain; overflow:auto;");
		ParagraphElement bookIntroElem = doc.createPElement();
		bookBackElem.appendChild(bookIntroElem);
		bookIntroElem.setInnerText(book.getIntroduction());

		// Right of book
		DivElement bookRightElem = doc.createDivElement();
		bookDiv.appendChild(bookRightElem);
		bookRightElem.addClassName("bk-right");

		// Left of book (binding)
		DivElement bookLeftElem = doc.createDivElement();
		bookDiv.appendChild(bookLeftElem);
		bookLeftElem.addClassName("bk-left");
		bookLeftElem.setAttribute("style", "background-color: " + catColor
				+ ";");
		HeadingElement h2AuthorTitleLeftElement = doc.createHElement(2);
		bookLeftElem.appendChild(h2AuthorTitleLeftElement);
		h2AuthorTitleLeftElement.setAttribute("style", "color: " + catColorTxt
				+ ";");
		// Author
		SpanElement authorLeftSpan = doc.createSpanElement();
		h2AuthorTitleLeftElement.appendChild(authorLeftSpan);
		authorLeftSpan.setInnerText(book.getAuthorId());
		// Title
		SpanElement titleLeftSpan = doc.createSpanElement();
		h2AuthorTitleLeftElement.appendChild(titleLeftSpan);
		titleLeftSpan.setInnerText(book.getBookTitle());

		// Book top
		DivElement bookTopElem = doc.createDivElement();
		bookDiv.appendChild(bookTopElem);
		bookTopElem.addClassName("bk-top");

		// Book top
		DivElement bookBottomElem = doc.createDivElement();
		bookDiv.appendChild(bookBottomElem);
		bookBottomElem.addClassName("bk-bottom");

		// Book info section, this gets added to the list item element
		DivElement bookInfoElem = doc.createDivElement();
		li.appendChild(bookInfoElem);
		bookInfoElem.addClassName("bk-info");
		// Flip button
		ButtonElement flipButtonElem = doc.createPushButtonElement();
		bookInfoElem.appendChild(flipButtonElem);
		flipButtonElem.addClassName("bk-bookback");
		// flipButtonElem.setInnerText("Back Cover");

		// Look inside button
		ButtonElement lookInsideButtonElem = doc.createPushButtonElement();
		bookInfoElem.appendChild(lookInsideButtonElem);
		lookInsideButtonElem.addClassName("bk-bookview");
		// lookInsideButtonElem.setInnerText("Look Inside");

		// Star rating
		DivElement rateContainerElem = doc.createDivElement();
		bookInfoElem.appendChild(rateContainerElem);
		rateContainerElem.setAttribute("style", "margin-top: 15px;");
		DivElement rateContainerTextElem = doc.createDivElement();
		rateContainerElem.appendChild(rateContainerTextElem);
		rateContainerTextElem.setInnerHTML("<b>Rating: </b>");
		rateContainerTextElem.setAttribute("style",
				"float: left; padding-right: 10px;");
		DivElement ratingStarContainerElem = doc.createDivElement();
		rateContainerElem.appendChild(ratingStarContainerElem);
		StarRating sr = new StarRating(5, true);
		sr.setRating(bookDisplay.getBookRating());
		Element starElement = sr.getElement();
		starElement.setAttribute("style", "display: inline-block;");
		ratingStarContainerElem.appendChild(starElement);

		// Return the bookDiv
		return bookDiv;
	}

	/**
	 * Used to add books in after the initial display
	 * 
	 * @param bookDiv
	 */
	public void attachEventsToLst() {
		final GQuery books = $("#bk-list > li > div.bk-book", rootDiv);
		this.currentBookCnt = books.length();

		books.each(new Function() {
			@Override
			public void f(Element e) {
				final GQuery book = $(e);
				final GQuery other = books.not(book);
				final GQuery parent = book.parent();
				final GQuery page = book.children("div.bk-page");
				final GQuery bookview = parent.find("button.bk-bookview");
				final GQuery flipAction = parent.find("button.bk-bookback");
				final GQuery content = page.children("div.bk-content");
				final GQuery tocItems = content
						.children(".bk-toc .bk-toc-item");
				final IntHolder current = new IntHolder();

				// Bind the call back
				book.bind(Event.ONCLICK, new Function() {
					@Override
					public boolean f(Event e) {
						String bookId = book.attr("bookId");
						bookSelectCallBack.onBookSelect(bookId);
						return true;
					}
				});

				flipAction.bind(Event.ONCLICK, new Function() {
					@Override
					public boolean f(Event e) {
						// Toggle the button
						if (flipAction.hasClass("bk-bookback-pressed") == true) {
							flipAction.removeClass("bk-bookback-pressed");
						} else {
							flipAction.addClass("bk-bookback-pressed");
						}

						bookview.removeClass("bk-active");
						boolean flipVal = false;
						Object flipObj = book.data("flip");
						if (flipObj != null) {
							flipVal = (Boolean) flipObj;
						}
						if (flipVal == true) {
							book.data("opened", false).data("flip", false);
							book.removeClass("bk-viewback");
							book.addClass("bk-bookdefault");
						} else {
							book.data("opened", false).data("flip", true);
							book.removeClass("bk-viewinside").removeClass(
									"bk-bookdefault");
							book.addClass("bk-viewback");
						}
						return true;
					}
				});

				bookview.bind(Event.ONCLICK, new Function() {
					@Override
					public boolean f(Event e) {

						// If the view back cover was pressed need to reset
						// the button
						if (flipAction.hasClass("bk-bookback-pressed") == true) {
							flipAction.removeClass("bk-bookback-pressed");
						}

						GQuery thisPt = $(e);
						other.data("opened", false);
						other.removeClass("bk-viewinside");
						GQuery otherParent = other.parent().css(
								CSS.ZINDEX.with(0));
						otherParent.find("button.bk-bookview").removeClass(
								"bk-active");

						if (!other.hasClass("bk-viewback")) {
							other.addClass("bk-bookdefault");
						}

						boolean openedVal = false;
						Object openedObj = book.data("opened");
						if (openedObj != null) {
							openedVal = (Boolean) openedObj;
						}
						if (openedVal == true) {
							thisPt.removeClass("bk-active");
							book.data("opened", false).data("flip", false);
							book.removeClass("bk-viewinside");
							book.addClass("bk-bookdefault");
						} else {
							thisPt.addClass("bk-active");
							book.data("opened", true).data("flip", false);
							book.removeClass("bk-viewback").removeClass(
									"bk-bookdefault");
							book.addClass("bk-viewinside");
							parent.css(CSS.ZINDEX.with(currentBookCnt++));
							current.setIntVal(0);
							content.removeClass("bk-content-current")
									.eq(current.getIntVal())
									.addClass("bk-content-current");
						}
						return true;
					}
				});

				if (content.length() > 1) {
					GQuery navPrev = $("<span class=\"bk-page-prev\">&lt;</span>");
					GQuery navNext = $("<span class=\"bk-page-next\">&gt;</span>");
					page.append($("<nav></nav>").append(navPrev)
							.append(navNext));

					navPrev.bind(Event.ONCLICK, new Function() {
						@Override
						public boolean f(Event e) {
							if (current.getIntVal() > 0) {
								current.subOne();
								content.removeClass("bk-content-current")
										.eq(current.getIntVal())
										.addClass("bk-content-current");
							}
							return false;
						}
					});

					navNext.bind(Event.ONCLICK, new Function() {
						@Override
						public boolean f(Event e) {
							if (current.getIntVal() < content.length() - 1) {
								current.plusOne();
								content.removeClass("bk-content-current")
										.eq(current.getIntVal())
										.addClass("bk-content-current");
							}
							return false;
						}
					});

					// TOC items
					tocItems.each(new Function() {
						@Override
						public void f(Element e) {
							GQuery item = $(e);
							item.bind(Event.ONCLICK, new Function() {
								@Override
								public boolean f(Event e) {
									GQuery thisItem = $(e);
									String refStr = thisItem.attr("item-ref");
									int refInt = Integer.parseInt(refStr);
									if (refInt <= content.length() - 1) {
										current.setIntVal(refInt);
										content.removeClass(
												"bk-content-current")
												.eq(current.getIntVal())
												.addClass("bk-content-current");
									}
									return false;
								}
							});
						}
					});
				}
			}
		});
	}

	/**
	 * Remove all events from list
	 */
	public void removeEventsFromLst() {
		final GQuery books = $("#bk-list > li > div.bk-book", rootDiv);
		books.each(new Function() {
			@Override
			public void f(Element e) {
				final GQuery book = $(e);
				final GQuery page = book.children("div.bk-page");
				final GQuery parent = book.parent();
				final GQuery bookview = parent.find("button.bk-bookview");
				final GQuery flipAction = parent.find("button.bk-bookback");
				final GQuery content = page.children("div.bk-content");
				final GQuery tocItems = content
						.children(".bk-toc .bk-toc-item");

				book.unbind(Event.ONCLICK);
				flipAction.unbind(Event.ONCLICK);
				bookview.unbind(Event.ONCLICK);
				if (content.length() > 1) {
					GQuery navPrev = page.find(".bk-page-prev");
					if (navPrev != null) {
						navPrev.unbind(Event.ONCLICK);
						navPrev.remove();
					}

					GQuery navNext = page.find(".bk-page-next");
					if (navNext != null) {
						navNext.unbind(Event.ONCLICK);
						navNext.remove();
					}

					// Unbind TOC items
					tocItems.each().unbind(Event.ONCLICK);
				}
			}
		});

	}

	@Override
	protected void onAttach() {
		super.onAttach();

		if (this.hasBeenAttached == false) {
			// Did it the first time mark it as done
			this.hasBeenAttached = true;
		}
		
		// Use jQuery for the tooltip buttons
		setUpToolTipsForButtons();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		
		// Remove tool tips
		removeToolTipsForButtons();
		removeEventsFromLst();
	}

	/**
	 * Set tooltips for item buttons
	 */
	private native void setUpToolTipsForButtons() /*-{
		$wnd.jQuery("[rel=tooltip]").tooltip();
	}-*/;

	/**
	 * Remove tooltips
	 */
	private native void removeToolTipsForButtons() /*-{
		$wnd.jQuery("[rel=tooltip]").tooltip("destroy");
	}-*/;

}