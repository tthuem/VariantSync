package de.ovgu.variantsync.presentationlayer.view;

import java.beans.PropertyChangeEvent;

/**
 * MVC-Part to catch events. Model fires events and view catches these events.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 18.05.2015
 */
public interface AbstractView {

	void modelPropertyChange(PropertyChangeEvent evt);
}