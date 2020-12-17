package com.ferguson.feedengine.batch.partition;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.ferguson.feedengine.batch.JobConstants;

public class MultiResourceFilesPartitioner implements Partitioner, JobConstants {

	private Resource[] resources = new Resource[0];

	public void setResources(Resource[] resources) {
		this.resources = resources;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> map = new HashMap<>(gridSize);
		int i = 0;
		for (Resource resource : resources) {
			ExecutionContext context = new ExecutionContext();
			Assert.state(resource.exists(), "Resource does not exist: " + resource);
			try {
				context.putString(STEP_PARAM_NAME_FILE_NAME, resource.getURI().toString());
				map.put("partition" + i, context);
				i++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		return map;
	}
}