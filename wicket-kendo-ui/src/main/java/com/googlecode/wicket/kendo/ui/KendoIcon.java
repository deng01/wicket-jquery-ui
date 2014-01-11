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
package com.googlecode.wicket.kendo.ui;

import com.googlecode.wicket.kendo.ui.form.button.Button;

/**
 * Provides some default jQuery icon class. Might be used to decorate a {@link Button} for instance.
 *
 * @author Sebastien Briquet - sebfz1
 *
 */
public class KendoIcon
{
	public static final String NONE = "";

	public static final String ARROW_N = "arrow_n";
	public static final String ARROW_E = "arrow_e";
	public static final String ARROW_S = "arrow_s";
	public static final String ARROW_W = "arrow_w";
	public static final String ARROWHEAD_E = "arrowhead_e";
	public static final String ARROWHEAD_N = "arrowhead_n";
	public static final String ARROWHEAD_S = "arrowhead_s";
	public static final String ARROWHEAD_W = "arrowhead_w";
	public static final String CALENDAR = "calendar";
	public static final String CANCEL = "cancel";
	public static final String CLOCK = "clock";
	public static final String CLOSE = "close";
	public static final String COLLAPSE = "collapse";
	public static final String COLLAPSE_W = "collapse_w";
	public static final String COLUMNS = "columns";
	public static final String CUSTOM = "custom";
	public static final String EXCEPTION = "exception";
	public static final String EXPAND = "expand";
	public static final String EXPAND_W = "expand_w";
	public static final String FOLDER_ADD = "folder_add";
	public static final String FOLDER_UP = "folder_up";
	public static final String FUNNEL = "funnel";
	public static final String FUNNEL_CLEAR = "funnel_clear";
	public static final String GROUP = "group";
	public static final String INSERT_ = "insert_";
	public static final String INSERT_M = "insert_m";
	public static final String INSERT_N = "insert_n";
	public static final String INSERT_S = "insert_s";
	public static final String MAXIMIZE = "maximize";
	public static final String MINIMIZE = "minimize";
	public static final String NOTE = "note";
	public static final String PENCIL = "pencil";
	public static final String PIN = "pin";
	public static final String PLUS = "plus";
	public static final String REFRESH = "refresh";
	public static final String RESTORE = "restore";
	public static final String SEARCH = "search";
	public static final String SEEK_E = "seek_e";
	public static final String SEEK_N = "seek_n";
	public static final String SEEK_S = "seek_s";
	public static final String SEEK_W = "seek_w";
	public static final String SORT_ASC = "sort_asc";
	public static final String SORT_DESC = "sort_desc";
	public static final String TICK = "tick";
	public static final String UNGROUP = "ungroup";
	public static final String UNPIN = "unpin";

	/**
	 * Constants class
	 */
	private KendoIcon()
	{
	}

	/**
	 * Indicates whether the icon is {@link KendoIcon#NONE}
	 *
	 * @param icon the icon
	 * @return true or false
	 */
	public static boolean isNone(String icon)
	{
		return KendoIcon.NONE.equals(icon);
	}
}
