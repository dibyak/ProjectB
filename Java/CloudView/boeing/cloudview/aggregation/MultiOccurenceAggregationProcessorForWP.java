
package boeing.cloudview.aggregation;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.exalead.cloudview.consolidationapi.processors.IAggregationDocument;
import com.exalead.cloudview.consolidationapi.processors.java.IJavaAllUpdatesAggregationHandler;
import com.exalead.cloudview.consolidationapi.processors.java.IJavaAllUpdatesAggregationProcessor;
import com.exalead.mercury.component.CVComponentDescription;
import com.exalead.mercury.component.config.CVComponentConfigClass;

/**
 * @author SGS
 */
@CVComponentConfigClass(configClass = MultiOccurenceAggregationProcessorForWPConfig.class)
@CVComponentDescription(value = "Multi Occurence Aggregation Processor for Work Plan")
public class MultiOccurenceAggregationProcessorForWP implements IJavaAllUpdatesAggregationProcessor {
	private MultiOccurenceAggregationProcessorForWPConfig workPlanConfig;
	private static final String META_INSTANCE_EXT_ID = "instance_externalid";
	private static final String META_NAME = "name";
	private static final String META_TYPE = "type";
	private static final String META_PID = "physicalid";
	private static final String INDEXED_VALUE_SEPARATOR = "\r\n";
	private static final String INST_REF_SEPARATOR = ":";
	private static final String INST_REF_PAIR_SEPARATOR = "|";
	private static final String NAME_PATH_PID_PATH_SEPARATOR = "~";
	private static final List<String> REL_LIST = Arrays.asList("VPMInstance", "DELFmiFunctionIdentifiedInstance", "DELLmiLoadingOperationInstance", "DELLmiGeneralOperationInstance", "DELLmiUnloadingOperationInstance",
			"DELLmiHeaderOperationInstance", "DELLmiWorkPlanSystemInstance");
	private static final Log logger = LogFactory.getLog(MultiOccurenceAggregationProcessorForWP.class);
	private static final Map<String, String> NODE_ARC_MAP = new HashMap<>();
	static {
		NODE_ARC_MAP.put("VPMInstance", "fromVPMInstance");
		// TODO : CHECK mapping for VPMInstance
		NODE_ARC_MAP.put("VPMReference", "toVPMInstance");
		NODE_ARC_MAP.put("BOE_Collector", "toVPMInstance");
		NODE_ARC_MAP.put("BOE_Installation", "toVPMInstance");
		NODE_ARC_MAP.put("BOE_Assembly", "toVPMInstance");
		NODE_ARC_MAP.put("DELFmiFunctionIdentifiedInstance", "fromDELFmiFunctionIdentifiedInstance");
		// TODO : CHECK mapping for DELFmiFunctionIdentifiedInstance
		NODE_ARC_MAP.put("CreateAssembly", "toDELFmiFunctionIdentifiedInstance");
		NODE_ARC_MAP.put("Provide", "toDELFmiFunctionIdentifiedInstance");
		NODE_ARC_MAP.put("Installation", "toDELFmiFunctionIdentifiedInstance");
		NODE_ARC_MAP.put("DELLmiLoadingOperationInstance", "fromDELLmiLoadingOperationInstance");
		// TODO : CHECK mapping for DELLmiLoadingOperationReference
		NODE_ARC_MAP.put("DELLmiLoadingOperationReference", "toDELLmiLoadingOperationInstance");
		NODE_ARC_MAP.put("DELLmiUnloadingOperationInstance", "fromDELLmiUnloadingOperationInstance");
		// TODO : CHECK mapping for DELLmiUnloadingOperationReference
		NODE_ARC_MAP.put("DELLmiUnloadingOperationReference", "toDELLmiUnloadingOperationInstance");
		NODE_ARC_MAP.put("DELLmiGeneralOperationInstance", "fromDELLmiGeneralOperationInstance");
		// TODO : CHECK mapping for DELLmiGeneralOperationReference
		NODE_ARC_MAP.put("DELLmiGeneralOperationReference", "toDELLmiGeneralOperationInstance");
		NODE_ARC_MAP.put("DELLmiHeaderOperationInstance", "fromDELLmiHeaderOperationInstance");
		NODE_ARC_MAP.put("DELLmiHeaderOperationReference", "toDELLmiHeaderOperationInstance");
		NODE_ARC_MAP.put("DELLmiWorkPlanSystemReference", "toDELLmiWorkPlanSystemInstance");
		NODE_ARC_MAP.put("DELLmiWorkPlanSystemInstance", "fromDELLmiWorkPlanSystemInstance");
	}

	public MultiOccurenceAggregationProcessorForWP(MultiOccurenceAggregationProcessorForWPConfig config) {
		this.workPlanConfig = config;
	}

