package com.ferguson.feedengine.batch.partition;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class MultiResourceFilesPartitioner implements Partitioner {

	private Resource[] resources = new Resource[0];

	public void setResources(Resource[] resources) {
		this.resources = resources;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> map = new HashMap<>(gridSize);
		int i = 0, k = 1;
		for (Resource resource : resources) {
			ExecutionContext context = new ExecutionContext();
			Assert.state(resource.exists(), "Resource does not exist: " + resource);
			context.putString("fileName", resource.getFilename());
			map.put("partition" + i, context);
			i++;
		}
		return map;
	}
}