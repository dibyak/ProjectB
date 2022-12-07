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

@Controller
public class MainController 
{
	@GetMapping("/")
	public String front()
	{
		System.out.println("Inside front");
		return "front";
	}
	
	
	@GetMapping("/index")
	public String index()
	{
		System.out.println("Inside index");
		return "index";
	}
	
	
	@PostMapping("/ProcessForm")
	public String handelForm(@RequestParam("child") String childValue, @RequestParam("parent") String parentValue,
			Model model) {

		try {
			String EXA_URL_PROTOCOL = "http";
			String EXA_SERVER_NAME = "www.sgspc0813dx2022.com";
			String EXA_SERVERPORT = "29010";
			String EXAQUERY_QUERY = "3dx_type_occurrence";
			
//			FileInputStream fis = new FileInputStream("..\\exalead_project1\\build.properties");
//			Properties prop = new Properties();
//			prop.load(fis);
//			String EXA_URL_PROTOCOL = prop.getProperty("protocol");
//			String EXA_SERVER_NAME = prop.getProperty("servername");
//			String EXA_SERVERPORT = prop.getProperty("serverport");
//			String EXAQUERY_QUERY = prop.getProperty("query");
			
			
			

			StringBuilder sbEXAUrl = new StringBuilder("search-api/search?");
			EXAQUERY_QUERY = URLEncoder.encode(EXAQUERY_QUERY, "UTF-8");
			String strEXAUrl = new StringBuilder(EXA_URL_PROTOCOL).append("://").append(EXA_SERVER_NAME).append(":")
					.append(EXA_SERVERPORT).append("/").append(sbEXAUrl.toString()).append("q=").append(EXAQUERY_QUERY)
					.append(":%22").append(childValue).append("*%22").toString();

			URL url = new URL(strEXAUrl);
			URLConnection conn = url.openConnection();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(conn.getInputStream());
			document.getDocumentElement().normalize();

			Element rootElement = document.getDocumentElement();
			String numberofnhits = rootElement.getAttribute("nhits");

			String value = null;

			if (!"0".equals(numberofnhits)) {
				NodeList hitsLists = document.getElementsByTagName("hits");

				for (int i = 0; i < hitsLists.getLength(); i++) {
					Node hits = hitsLists.item(i);

					if (hits.getNodeType() == Node.ELEMENT_NODE) {
						NodeList hitsDetails = hits.getChildNodes();

						ArrayList<String> url1 = new ArrayList<String>();

						int hitCount = 0;
						Element detailElementHits = null;
						Node detailhits = null;
						NodeList HitList = null;

						for (int j = 0; j < hitsDetails.getLength(); j++) {
							detailhits = hitsDetails.item(j);
							if (detailhits.getNodeType() == Node.ELEMENT_NODE) {
								detailElementHits = (Element) detailhits;

								if (detailElementHits.getNodeName().contains("Hit")) // get Hit...
								{
									hitCount++;
									url1.add(detailElementHits.getAttribute("url"));
									HitList = document.getElementsByTagName("Hit");
								}
							}
						}

						for (int m = 0; m < HitList.getLength(); m++) {
							Node Hit = HitList.item(m);

							if (Hit.getNodeType() == Node.ELEMENT_NODE) {
								Element HitElement = (Element) Hit;

								NodeList metasDetails = Hit.getChildNodes();

								Node metasdetail = null;
								Element detailElement = null;

								for (int n = 0; n < metasDetails.getLength(); n++) {
									metasdetail = metasDetails.item(n);

									if (metasdetail.getNodeType() == Node.ELEMENT_NODE) {
										detailElement = (Element) metasdetail;

										if (detailElement.getNodeName() == "metas") {
											NodeList metasList = document.getElementsByTagName("metas");

											for (int a = 0; a < metasList.getLength(); a++) {
												Node meta = metasList.item(a);
												if (meta.getNodeType() == Node.ELEMENT_NODE) {
													Element metaElement = (Element) meta;
													NodeList metaDetails = meta.getChildNodes();

													for (int b = 0; b < metaDetails.getLength(); b++) {
														Node detail = metaDetails.item(b);
														if (detail.getNodeType() == Node.ELEMENT_NODE) {
															Element detailElements = (Element) detail;

															String metasdetails = detailElements.getAttribute("name");

															if (metasdetails.contains("occurrence")) {
																value = detailElements
																		.getElementsByTagName("MetaString").item(0)
																		.getTextContent();

																String[] result = value.split(" ");

																String newvalue = null;
																String splitvalue = null;
																List<String> splitvaluewithList = new ArrayList<String>();

																System.out.println("result = ");
																for (int q = 0; q < result.length; q++) {
																	if (result[q].contains(childValue)
																			&& result[q].contains(parentValue)) {
																		newvalue = result[q];

																		int index1 = newvalue.indexOf(parentValue);

																		String lastcahr = parentValue
																				.substring(parentValue.length() - 1);

																		int index2 = parentValue.lastIndexOf(lastcahr);

																		splitvalue = newvalue.substring(0,
																				((index1 + index2) + 1));

																		splitvaluewithList.add(splitvalue);

																	}
																}

																model.addAttribute("userchild", childValue);
																model.addAttribute("userparent", parentValue);
																model.addAttribute("pathsAfterSplit",
																		splitvaluewithList);

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
