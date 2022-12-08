package com.example.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.exalead.cloudview.sdk.CloudviewAPIClientsFactory;
import com.exalead.searchapi.client.SearchAPIClient;
import com.exalead.searchapi.xmlv10.client.Hit;
import com.exalead.searchapi.xmlv10.client.Meta;
import com.exalead.searchapi.xmlv10.client.MetaValue;
import com.exalead.searchapi.xmlv10.client.SearchAnswer;
import com.exalead.searchapi.xmlv10.client.SearchClient;
import com.exalead.searchapi.xmlv10.client.SearchParameter;
import com.exalead.searchapi.xmlv10.client.SearchQuery;

/**
 * 
 * @author SGSPL
 *
 */
@Controller
public class ViewController 
{
	@GetMapping("/view")
	public String view()
	{
		return "view";
	}
	
	/**
	 * 
	 * @param strHost get host details from UI
	 * @param strBasePort get port details from UI
	 * @param model works a container that contains the data of the application. Here, a data can be in any form such as objects, strings, information from the database, etc.
	 * @return used to send all the details to UI part to show the output
	 */
	@PostMapping("/ProcessView")
	public String handelView(@RequestParam("hostname") String strHost, @RequestParam("baseport") String strBasePort,
			Model model) {
		CloudviewAPIClientsFactory cloudviewAPIClientsFactory = null;
		String strGatewayUrl;
		int intBasePort = Integer.parseInt(strBasePort);
		int gatewayPort = intBasePort + 11;
		strGatewayUrl = "http://" + strHost + ":" + gatewayPort + "/";
		cloudviewAPIClientsFactory = CloudviewAPIClientsFactory.newInstance(strGatewayUrl);
		Map<Integer,String> map=new HashMap<>();
		try {
			SearchAPIClient searchAPIClient = cloudviewAPIClientsFactory.newSearchAPIClient();
			SearchClient searchClient = searchAPIClient.searchClient();
			SearchQuery searchQuery = new SearchQuery("vpmreference");
			searchQuery.addParameter(SearchParameter.SYNTHESIS, "disabled");
			searchQuery.addParameter(SearchParameter.USE_LOGIC_FACETS, "false");
			searchQuery.addParameter(SearchParameter.NRESULTS, "-1");
			SearchAnswer searchAnswer = searchClient.getResults(searchQuery);
			int intHitIndex = 1;
			int intHitIndexSub ;
			for (Hit hit : searchAnswer.getHits()) {	
				intHitIndexSub = intHitIndex;
				String strHitDetails = "";
				String strHitDetailsSub = "";
				for (Meta meta : hit.getMetas()) {
					strHitDetails += " ";
					for (MetaValue value : meta.getValues()) {
						strHitDetails += meta.getName() + "=" + value.getStringValue();
						strHitDetails += "; ";
					}
				}
				if (strHitDetails.length() > 0) {
					strHitDetailsSub = strHitDetails;
				}
				map.put(intHitIndexSub, strHitDetailsSub);		
				intHitIndex++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		model.addAttribute("listOfValue" , map);
		return "view";
	}

}
