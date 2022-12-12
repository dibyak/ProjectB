package com.boeing.cloudview.search;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/**
	 * 
	 * @return used to send all the details to UI part to show the output
	 */
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

	private final String EXA_URL_PROTOCOL = "http";
	private final String EXA_SERVER_NAME = "www.sgspc0813dx2022.com";
	private final String EXA_SERVER_PORT = "29010";

	private String INSTANCE_NAME = null;
	private String REFERENCE_NAME = null;

	/**
	 * 
	 * @param strInstance  get the Instance value from UI
	 * @param strReference get the reference value from UI
	 * @param model        works a container that contains the data of the
	 *                     application. Here, a data can be in any form such as
	 *                     objects, strings, information from the database, etc.
	 * @return used to send all the details to UI part to show the output
	 */
	@PostMapping("/index")
	public String handelForm(@RequestParam("child") String strInstance, @RequestParam("parent") String strReference, Model model) {
		Map<String, List> mlFinalData = new HashMap<>();
		List<Map<String, String>> lFinaldata = new ArrayList<Map<String, String>>();
		try {
			INSTANCE_NAME = strInstance;
			REFERENCE_NAME = strReference;
			Document docInstance = getDataFromSearchAPI("3dx_rel_instance_externalid:" + INSTANCE_NAME);
			List<Map<String, String>> lmInstanceMetaResult = getMetaInformation(docInstance, Arrays.asList("occurrence"));

			for (Map mInstanceMeta : lmInstanceMetaResult) {
				if (mInstanceMeta.containsKey("occurrence")) {
					String strPath = (String) mInstanceMeta.get("occurrence");

					String[] sPaths = strPath.split("\n");
					for (int k = 0; k < sPaths.length; k++) {
						strPath = sPaths[k];
						if (strPath != null && !"".equals(strPath) && strPath.contains(INSTANCE_NAME) && strPath.contains(REFERENCE_NAME)) {
							String strPhysicalIDPath = strPath.substring(strPath.lastIndexOf("~") + 1);
							String strUpdatedPath = "";
							String[] sNewPaths = strPhysicalIDPath.split("\\|");
							for (int x = 1; x < sNewPaths.length; x++) {
								if (sNewPaths[x].contains(":")) {
									strUpdatedPath += "*" + sNewPaths[x].split(":")[0] + "* AND ";
								} else {
									strUpdatedPath += "*" + sNewPaths[x] + "* AND ";
								}
							}
							strUpdatedPath = strUpdatedPath.substring(0, strUpdatedPath.length() - 4);

							// Get the MfgProductionPlanning Details
							Document docMFG = getDataFromSearchAPI("3dx_type:mfgproductionplanning AND " + strUpdatedPath);
							List<Map<String, String>> lmMFGMetaResult = getMetaInformation(docMFG, Arrays.asList("mpart_pid"));
							for (Map mMFGMeta : lmMFGMetaResult) {
								if (mMFGMeta.containsKey("mpart_pid")) {
									String strMPartID = (String) mMFGMeta.get("mpart_pid");
									if (strMPartID != null && !"".equals(strMPartID)) {
										// Call Search-API to get the MBOMInstance Data
										Document docDelfIns = getDataFromSearchAPI("3dx_physicalid:" + strMPartID);
										List<Map<String, String>> lmDelfMetaResult = getMetaInformation(docDelfIns, Arrays.asList("physicalid", "instance_externalid"));
										mlFinalData.put(strPath, lmDelfMetaResult);
									}
								}
							}
						}
					}

				}
			}
			model.addAttribute("InstanceValue", strInstance);
			model.addAttribute("ReferenceValue", strReference);
			model.addAttribute("FinalData", mlFinalData);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "index";
	}

	/**
	 * 
	 * @param strQuery get the search query value
	 * @return document object
	 * @throws Exception to handle all the exception if its occur during run time
	 */
	public Document getDataFromSearchAPI(String strQuery) throws Exception {
		StringBuilder sbSearchURL = new StringBuilder();
		Document document = null;
		String strSearchURL = null;
		try {
			sbSearchURL = new StringBuilder("search-api/search?");
			strSearchURL = URLEncoder.encode(strQuery, "UTF-8");
			strSearchURL = new StringBuilder(EXA_URL_PROTOCOL).append("://").append(EXA_SERVER_NAME).append(":").append(EXA_SERVER_PORT).append("/").append(sbSearchURL.toString())
					.append("q=").append(strSearchURL).toString();
			URL url = new URL(strSearchURL);
			URLConnection urlConnection = url.openConnection();
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(urlConnection.getInputStream());
			document.getDocumentElement().normalize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * 
	 * @param document get the document object from caller method
	 * @param lMetas   get the list of data from caller method
	 * @return the list of meta information to the caller method
	 * @throws Exception
	 */
	public List<Map<String, String>> getMetaInformation(Document document, List<String> lMetas) throws Exception {
		Element rootElement = null;
		String strNumberOfnHits = null;
		List<Map<String, String>> lmMetaInfomation = new ArrayList<Map<String, String>>();
		try {
			rootElement = document.getDocumentElement();
			strNumberOfnHits = rootElement.getAttribute("nhits");
			if (!"0".equals(strNumberOfnHits)) {
				NodeList nMetasList = document.getElementsByTagName("metas");

				for (int i = 0; i < nMetasList.getLength(); i++) {
					Node nMetas = nMetasList.item(i);
					Map<String, String> mMeta = new HashMap<>();
					for (int j = 0; j < nMetas.getChildNodes().getLength(); j++) {
						Element eMetaDetails = (Element) nMetas.getChildNodes().item(j);
						String strMetaName = eMetaDetails.getAttribute("name");
						if (lMetas.contains(strMetaName)) {
							mMeta.put(strMetaName, eMetaDetails.getElementsByTagName("MetaString").item(0).getTextContent());
						}
					}
					lmMetaInfomation.add(mMeta);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lmMetaInfomation;
	}

}
