/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2017  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.tubs.variantsync.core.utilities.event;

/**
 * Event triggered by changes to a feature model or its elements. <br/>
 * <br/>
 * Each event contains the following information:
 * <ul>
 * <li>an event type which determine the kind of event</li>
 * <li>the sender (source) of this event, i.e., which object fired this
 * event</li>
 * <li>the old value (if available), and the new value</li>
 * </ul>
 * <br/>
 * <br/>
 * This events are intended to be processed by {@link IEventListener} instances.
 * <br/>
 * <br/>
 * For usage to fire <code>VariantSyncEvent</code>s, see
 * {@link ConfigurationProject#fireEvent(VariantSyncEvent)}.
 * 
 * @author Sebastian Krieter
 * @author Marcus Pinnecke
 * @author Christopher Sontag
 */
public class VariantSyncEvent {

	/**
	 * Typing of the event instance. This type have to be used in order to
	 * distinguish of the event kind.
	 */
	public enum EventType {
		/**
		 * The context was changed.
		 */
		CONTEXT_CHANGED,
		/**
		 * A feature expression was added
		 */
		FEATURECONTEXT_ADDED,
		/**
		 * A feature expression was changed
		 */
		FEATURECONTEXT_CHANGED,
		/**
		 * A feature expression was removed
		 */
		FEATURECONTEXT_REMOVED,
		/**
		 * Recording was started
		 */
		CONTEXT_RECORDING_START,
		/**
		 * Recording was stopped
		 */
		CONTEXT_RECORDING_STOP,
		/**
		 * The actual patch was changed
		 */
		PATCH_CHANGED,
		/**
		 * A new patch has been opened
		 */
		PATCH_ADDED,
		/**
		 * A patch has been closed
		 */
		PATCH_CLOSED,
		/**
		 * A configuration project is setted
		 */
		CONFIGURATIONPROJECT_SET,
		/**
		 * The configuration project has changed
		 */
		CONFIGURATIONPROJECT_CHANGED,
		/**
		 * A variant was added
		 */
		VARIANT_ADDED,
		/**
		 * A variant was removed
		 */
		VARIANT_REMOVED,
		/**
		 * Call after all projects are initalized
		 */
		INITALIZED
	}

	static VariantSyncEvent[] defaultEvents = new VariantSyncEvent[EventType.values().length];
	static {
		for (EventType e : EventType.values()) {
			defaultEvents[e.ordinal()] = new VariantSyncEvent(e);
		}
	}

	public static VariantSyncEvent getDefault(final EventType e) {
		return defaultEvents[e.ordinal()];
	}

	private final Object source;
	private final EventType eventType;
	private final Object oldValue;
	private final Object newValue;

	private VariantSyncEvent(EventType e) {
		this(null, e);
	}

	public VariantSyncEvent(Object source, EventType eventType) {
		this(source, eventType, null, null);
	}

	public VariantSyncEvent(Object source, EventType eventType, Object oldValue, Object newValue) {
		this.source = source;
		this.eventType = eventType;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Object getSource() {
		return source;
	}

	public EventType getEventType() {
		return eventType;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	@Override
	public String toString() {
		return "VariantSyncEvent [source=" + source + ", eventType=" + eventType + ", oldValue=" + oldValue
				+ ", newValue=" + newValue + "]";
	}

}
