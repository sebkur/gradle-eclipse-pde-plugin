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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.SystemProperties;
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
	protected void configure() throws IOException
	{
		Project project = getProject();

		if (configuration.isDebug()) {
			SortedSet<String> names = project.getConfigurations().getNames();
			for (String name : names) {
				logger.info("configuration: " + name);
			}
		}

		String configName = getConfiguration().getConfiguration();
		logger.lifecycle("configuration: " + configName);

		Configuration config = project.getConfigurations()
				.getByName(configName);
		DependencySet dependencies = config.getAllDependencies();
		dependencies.all(d -> {
			String group = d.getGroup();
			String module = d.getName();
			String version = d.getVersion();
			logger.lifecycle(String.format("Declared: %s:%s:%s", group, module,
					version));
		});

		createBuildProperties(project, config);
		createManifestMf(project, config);
	}

	private void createBuildProperties(Project project, Configuration config)
			throws IOException
	{
		Path projectDir = project.getProjectDir().toPath();
		Path fileBuildProperties = projectDir
				.resolve(Constants.FILE_NAME_BUILD_PROPERTIES);
		logger.lifecycle("creating build.properties: " + fileBuildProperties);

		String ls = SystemProperties.getInstance().getLineSeparator();

		StringBuilder strb = new StringBuilder();
		strb.append("source.. = src/main/java");
		// TODO: use source path from sourceSets config
		strb.append(ls);
		strb.append("output.. = bin/");
		strb.append(ls);
		strb.append("bin.includes = plugin.xml");
		strb.append(",\\");
		strb.append(ls);
		strb.append("               META-INF/");
		strb.append(",\\");
		strb.append(ls);
		strb.append("               .");

		// TODO: allow additional entries via plugin configuration

		Set<ResolvedArtifact> artifacts = config.getResolvedConfiguration()
				.getResolvedArtifacts();
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

				strb.append(",\\");
				strb.append(ls);
				strb.append(String.format("               libs/%s-%s.jar",
						module, version));
			}
		}

		OutputStream output = Files.newOutputStream(fileBuildProperties);
		IOUtils.write(strb.toString(), output, StandardCharsets.UTF_8);
	}

	private void createManifestMf(Project project, Configuration config)
			throws IOException
	{
		Path projectDir = project.getProjectDir().toPath();
		Path fileManifestMf = projectDir.resolve(Constants.PATH_MANIFEST_MF);
		logger.lifecycle("creating MANIFEST.MF: " + fileManifestMf);
		Files.createDirectories(fileManifestMf.getParent());

		Path fileManifestHead = projectDir.resolve("manifest.mf");
		if (!Files.exists(fileManifestHead)) {
			String message = String.format("Please create a file '%s'.",
					fileManifestHead);
			logger.error(message);
			throw new IllegalStateException(message);
		}
		InputStream input = Files.newInputStream(fileManifestHead);
		List<String> lines = IOUtils.readLines(input, StandardCharsets.UTF_8);

		String ls = SystemProperties.getInstance().getLineSeparator();

		StringBuilder strb = new StringBuilder();
		for (String line : lines) {
			strb.append(line);
			strb.append(ls);
		}

		List<String> entries = new ArrayList<>();
		Set<ResolvedArtifact> artifacts = config.getResolvedConfiguration()
				.getResolvedArtifacts();
		for (ResolvedArtifact artifact : artifacts) {
			ComponentArtifactIdentifier id = artifact.getId();
			if (id instanceof ModuleComponentArtifactIdentifier) {
				ModuleComponentArtifactIdentifier mcai = (ModuleComponentArtifactIdentifier) id;
				ModuleComponentIdentifier mci = mcai.getComponentIdentifier();
				String module = mci.getModule();
				String version = mci.getVersion();

				entries.add(String.format("libs/%s-%s.jar", module, version));
			}
		}
		entries.add(".");

		strb.append("Bundle-ClassPath: ");
		strb.append(entries.get(0));
		if (entries.size() > 1) {
			strb.append(",");
		}
		strb.append(ls);
		for (int i = 1; i < entries.size(); i++) {
			strb.append(" ");
			strb.append(entries.get(i));
			if (i < entries.size() - 1) {
				strb.append(",");
			}
			// we need the line break at the end of the file, too
			strb.append(ls);
		}

		OutputStream output = Files.newOutputStream(fileManifestMf);
		IOUtils.write(strb.toString(), output, StandardCharsets.UTF_8);
	}

}
