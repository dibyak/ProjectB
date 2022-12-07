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

@Controller
public class ViewController 
{
	@GetMapping("/view")
	public String view()
	{
		return "view";
	}
	
	
	
	@PostMapping("/ProcessView")
	public String handelView(@RequestParam("hostname") String host, @RequestParam("baseport") String basePort1,
			Model model) {

		CloudviewAPIClientsFactory cloudviewAPIClientsFactory = null;
		String gatewayUrl;

		int basePort2 = Integer.parseInt(basePort1);

		int gatewayPort = basePort2 + 11;
		gatewayUrl = "http://" + host + ":" + gatewayPort + "/";
		//System.out.println("SG :: gatewayUrl - " + gatewayUrl);
		cloudviewAPIClientsFactory = CloudviewAPIClientsFactory.newInstance(gatewayUrl);
		
		Map<Integer,String> map=new HashMap<Integer,String>();
		
		try {
			SearchAPIClient searchAPIClient = cloudviewAPIClientsFactory.newSearchAPIClient();
			SearchClient searchClient = searchAPIClient.searchClient();
			SearchQuery query = new SearchQuery("vpmreference");
			query.addParameter(SearchParameter.SYNTHESIS, "disabled");
			query.addParameter(SearchParameter.USE_LOGIC_FACETS, "false");
			query.addParameter(SearchParameter.NRESULTS, "-1");

			SearchAnswer answer = searchClient.getResults(query);
			
			//System.out.println("SG :: answer count - " + answer.getHits().size());

			int hitIndex = 1;
			int hitIndex_1 ;
			for (Hit hit : answer.getHits()) {
				
				hitIndex_1 = hitIndex;

				String line = "";
				String line_1 = "";

				for (Meta meta : hit.getMetas()) {
					line += " ";
					for (MetaValue value : meta.getValues()) {
						line += meta.getName() + "=" + value.getStringValue();
						line += "; ";
					}
				}

				// Print fields
				if (line.length() > 0) {
					line_1 = line;
				}
 
				map.put(hitIndex_1, line_1);
				
				hitIndex++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("listOfValue" , map);

		return "view";

	}

}
