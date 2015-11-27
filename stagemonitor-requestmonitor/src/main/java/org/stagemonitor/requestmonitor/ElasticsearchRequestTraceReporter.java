package org.stagemonitor.requestmonitor;

import java.util.Collection;

import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.core.elasticsearch.ElasticsearchClient;
import org.stagemonitor.core.util.StringUtils;

/**
 * An implementation of {@link RequestTraceReporter} that sends the {@link RequestTrace} to Elasticsearch
 */
public class ElasticsearchRequestTraceReporter implements RequestTraceReporter {

	private final CorePlugin corePlugin;
	private final RequestMonitorPlugin requestMonitorPlugin;
	private final ElasticsearchClient elasticsearchClient;

	public ElasticsearchRequestTraceReporter() {
		this(Stagemonitor.getConfiguration(CorePlugin.class), Stagemonitor.getConfiguration(RequestMonitorPlugin.class),
				Stagemonitor.getConfiguration().getConfig(CorePlugin.class).getElasticsearchClient());
	}

	public ElasticsearchRequestTraceReporter(CorePlugin corePlugin, RequestMonitorPlugin requestMonitorPlugin,
											 ElasticsearchClient elasticsearchClient) {
		this.corePlugin = corePlugin;
		this.requestMonitorPlugin = requestMonitorPlugin;
		this.elasticsearchClient = elasticsearchClient;
	}

	@Override
	public <T extends RequestTrace> void reportRequestTrace(T requestTrace) {
		final Collection<String> onlyReportRequestsWithName = requestMonitorPlugin.getOnlyReportRequestsWithNameToElasticsearch();
		if (onlyReportRequestsWithName.isEmpty() || onlyReportRequestsWithName.contains(requestTrace.getName())) {
			elasticsearchClient.index("stagemonitor-requests-" + StringUtils.getLogstashStyleDate(), "requests", requestTrace);
		}
	}

	@Override
	public <T extends RequestTrace> boolean isActive(T requestTrace) {
		return StringUtils.isNotEmpty(corePlugin.getElasticsearchUrl());
	}
}
