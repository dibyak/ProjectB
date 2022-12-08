package com.example.demo;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author SGSPL
 *
 */
@Controller
public class MainController {
	@GetMapping("/")
	public String front() {
		return "front";
	}

	/**
	 * 
	 * @return used to send all the details to UI part to show the output
	 */
	@GetMapping("/index")
	public String index() {
		return "index";
	}

	/**
	 * 
	 * @param strInstance  get the Instance value from UI
	 * @param strReference get the reference value from UI
	 * @param model        works a container that contains the data of the
	 *                     application. Here, a data can be in any form such as
	 *                     objects, strings, information from the database, etc.
	 * @return used to send all the details to UI part to show the output
	 */
	@PostMapping("/ProcessForm")
	public String handelForm(@RequestParam("child") String strInstance, @RequestParam("parent") String strReference, Model model) {
		try {
			String strEXAURLPROTOCOL = "http";
			String strEXASERVERNAME = "www.sgspc0813dx2022.com";
			String strEXASERVERPORT = "29010";
			String strEXAQUERYQUERY = "3dx_type_occurence_test";
			
			String strInstanceCopy = strInstance;

			StringBuilder sbEXAUrl = new StringBuilder("search-api/search?");
			strEXAQUERYQUERY = URLEncoder.encode(strEXAQUERYQUERY, "UTF-8");
			strInstanceCopy = (URLEncoder.encode(strInstanceCopy, "UTF-8").replace("+", "%20"));
			String strEXAUrl = new StringBuilder(strEXAURLPROTOCOL).append("://").append(strEXASERVERNAME).append(":")
					.append(strEXASERVERPORT).append("/").append(sbEXAUrl.toString()).append("q=").append(strEXAQUERYQUERY)
					.append(":%22").append(strInstanceCopy).append("*%22").toString();

			URL url = new URL(strEXAUrl);
			URLConnection urlConnection = url.openConnection();

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(urlConnection.getInputStream());
			document.getDocumentElement().normalize();

			Element rootElement = document.getDocumentElement();
			String strNumberOfnHits = rootElement.getAttribute("nhits");

			String strOccurenceDetails = null;

			if (!"0".equals(strNumberOfnHits)) {
				NodeList hitsList = document.getElementsByTagName("hits");

				for (int i = 0; i < hitsList.getLength(); i++) {
					Node nodeHitsListItem = hitsList.item(i);

					if (nodeHitsListItem.getNodeType() == Node.ELEMENT_NODE) {
						NodeList nodeHitListChildItem = nodeHitsListItem.getChildNodes();
						ArrayList<String> listDetailAttribute = new ArrayList<>();
						Element detailElementHits = null;
						Node nodeHitListItem = null;
						NodeList hitList = null;

						for (int j = 0; j < nodeHitListChildItem.getLength(); j++) {
							nodeHitListItem = nodeHitListChildItem.item(j);
							if (nodeHitListItem.getNodeType() == Node.ELEMENT_NODE) {
								detailElementHits = (Element) nodeHitListItem;

								if (detailElementHits.getNodeName().contains("Hit"))
								{
									listDetailAttribute.add(detailElementHits.getAttribute("url"));
									hitList = document.getElementsByTagName("Hit");
								}
							}
						}

						for (int m = 0; m < hitList.getLength(); m++) {
							Node hitDetails = hitList.item(m);
							if (hitDetails.getNodeType() == Node.ELEMENT_NODE) {
								NodeList nodeMetasListItem = hitDetails.getChildNodes();
								Node nodemMetasListItem = null;
								Element detailElement = null;
								for (int n = 0; n < nodeMetasListItem.getLength(); n++) {
									nodemMetasListItem = nodeMetasListItem.item(n);
									if (nodemMetasListItem.getNodeType() == Node.ELEMENT_NODE) {
										detailElement = (Element) nodemMetasListItem;
										if ("metas".equals(detailElement.getNodeName())) {
											NodeList metasList = document.getElementsByTagName("metas");
											for (int a = 0; a < metasList.getLength(); a++) {
												Node meta = metasList.item(a);
												if (meta.getNodeType() == Node.ELEMENT_NODE) {
													NodeList metaDetails = meta.getChildNodes();
													for (int b = 0; b < metaDetails.getLength(); b++) {
														Node detail = metaDetails.item(b);
														if (detail.getNodeType() == Node.ELEMENT_NODE) {
															Element detailElements = (Element) detail;
															String strMetaNameDetails = detailElements.getAttribute("name");
															if (strMetaNameDetails.contains("occurence_test")) {
																strOccurenceDetails = detailElements.getElementsByTagName("MetaString").item(0).getTextContent();

																String[] arrOccurenceDetailSeperator = strOccurenceDetails.split("~");
																String strOccurencePath = null;
																String splitvalue = null;
																List<String> splitvaluewithList = new ArrayList<>();

																for (int q = 0; q < arrOccurenceDetailSeperator.length; q++) {
																	if (arrOccurenceDetailSeperator[q].contains(strInstance)
																			&& arrOccurenceDetailSeperator[q].contains(strReference)) {
																		strOccurencePath = arrOccurenceDetailSeperator[q];

																		int intReferenceIndex = strOccurencePath.indexOf(strReference);
																		String strReferenceLastChar = strReference.substring(strReference.length() - 1);
																		int intReflastCharIndex = strReference.lastIndexOf(strReferenceLastChar);
																		splitvalue = strOccurencePath.substring(0, ((intReferenceIndex + intReflastCharIndex) + 1));
																		splitvaluewithList.add(splitvalue);

																	}
																}

																model.addAttribute("userchild", strInstance);
																model.addAttribute("userparent", strReference);
																model.addAttribute("pathsAfterSplit", splitvaluewithList);

															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "index";
	}

}
