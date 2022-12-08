package boeing.cloudview.aggregation;

import com.exalead.config.bean.IsMandatory;
import com.exalead.config.bean.PropertyDescription;
import com.exalead.config.bean.PropertyLabel;
import com.exalead.mercury.component.config.CVComponentConfig;
import com.exalead.mercury.component.config.CVComponentConfigClass;
/**
 * SGS
 */
@PropertyLabel(value = "Multi Occurence Aggregation Processor For Work Plan")
@CVComponentConfigClass(configClass = MultiOccurenceAggregationProcessorForWPConfig.class)
public class MultiOccurenceAggregationProcessorForWPConfig implements CVComponentConfig {

	private String sRelName;
	private String sTargetMeta;
	private String sTargetNode;

	public String getRelName() {
		return sRelName;
	}

	@IsMandatory(true)
	@PropertyLabel("Nodes Relationship")
	@PropertyDescription("Nodes type to process to find the multiple occurence for the Relationship node.")
	public void setRelName(String rel) {
		this.sRelName = rel;
	}

	public String getTargetMeta() {
		return sTargetMeta;
	}

	@IsMandatory(true)
	@PropertyLabel("Target Meta")
	@PropertyDescription("Result store on the target node.")
	public void setTargetMeta(String sTargetMeta) {
		this.sTargetMeta = sTargetMeta;
	}

	public String getTargetNode() {
		return sTargetNode;
	}

	@IsMandatory(true)
	@PropertyLabel("Document Node")
	@PropertyDescription("Processor call for this Node")
	public void setTargetNode(String sTargetNode) {
		this.sTargetNode = sTargetNode;
	}

	@Override
	public String toString() {
		return "MultiOccurenceAggregationProcessorForWPConfig{" + "rel='" + sRelName + '\'' + ", targetMeta='"
				+ sTargetMeta + '\'' + ", sTargetNode='" + sTargetNode + "'}";
	}
}