	/**
	 * Overridden Method : See {@link IJavaAllUpdatesAggregationProcessor}
	 */
	@Override
	public void process(IJavaAllUpdatesAggregationHandler aggregationHandler, IAggregationDocument aggregationDocument) throws Exception {
		aggregationDocument.deleteMeta(this.workPlanConfig.getTargetMeta());
		StringBuilder strBuildOccurrencePaths = processDocument(aggregationHandler, aggregationDocument);
		logger.info(strBuildOccurrencePaths.toString());
		aggregationDocument.withMeta(this.workPlanConfig.getTargetMeta(), strBuildOccurrencePaths.toString());
	}

	/**
	 * @param aggrHandler
	 *            The aggregation handler with the allowed operations for the processor.
	 * @param aggrDoc
	 *            The reference document.
	 * @param strSeparator
	 * @param strRelationship
	 *            It defines nodes type to process to find the multiple occurrence for the Relationship node
	 * @return string value
	 */
	private StringBuilder processDocument(IJavaAllUpdatesAggregationHandler aggrHandler, IAggregationDocument aggrDoc) {
		ArrayDeque<ArrayDeque<IAggregationDocument>> arrDequePaths = new ArrayDeque<>();
		List<IAggregationDocument> listConnectedDocs = getPathEnd(aggrHandler, aggrDoc);
		for (IAggregationDocument aggrDocConnected : listConnectedDocs) {
			ArrayDeque<IAggregationDocument> arrDequePath = new ArrayDeque<>();
			arrDequePath.push(aggrDoc);
			arrDequePath.push(aggrDocConnected);
			arrDequePaths.push(arrDequePath);
		}
		StringBuilder strBuildPaths = new StringBuilder();
		while (!arrDequePaths.isEmpty()) {
			ArrayDeque<IAggregationDocument> arrDequePath = arrDequePaths.pop();
			IAggregationDocument aggrDocNew = arrDequePath.peek();
			listConnectedDocs = getPathEnd(aggrHandler, aggrDocNew);
			if (listConnectedDocs == null || listConnectedDocs.isEmpty()) {
				strBuildPaths.append(computePath(arrDequePath)).append(INDEXED_VALUE_SEPARATOR);
			} else {
				for (IAggregationDocument aggrConnectedDoc : listConnectedDocs) {
					ArrayDeque<IAggregationDocument> arrDequeClonedPath = arrDequePath.clone();
					arrDequeClonedPath.push(aggrConnectedDoc);
					arrDequePaths.push(arrDequeClonedPath);
				}
			}
		}
		return strBuildPaths;
	}

	/**
	 * @param aggregationHandler
	 *            The aggregation handler with the allowed operations for the processor.
	 * @param aggregationDocument
	 *            reference document.
	 * @return empty list value
	 */
	private List<IAggregationDocument> getPathEnd(IJavaAllUpdatesAggregationHandler aggregationHandler, IAggregationDocument aggregationDocument) {
		String strRelationshipName = null;
		strRelationshipName = NODE_ARC_MAP.get(aggregationDocument.getMeta(META_TYPE));
		logger.info("DOC TYPE = " + aggregationDocument.getMeta(META_TYPE));
		logger.info("DOC NAME = " + aggregationDocument.getMeta(META_NAME));
		logger.info("DOC REL AS FETCHED FROM MAP = " + NODE_ARC_MAP.get(aggregationDocument.getMeta(META_TYPE)));
		List<IAggregationDocument> listTempDoc = null;
		if (strRelationshipName != null && !strRelationshipName.isEmpty()) {
			listTempDoc = aggregationHandler.matchPathEnd(aggregationDocument, strRelationshipName);
		}
		return listTempDoc;
	}

	/**
	 * @param arrDequePath
	 *            represents a last-in-first-out (LIFO) stack of objects. The usual {@code push} and {@code pop} operations are provided, as well as a
	 *            method to {@code peek} at the top item on the stack
	 * @return
	 */
	private StringBuilder computePath(ArrayDeque<IAggregationDocument> arrDequePath) {
		StringBuilder strBuildPathWithPIDs = new StringBuilder();
		StringBuilder strBuildPathWithNames = new StringBuilder();
		for (IAggregationDocument aggrDoc : arrDequePath) {
			strBuildPathWithPIDs.append(aggrDoc.getMeta(META_PID));
			if (REL_LIST.contains(aggrDoc.getMeta(META_TYPE))) {
				strBuildPathWithNames.append(aggrDoc.getMeta(META_INSTANCE_EXT_ID));
				strBuildPathWithNames.append(INST_REF_SEPARATOR);
				strBuildPathWithPIDs.append(INST_REF_SEPARATOR);
			} else {
				strBuildPathWithNames.append(aggrDoc.getMeta(META_NAME));
				strBuildPathWithNames.append(INST_REF_PAIR_SEPARATOR);
				strBuildPathWithPIDs.append(INST_REF_PAIR_SEPARATOR);
			}
		}
		return new StringBuilder(strBuildPathWithNames.substring(0, strBuildPathWithNames.length() - 1)).append(NAME_PATH_PID_PATH_SEPARATOR)
				.append(strBuildPathWithPIDs.substring(0, strBuildPathWithPIDs.length() - 1));
	}

	@Override
	public String getAggregationDocumentType() {
		// TODO Auto-generated method stub
		return workPlanConfig.getTargetNode();
	}
}
