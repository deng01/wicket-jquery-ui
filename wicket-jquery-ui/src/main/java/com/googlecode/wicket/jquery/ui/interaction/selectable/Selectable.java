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
package com.googlecode.wicket.jquery.ui.interaction.selectable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.JQueryContainer;
import com.googlecode.wicket.jquery.ui.JQueryIcon;
import com.googlecode.wicket.jquery.ui.interaction.draggable.Draggable;

/**
 * Provides a jQuery UI selectable {@link JQueryContainer}.<br/>
 * Children of that container can be selected using the mouse or by pressing ctrl+click<br/>
 * Usage:
 * 
 * <pre>
 * &lt;ul wicket:id="selectable"&gt;
 * 	&lt;li wicket:id="items"&gt;
 * 		&lt;span wicket:id="item"&gt;[label]&lt;/span&gt;
 * 	&lt;/li&gt;
 * &lt;/ul&gt;
 * 
 * 
 * final Selectable&lt;String&gt; selectable = new Selectable&lt;String&gt;("selectable", list) {
 * 
 * 	protected void onSelect(AjaxRequestTarget target, List&lt;String&gt; items)
 * 	{
 * 		//items: gets the selected item
 * 	}
 * };
 * 
 * this.add(selectable);
 * 
 * // ... //
 * 
 * //selectable.getSelectedItems(): gets the selected items
 * </pre>
 * 
 * @param <T> the type of the model object
 * @author Sebastien Briquet - sebfz1
 * 
 */
public class Selectable<T extends Serializable> extends JQueryContainer implements ISelectableListener<T>
{
	private static final long serialVersionUID = 1L;

	private List<T> items = null; // selected items

	/**
	 * Constructor
	 * 
	 * @param id the markup id
	 * @param list the list the {@link Selectable} should observe.
	 */
	public Selectable(String id, List<T> list)
	{
		this(id, new ListModel<T>(list));
	}

	/**
	 * Constructor
	 * 
	 * @param id the markup id
	 * @param model the list the {@link Selectable} should observe.
	 */
	public Selectable(String id, IModel<List<T>> model)
	{
		super(id, model);
	}

	// Properties //
	
	@SuppressWarnings("unchecked")
	public List<T> getModelObject()
	{
		return (List<T>) this.getDefaultModelObject();
	}

	/**
	 * Gets the selector that identifies the selectable item within a {@link Selectable}<br/>
	 * The selector should be the path from the {@link Selectable} to the item (for instance '#myUL LI', where '#myUL' is the {@link Selectable}'s selector)
	 * 
	 * @return "li" by default
	 */
	protected String getItemSelector()
	{
		return "li";
	}

	/**
	 * Gets the selected items
	 * 
	 * @return selected items
	 */
	public List<T> getSelectedItems()
	{
		if (this.items != null)
		{
			return this.items;
		}

		return Collections.emptyList();
	}

	public void setSelectedItems(List<T> items)
	{
		this.items = items;
	}

	// Events //

	/**
	 * Triggered when a selection has been made (stops)
	 * 
	 * @param target the {@link AjaxRequestTarget}
	 * @param items the {@link List} of selected items
	 */
	@Override
	public void onSelect(AjaxRequestTarget target, List<T> items)
	{
		// noop
	}

	// IJQueryWidget //
	
	@Override
	public JQueryBehavior newWidgetBehavior(String selector)
	{
		return new SelectableBehavior<T>(selector) {

			private static final long serialVersionUID = 1L;

			@Override
			protected List<T> getItemList()
			{
				return Selectable.this.getModelObject();
			}

			@Override
			protected String getItemSelector()
			{
				return Selectable.this.getItemSelector();
			}

			@Override
			public void onSelect(AjaxRequestTarget target, List<T> items)
			{
				Selectable.this.setSelectedItems(items);
				Selectable.this.onSelect(target, items);
			}
		};
	}

	// DraggableFactory methods //

	/**
	 * Creates a {@link Draggable} object that is related to this {@link Selectable}.<br/>
	 * Uses a default factory that will create a {@link Draggable} with a <code>ui-icon-arrow-4-diag</code> icon
	 * 
	 * @param id the markup id
	 * @return the {@link Draggable}
	 */
	public Draggable<?> createDraggable(String id)
	{
		return this.createDraggable(id, new DefaultDraggableFactory());
	}

	/**
	 * Creates a {@link Draggable} object that is related to this {@link Selectable}
	 * 
	 * @param id the markup id
	 * @param factory the {@link SelectableDraggableFactory} instance
	 * @return the {@link Draggable}
	 */
	public Draggable<?> createDraggable(String id, SelectableDraggableFactory factory)
	{
		return factory.create(id, JQueryWidget.getSelector(this)); // let throw a NPE if no factory is defined
	}

	// Default Draggable Factory //
	
	/**
	 * Default {@link SelectableDraggableFactory} implementation which will create a {@link Draggable} with a {@link JQueryIcon#ARROW_4_DIAG} icon
	 */
	class DefaultDraggableFactory extends SelectableDraggableFactory
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected Draggable<?> create(String id, String selector, final String helper)
		{
			Draggable<T> draggable = new Draggable<T>(id) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onConfigure(JQueryBehavior behavior)
				{
					super.onConfigure(behavior);

					behavior.setOption("helper", helper);
				}
			};

			draggable.add(AttributeModifier.append("class", "ui-icon " + JQueryIcon.ARROW_4_DIAG));
			draggable.add(AttributeModifier.append("style", "display: inline-block; background-position: -16px -80px !important;")); // The background position is the same as ui-icon-arrow-4-diag. It is marked as important for the icon to not disappear while selecting over it.

			return draggable;
		}
	}
}
