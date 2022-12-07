package boeing_exalead.consobox.aggregation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.exalead.cloudview.consolidationapi.processors.IAggregationDocument;
import com.exalead.cloudview.consolidationapi.processors.java.IJavaAllUpdatesAggregationHandler;
import com.exalead.cloudview.consolidationapi.processors.java.IJavaAllUpdatesAggregationProcessor;
import com.exalead.mercury.component.CVComponentDescription;
import com.exalead.mercury.component.config.CVComponentConfigClass;

/**
 * 
 * @author SGS
 *
 */
@CVComponentConfigClass(configClass = MultiOccurenceAggregationProcessorForWPConfig.class)
@CVComponentDescription(value = "This Multi Occurence Aggregation Processor for Work Plan")
public class MultiOccurenceAggregationProcessorForWP implements IJavaAllUpdatesAggregationProcessor {
	private MultiOccurenceAggregationProcessorForWPConfig workPlanConfig;

	private String strMetaInstanceEexternalid = "instance_externalid";
	private String strMetaName = "name";

	private List<String> listOfRelationship = Arrays.asList("DELLmiLoadingOperationInstance",
			"DELLmiGeneralOperationInstance", "DELLmiUnloadingOperationInstance", "DELLmiHeaderOperationInstance",
			"DELLmiWorkPlanSystemInstance");

	private StringBuilder stringBuilderResult = new StringBuilder();

	public MultiOccurenceAggregationProcessorForWP(MultiOccurenceAggregationProcessorForWPConfig config) {
		this.workPlanConfig = config;
	}

	/**
	 * 
	 */
	@Override
	public String getAggregationDocumentType() {
		return workPlanConfig.getTargetNode();
	}

	/**
	 * 
	 */
	@Override
	public void process(IJavaAllUpdatesAggregationHandler aggregationHandler, IAggregationDocument aggregationDocument)
			throws Exception {

		aggregationDocument.deleteMeta(this.workPlanConfig.getTargetMeta());

		StringBuilder stringBuilderData = processDocument(aggregationHandler, aggregationDocument, "",
				this.workPlanConfig.getRelName());

		aggregationDocument.withMeta(this.workPlanConfig.getTargetMeta(), stringBuilderData.toString());
	}

	/**
	 * 
	 * @param aggregationHandler  The aggregation handler with the allowed
	 *                            operations for the processor.
	 * @param aggregationDocument The reference document.
	 * @param strSeparator
	 * @param strRelationship     It defines nodes type to process to find the
	 *                            multiple occurrence for the Relationship node
	 * @return string value
	 */
	private StringBuilder processDocument(IJavaAllUpdatesAggregationHandler aggregationHandler,
			IAggregationDocument aggregationDocument, String strSeparator, String strRelationship) {
		Stack<Stack<IAggregationDocument>> stackStackPaths = new Stack<>();

		List<IAggregationDocument> listTempDoc = aggregationHandler.matchPathEnd(aggregationDocument, strRelationship);
		for (IAggregationDocument tempDoc : listTempDoc) {
			Stack<IAggregationDocument> stackPath = new Stack<>();
			stackPath.push(aggregationDocument);
			stackPath.push(tempDoc);
			stackStackPaths.push(stackPath);
		}

		while (!stackStackPaths.isEmpty()) {

			Stack<IAggregationDocument> stackPath = stackStackPaths.pop();
			IAggregationDocument doc2 = stackPath.peek();
			List<IAggregationDocument> listTempDoc2 = getPathObj(aggregationHandler, doc2);
			if (listTempDoc2 == null || listTempDoc2.isEmpty()) {
				printPath(stackPath);
			} else {
				for (IAggregationDocument tempDoc : listTempDoc2) {
					@SuppressWarnings("unchecked")
					Stack<IAggregationDocument> stackPath2 = (Stack<IAggregationDocument>) stackPath.clone();
					stackPath2.push(tempDoc);
					stackStackPaths.push(stackPath2);
				}
			}
		}
		return stringBuilderResult;
	}

	/**
	 * 
	 * @param aggregationHandler  The aggregation handler with the allowed
	 *                            operations for the processor.
	 * @param aggregationDocument reference document.
	 * @return empty list value
	 */
	private List<IAggregationDocument> getPathObj(IJavaAllUpdatesAggregationHandler aggregationHandler,
			IAggregationDocument aggregationDocument) {
		String strRelationshipName = null;

		Map<String, String> mArcInfo = new HashMap<>();
		mArcInfo.put("DELLmiLoadingOperationInstance", "fromDELLmiLoadingOperationInstance");
		mArcInfo.put("DELLmiUnloadingOperationInstance", "fromDELLmiUnloadingOperationInstance");
		mArcInfo.put("DELLmiGeneralOperationInstance", "fromDELLmiGeneralOperationInstance");
		mArcInfo.put("DELLmiHeaderOperationReference", "toDELLmiHeaderOperationInstance");
		mArcInfo.put("DELLmiHeaderOperationInstance", "fromDELLmiHeaderOperationInstance");
		mArcInfo.put("DELLmiWorkPlanSystemReference", "toDELLmiWorkPlanSystemInstance");
		mArcInfo.put("DELLmiWorkPlanSystemInstance", "fromDELLmiWorkPlanSystemInstance");

		strRelationshipName = mArcInfo.get(aggregationDocument.getMeta("type"));
		if (strRelationshipName != null && !strRelationshipName.isEmpty()) {
			List<IAggregationDocument> listTempDoc = aggregationHandler.matchPathEnd(aggregationDocument,
					strRelationshipName);
			if (listTempDoc != null && !listTempDoc.isEmpty()) {

				return listTempDoc;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * 
	 * @param stackPath represents a last-in-first-out (LIFO) stack of objects. The
	 *                  usual {@code push} and {@code pop} operations are provided,
	 *                  as well as a method to {@code peek} at the top item on the
	 *                  stack
	 */
	private void printPath(Stack<IAggregationDocument> stackPath) {
		stringBuilderResult.append("\u007E");
		for (Iterator<IAggregationDocument> iterator = stackPath.iterator(); iterator.hasNext();) {
			IAggregationDocument doc = iterator.next();
			if (listOfRelationship.contains(doc.getMeta(strMetaName))) {
				stringBuilderResult.append(doc.getMeta(strMetaInstanceEexternalid));
				stringBuilderResult.append("|");
			} else {
				stringBuilderResult.append(doc.getMeta(strMetaName));
				stringBuilderResult.append("#");
			}
		}
		stringBuilderResult.setLength(stringBuilderResult.length() - 1);
	}

}
