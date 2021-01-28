package com.ferguson.feedengine.batch.partition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import com.ferguson.feedengine.batch.JobConstants;

import io.micrometer.core.instrument.util.StringUtils;

public class DefaultPartitioner implements Partitioner, JobConstants {

	private List<String> inputs;

	public void setResources(List<String> inputs) {
		this.inputs = inputs;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> map = new HashMap<>(gridSize);
		int i = 0;
		if (null == this.inputs) {
			return map;
		}
		
		for (String input : inputs) {
			if (StringUtils.isBlank(input)) {
				continue;
			}
			ExecutionContext context = new ExecutionContext();
			context.putString(STEP_PARAM_NAME_TASK, input);
			map.put("partition" + i, context);
			i++;
		}
		return map;
	}
}