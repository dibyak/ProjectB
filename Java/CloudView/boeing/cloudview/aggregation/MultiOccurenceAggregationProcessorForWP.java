package boeing.cloudview.aggregation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.exalead.cloudview.consolidationapi.processors.IAggregationDocument;
import com.exalead.cloudview.consolidationapi.processors.java.IJavaAllUpdatesAggregationHandler;
import com.exalead.cloudview.consolidationapi.processors.java.IJavaAllUpdatesAggregationProcessor;
import com.exalead.mercury.component.CVComponentDescription;
import com.exalead.mercury.component.config.CVComponentConfigClass;

@CVComponentConfigClass(configClass = MultiOccurenceAggregationProcessorForWPConfig.class)
@CVComponentDescription(value = "This Multi Occurence Aggregation Processor for Work Plan")
public class MultiOccurenceAggregationProcessorForWP implements IJavaAllUpdatesAggregationProcessor {
	private MultiOccurenceAggregationProcessorForWPConfig config;

	private String meta_instance_externalid = "instance_externalid";
	private String meta_name = "name";

//	private List<String> listOfTypes = Arrays.asList("a", "b", "c");
	private List<String> listOfRels = Arrays.asList("DELLmiLoadingOperationInstance", "DELLmiHeaderOperationInstance",
			"DELLmiWorkPlanSystemInstance");

	private StringBuilder sbResult = new StringBuilder();

	private static final Log logger = LogFactory.getLog(MultiOccurenceAggregationProcessorForWP.class);

	public MultiOccurenceAggregationProcessorForWP(MultiOccurenceAggregationProcessorForWPConfig config) {
		this.config = config;
	}

	@Override
	public String getAggregationDocumentType() {
//		return config.getType();
//		return "rel_dellmiloadingoperationinstance";
		return config.getTargetNode();
	}

	@Override
	public void process(IJavaAllUpdatesAggregationHandler handler, IAggregationDocument document) throws Exception {

		logMessage("info",
				"MultiOccurenceAggregationProcessorForWP  :: getTargetMeta - " + this.config.getTargetMeta());
		logMessage("info",
				"MultiOccurenceAggregationProcessorForWP  :: getTargetMeta - " + document.getMeta("physicalid"));
		document.deleteMeta(this.config.getTargetMeta());

		StringBuilder sbData = processDocument(handler, document, "", this.config.getRelName());
		String[] sArrayOfObjects = sbData.toString().split("~");
		for (String sObjects : sArrayOfObjects) {
			document.withMeta(this.config.getTargetMeta(), sObjects);
		}

	}

	private StringBuilder processDocument(IJavaAllUpdatesAggregationHandler handler, IAggregationDocument document,
			String separator, String SRelationship) {
		logger.info("---- CALLED --- " + SRelationship);
		Stack<Stack<IAggregationDocument>> stackStackPaths = new Stack<>();

		List<IAggregationDocument> listTempDoc = handler.matchPathEnd(document, SRelationship);
		logger.info("---- listTempDoc --- " + listTempDoc.size());
		for (IAggregationDocument tempDoc : listTempDoc) {
			Stack<IAggregationDocument> stackPath = new Stack<>();
			stackPath.push(document);
			stackPath.push(tempDoc);
			stackStackPaths.push(stackPath);
		}

		while (!stackStackPaths.isEmpty()) {
			logger.info("---- while loop --- " + stackStackPaths.size());

			/*
			 * if (stackStackPaths.size() == 10) { break; }
			 */

			logger.info("---- stackStackPaths Before Pop --- " + stackStackPaths);
			Stack<IAggregationDocument> stackPath = stackStackPaths.pop();
			logger.info("---- stackStackPaths After Pop --- " + stackStackPaths);
			logger.info("---- stackPath --- " + stackPath);
			IAggregationDocument doc2 = stackPath.peek();
			logger.info("---- peeked object --- " + doc2.getMeta("name"));
			List<IAggregationDocument> listTempDoc2 = getPathObj(handler, doc2);
			if (listTempDoc2 == null || listTempDoc2.isEmpty()) {
				printPath(stackPath);
			} else {
				for (IAggregationDocument tempDoc : listTempDoc2) {
					Stack<IAggregationDocument> stackPath2 = (Stack<IAggregationDocument>) stackPath.clone();
					stackPath2.push(tempDoc);
					stackStackPaths.push(stackPath2);
				}
				logger.info("---- stackStackPaths Added new Paths to Stack--- " + stackStackPaths);
			}
		}
		logger.info("final path = " + sbResult);
		return sbResult;
	}

	private List<IAggregationDocument> getPathObj(IJavaAllUpdatesAggregationHandler handler,
			IAggregationDocument document) {
		logger.info("---- In  getPathObj --- ");
		/*
		 * String[] sRelList = new String[4]; sRelList[0] =
		 * "toDELLmiHeaderOperationInstance"; sRelList[1] =
		 * "fromDELLmiHeaderOperationInstance"; sRelList[2] =
		 * "toDELLmiWorkPlanSystemInstance"; sRelList[3] =
		 * "fromDELLmiWorkPlanSystemInstance";
		 */

		String sRelName = null;

		Map mArcInfo = new HashMap<String, String>();
		mArcInfo.put("DELLmiLoadingOperationInstance", "fromDELLmiLoadingOperationInstance");
		mArcInfo.put("DELLmiHeaderOperationReference", "toDELLmiHeaderOperationInstance");
		mArcInfo.put("DELLmiHeaderOperationInstance", "fromDELLmiHeaderOperationInstance");
		mArcInfo.put("DELLmiWorkPlanSystemReference", "toDELLmiWorkPlanSystemInstance");
		mArcInfo.put("DELLmiWorkPlanSystemInstance", "fromDELLmiWorkPlanSystemInstance");

		logger.info("---- document type =  " + document.getMeta("type"));
		sRelName = (String) mArcInfo.get(document.getMeta("type"));
		if (sRelName != null && !sRelName.isEmpty()) {
			logger.info("---- sRelName =  " + sRelName);
			List<IAggregationDocument> listTempDoc = handler.matchPathEnd(document, sRelName);
			logger.info("---- listTempDoc =  " + listTempDoc);
			logger.info("---- In  getPathObj ---return list doc size =  " + listTempDoc.size());
			if (listTempDoc != null && !listTempDoc.isEmpty()) {

				return listTempDoc;
			}
		}
		return null;
	}

	private void printPath(Stack<IAggregationDocument> stackPath) {
		logger.info("DDDDDDBBBBBBBB --- ");
		sbResult.append("~");
		for (Iterator<IAggregationDocument> iterator = stackPath.iterator(); iterator.hasNext();) {
			IAggregationDocument doc = iterator.next();
			if (listOfRels.contains(doc.getMeta(meta_name))) {
				sbResult.append(doc.getMeta(meta_instance_externalid));
				sbResult.append("|");
			} else {
				sbResult.append(doc.getMeta(meta_name));
				sbResult.append("#");
			}
		}
		sbResult.setLength(sbResult.length() - 1);
		logger.info("path = " + sbResult);
	}

	private void logMessage(String method, String message) {

		if (method.equalsIgnoreCase("info")) {
			logger.info(message);
		} else if (method.equalsIgnoreCase("debug")) {
			logger.debug(message);
		} else if (method.equalsIgnoreCase("warn")) {
			logger.warn(message);
		} else if (method.equalsIgnoreCase("error")) {
			logger.error(message);
		} else {
			// do nothing
		}

	}

}
