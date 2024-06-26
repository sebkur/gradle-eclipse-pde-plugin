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

import org.gradle.api.internal.ConventionTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Internal;

public abstract class AbstractEclipsePdeTask extends ConventionTask
{

	protected final Logger logger = getLogger();

	@Internal
	protected EclipsePdePluginExtension configuration;

	public EclipsePdePluginExtension getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(EclipsePdePluginExtension configuration)
	{
		this.configuration = configuration;
	}

}
