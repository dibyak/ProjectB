package boeing.cloudview.aggregation;

import com.exalead.config.bean.IsMandatory;
import com.exalead.config.bean.PropertyDescription;
import com.exalead.config.bean.PropertyLabel;
import com.exalead.mercury.component.config.CVComponentConfig;
import com.exalead.mercury.component.config.CVComponentConfigClass;

/**
 * Sample aggregation processor config
 */
@PropertyLabel(value = "Multi Occurence Aggregation Processor For Work Plan")
@CVComponentConfigClass(configClass = MultiOccurenceAggregationProcessorForWPConfig.class)
public class MultiOccurenceAggregationProcessorForWPConfig implements CVComponentConfig {

	
	private String sTargetMeta;
	private String sTargetNode;

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
	@PropertyLabel("Target Node")
	@PropertyDescription("Processor call for this Node")
	public void setTargetNode(String sTargetNode) {
		this.sTargetNode = sTargetNode;
	}

	@Override
	public String toString() {
		return "MultiOccurenceAggregationProcessorForWPConfig [sTargetMeta=" + sTargetMeta + ", sTargetNode="
				+ sTargetNode + "]";
	}

}
