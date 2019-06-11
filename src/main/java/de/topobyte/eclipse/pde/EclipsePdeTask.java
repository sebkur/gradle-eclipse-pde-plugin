// Copyright 2019 Sebastian Kuerten
//
// This file is part of gradle-eclipse-pde-plugin.
//
// gradle-eclipse-pde-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// gradle-eclipse-pde-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with gradle-eclipse-pde-plugin. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.eclipse.pde;

import java.nio.file.Path;
import java.util.Set;
import java.util.SortedSet;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.component.external.model.ModuleComponentArtifactIdentifier;
import org.gradle.plugins.ide.eclipse.EclipsePlugin;

public class EclipsePdeTask extends AbstractEclipsePdeTask
{

	public EclipsePdeTask()
	{
		dependsOn(EclipsePlugin.ECLIPSE_TASK_NAME);
		setGroup("build");
	}

	@TaskAction
	protected void configure()
	{
		Project project = getProject();

		if (configuration.isDebug()) {
			SortedSet<String> names = project.getConfigurations().getNames();
			for (String name : names) {
				logger.info("configuration: " + name);
			}
		}

		String configName = getConfiguration().getConfiguration();

		Configuration configuration = project.getConfigurations()
				.getByName(configName);
		DependencySet dependencies = configuration.getAllDependencies();
		dependencies.all(d -> {
			String group = d.getGroup();
			String module = d.getName();
			String version = d.getVersion();
			logger.info(String.format("%s:%s:%s", group, module, version));
		});

		Path buildDir = project.getBuildDir().toPath();
		Path fileBuildProperties = buildDir
				.resolve(Constants.FILE_NAME_BUILD_PROPERTIES);
		logger.lifecycle("creating build.properties: " + fileBuildProperties);

		Set<ResolvedArtifact> artifacts = configuration
				.getResolvedConfiguration().getResolvedArtifacts();
		for (ResolvedArtifact artifact : artifacts) {
			ComponentArtifactIdentifier id = artifact.getId();
			if (id instanceof ModuleComponentArtifactIdentifier) {
				ModuleComponentArtifactIdentifier mcai = (ModuleComponentArtifactIdentifier) id;
				ModuleComponentIdentifier mci = mcai.getComponentIdentifier();
				String group = mci.getGroup();
				String module = mci.getModule();
				String version = mci.getVersion();

				logger.lifecycle(String.format("Normal: %s:%s:%s", group,
						module, version));

				// TODO: create file
			}
		}
	}

}
