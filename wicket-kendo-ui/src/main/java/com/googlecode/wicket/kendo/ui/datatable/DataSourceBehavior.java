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
package com.googlecode.wicket.kendo.ui.datatable;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONStringer;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.convert.ConversionException;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.datatable.column.IColumn;
import com.googlecode.wicket.kendo.ui.datatable.column.PropertyColumn;

/**
 * Provides the {@link DataTable} data source {@link AbstractDefaultAjaxBehavior}
 *
 * @param <T> the type of the model object
 * @author Sebastien Briquet - sebfz1
 */
class DataSourceBehavior<T> extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;
	private static final String ASC = "asc";

	/** The max number of filtered column */
	private static final int COLS = 20;

	private final IDataProvider<T> provider;
	private final List<? extends IColumn> columns;

	/**
	 * Constructor
	 *
	 * @param columns the list of {@link IColumn}
	 * @param provider the {@link IDataProvider}
	 */
	public DataSourceBehavior(final List<? extends IColumn> columns, final IDataProvider<T> provider)
	{
		this.columns = columns;
		this.provider = provider;
	}

	@SuppressWarnings("unchecked")
	protected void setSort(String property, SortOrder order)
	{
		ISortStateLocator<String> locator = ((ISortStateLocator<String>) this.provider);

		locator.getSortState().setPropertySortOrder(property, order);
	}

	@Override
	protected void respond(AjaxRequestTarget target)
	{
		final RequestCycle requestCycle = RequestCycle.get();
		final IRequestParameters parameters = requestCycle.getRequest().getQueryParameters();

		final int first = parameters.getParameterValue("skip").toInt(0);
		final int count = parameters.getParameterValue("take").toInt(0);

		// ISortStateLocator //
		if (this.provider instanceof ISortStateLocator)
		{
			String property = parameters.getParameterValue("sort[0][field]").toOptionalString();
			String direction = parameters.getParameterValue("sort[0][dir]").toOptionalString();

			if (property != null)
			{
				this.setSort(property, direction == null ? SortOrder.NONE : direction.equals(ASC) ? SortOrder.ASCENDING : SortOrder.DESCENDING);
			}
		}

		// IFilterStateLocator //
		if (this.provider instanceof IFilterStateLocator)
		{
			String fieldPattern = "filter[filters][%d][field]";
			String valuePattern = "filter[filters][%d][value]";

			@SuppressWarnings("unused")
			String logicPattern = "filter[logic]";
			@SuppressWarnings("unused")
			String operatorPattern = "filter[filters][%d][operator]";
			// TODO: implement logic & operator (new IFilterStateLocator interface?)

			@SuppressWarnings("unchecked")
			T object = ((IFilterStateLocator<T>) this.provider).getFilterState();
			PropertyResolverConverter converter = this.newPropertyResolverConverter();

			for (int i = 0; i < COLS; i++)
			{
				String field = parameters.getParameterValue(String.format(fieldPattern, i)).toOptionalString();
				String value = parameters.getParameterValue(String.format(valuePattern, i)).toOptionalString();

				if (field != null)
				{
					PropertyResolver.setValue(field, object, value, converter);
				}
				else
				{
					break;
				}

			}
		}

		final IRequestHandler handler = this.newRequestHandler(first, count);
		requestCycle.scheduleRequestHandlerAfterCurrent(handler);
	}

	/**
	 * Get a new {@link PropertyResolverConverter}
	 *
	 * @return a new {@link PropertyResolverConverter}
	 */
	protected PropertyResolverConverter newPropertyResolverConverter()
	{
		return new PropertyResolverConverter(Application.get().getConverterLocator(), Session.get().getLocale());
	}

	/**
	 * Gets the new {@link IRequestHandler} that will respond the data in a json format
	 *
	 * @param first the first row number
	 * @param count the count of rows
	 * @return a new {@link IRequestHandler}
	 */
	private IRequestHandler newRequestHandler(final int first, final int count)
	{
		return new IRequestHandler() {

			@Override
			public void respond(final IRequestCycle requestCycle)
			{
				WebResponse response = (WebResponse) requestCycle.getResponse();

				final String encoding = Application.get().getRequestCycleSettings().getResponseRequestEncoding();
				response.setContentType("text/json; charset=" + encoding);
				response.disableCaching();

				final long size = provider.size();
				final Iterator<? extends T> iterator = provider.iterator(first, count);

				// builds JSON result //
				StringBuilder builder = new StringBuilder();

				builder.append("{ ");
				builder.append(Options.QUOTE).append("__count").append(Options.QUOTE).append(": ").append(size).append(", ");
				builder.append(Options.QUOTE).append("results").append(Options.QUOTE).append(": ");
				builder.append("[ ");

				for (int index = 0; iterator.hasNext(); index++)
				{
					if (index > 0)
					{
						builder.append(", ");
					}

					builder.append(DataSourceBehavior.this.newJsonRow(iterator.next()));
				}

				builder.append(" ] }");

				response.write(builder);
			}

			@Override
			public void detach(final IRequestCycle requestCycle)
			{
				provider.detach();
			}
		};
	}

	/**
	 * Gets a new JSON object from the bean
	 *
	 * @param bean T object
	 * @return a new JSON object
	 */
	protected String newJsonRow(T bean)
	{
		JSONStringer stringer = new JSONStringer();

		try
		{
			stringer.object();

			for (IColumn column : this.columns)
			{
				if (column instanceof PropertyColumn)
				{
					PropertyColumn pc = (PropertyColumn) column;
					stringer.key(pc.getField()).value(pc.getValue(bean));
				}
			}

			stringer.endObject();
		}
		catch (JSONException e)
		{
			throw new ConversionException(e);
		}

		return stringer.toString();
	}
}
