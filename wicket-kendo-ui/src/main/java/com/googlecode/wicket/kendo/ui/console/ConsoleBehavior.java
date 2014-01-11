/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.wicket.kendo.ui.console;

import java.io.Serializable;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.settings.IJavaScriptLibrarySettings;

import com.googlecode.wicket.jquery.core.JQueryAbstractBehavior;
import com.googlecode.wicket.kendo.ui.settings.ConsoleLibrarySettings;
import com.googlecode.wicket.kendo.ui.settings.IConsoleLibrarySettings;

/**
 * Provides the Kendo UI console behavior
 *
 * @author Sebastien Briquet - sebfz1
 */
public abstract class ConsoleBehavior extends JQueryAbstractBehavior
{
	private static final long serialVersionUID = 1L;
	private static final String CSS_CLASS = "console";

	/**
	 * Gets the {@link IConsoleLibrarySettings}
	 *
	 * @return Default internal {@link ConsoleLibrarySettings} instance if {@link Application}'s {@link IJavaScriptLibrarySettings} is not an instance of {@link IConsoleLibrarySettings}
	 */
	private static IConsoleLibrarySettings getLibrarySettings()
	{
		if (Application.exists() && (Application.get().getJavaScriptLibrarySettings() instanceof IConsoleLibrarySettings))
		{
			return (IConsoleLibrarySettings) Application.get().getJavaScriptLibrarySettings();
		}

		return ConsoleLibrarySettings.get();
	}

	/**
	 * Constructor
	 */
	public ConsoleBehavior()
	{
		super("console");

		this.initReferences();
	}

	/**
	 * Initializes CSS & JavaScript resource references
	 */
	private void initReferences()
	{
		IConsoleLibrarySettings settings = getLibrarySettings();

		// console.css //
		if (settings.getConsoleStyleSheetReference() != null)
		{
			this.add(settings.getConsoleStyleSheetReference());
		}

		// console.js //
		if (settings.getConsoleJavaScriptReference() != null)
		{
			this.add(settings.getConsoleJavaScriptReference());
		}
	}

	@Override
	public void bind(Component component)
	{
		super.bind(component);

		component.add(AttributeModifier.append("class", CSS_CLASS));
	}

	/**
	 * Formats the message (escaping, etc)
	 *
	 * @param message the message to format
	 * @param error indicates whether the message is an error message
	 * @return the formated message
	 */
	protected abstract String format(Serializable message, boolean error);

	/**
	 * Gets the jQuery statement that logs the message<br/>
	 * <b>Warning: </b> This method is *not* called by the behavior directly (only {@link #$()} is).
	 *
	 * @param message the message to log
	 * @param error indicates whether the message is an error message
	 * @return the jQuery statement
	 */
	public String $(Serializable message, boolean error)
	{
		return String.format("kendoConsole.log('%s', %b);", this.format(message, error), error);
	}
}
