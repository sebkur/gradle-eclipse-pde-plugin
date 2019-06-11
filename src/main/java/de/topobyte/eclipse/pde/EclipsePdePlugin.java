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

import java.util.List;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.plugins.ide.eclipse.EclipsePlugin;
import org.gradle.plugins.ide.eclipse.GenerateEclipseProject;
import org.gradle.plugins.ide.eclipse.model.BuildCommand;
import org.gradle.plugins.ide.eclipse.model.EclipseProject;

public class EclipsePdePlugin implements Plugin<Project>
{

	@Override
	public void apply(final Project project)
	{
		Logger logger = project.getLogger();
		logger.info("applying Eclipse PDE plugin");

		if (!project.getPlugins().hasPlugin(EclipsePlugin.class)) {
			logger.error("Please enable the Eclipse plugin.");
			throw new IllegalStateException("No Eclipse plugin detected.");
		}

		project.getTasks().findByName("eclipseProject").doFirst(e -> {
			GenerateEclipseProject gep = (GenerateEclipseProject) e;
			EclipseProject projectModel = gep.getProjectModel();

			projectModel.getNatures().add("org.eclipse.pde.PluginNature");
			List<BuildCommand> buildCommands = projectModel.getBuildCommands();
			buildCommands
					.add(new BuildCommand("org.eclipse.pde.ManifestBuilder"));
			buildCommands
					.add(new BuildCommand("org.eclipse.pde.SchemaBuilder"));
		});

		EclipsePdePluginExtension extension = project.getExtensions()
				.create("eclipsePde", EclipsePdePluginExtension.class);

		EclipsePdeTask task = project.getTasks().create("eclipsePde",
				EclipsePdeTask.class);
		task.setConfiguration(extension);

		EclipsePdeCleanTask cleanTask = project.getTasks()
				.create("eclipsePdeClean", EclipsePdeCleanTask.class);
		cleanTask.setConfiguration(extension);
	}

}
