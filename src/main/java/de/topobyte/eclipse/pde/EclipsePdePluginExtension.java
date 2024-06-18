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

import java.util.ArrayList;
import java.util.List;

public class EclipsePdePluginExtension
{

	private String configuration = "runtimeClasspath";

	private boolean debug = false;

	private List<String> additionalResources = new ArrayList<>();

	public String getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(String configuration)
	{
		this.configuration = configuration;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public List<String> getAdditionalResources()
	{
		return additionalResources;
	}

	public void setAdditionalResources(List<String> additionalResources)
	{
		this.additionalResources = additionalResources;
	}

}
