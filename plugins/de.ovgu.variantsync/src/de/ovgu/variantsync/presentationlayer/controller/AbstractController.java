package de.ovgu.variantsync.presentationlayer.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ovgu.variantsync.applicationlayer.AbstractModel;
import de.ovgu.variantsync.presentationlayer.view.AbstractView;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;

/**
 * Provides functions to register and remove views and models at controller. All
 * registered views receive events fired by models.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 18.05.2015
 */
public abstract class AbstractController implements PropertyChangeListener {

	private List<AbstractView> registeredViews;
	private List<AbstractModel> registeredModels;
	private static final String ERROR_MESSAGE = "Error appeared in called method.";

	/**
	 * Initializes view and model list.
	 */
	public AbstractController() {
		registeredViews = new CopyOnWriteArrayList<AbstractView>();
		registeredModels = new CopyOnWriteArrayList<AbstractModel>();
	}

	/**
	 * Register a model. Models are able to fire events. Controller sends these
	 * events to registered listeners.
	 * 
	 * @param model
	 *            the model to register
	 */
	public void addModel(AbstractModel model) {
		registeredModels.add(model);
		model.addPropertyChangeListener(this);
	}

	/**
	 * Removes a model from controller. Removed model no longer able to fire
	 * events.
	 * 
	 * @param model
	 *            the model to remove
	 */
	public void removeModel(AbstractModel model) {
		registeredModels.remove(model);
		model.removePropertyChangeListener(this);
	}

	/**
	 * Register a view. Views are able to receive events.
	 * 
	 * @param view
	 *            the view to register
	 */
	public void addView(AbstractView view) {
		registeredViews.add(view);
	}

	/**
	 * Removes a view from controller. Removed views no longer receive events.
	 * 
	 * @param view
	 *            the view to remove
	 */
	public void removeView(AbstractView view) {
		registeredViews.remove(view);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		for (AbstractView view : registeredViews) {
			view.modelPropertyChange(evt);
		}
	}

	/**
	 * Searches in all registered models for the specified method to call.
	 * Method is called via reflection. Calling method do not has arguments.
	 * 
	 * @param propertyName
	 *            method name to call
	 */
	protected void setModelProperty(String propertyName) {

		for (AbstractModel model : registeredModels) {
			try {
				Method method;
				method = model.getClass().getMethod(propertyName);
				method.invoke(model);

			} catch (NoSuchMethodException e) {
				handleNoSuchMethodException(e);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LogOperations.logError(ERROR_MESSAGE, e);
			}
		}
	}

	/**
	 * Searches in all registered models for the specified method to call.
	 * Method is called via reflection. Calling method has one argument.
	 * 
	 * @param propertyName
	 *            method name to call
	 * @param newValue
	 *            argument of called method
	 */
	protected void setModelProperty(String propertyName, Object newValue) {

		for (AbstractModel model : registeredModels) {
			try {
				Method method;
				method = model.getClass().getMethod(propertyName,
						new Class[] { newValue.getClass() });
				method.invoke(model, newValue);
			} catch (NoSuchMethodException e) {
				handleNoSuchMethodException(e);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LogOperations.logError(ERROR_MESSAGE, e);
			}
		}
	}

	/**
	 * Searches in all registered models for the specified method to call.
	 * Method is called via reflection. Calling method has two arguments.
	 * 
	 * @param propertyName
	 *            method name to call
	 * @param firstArgument
	 *            first argument of called method
	 * @param secondArgument
	 *            second argument of called method
	 */
	protected void setModelProperty(String propertyName, Object firstArgument,
			Object secondArgument) {
		for (AbstractModel model : registeredModels) {
			try {
				Method method;
				method = model.getClass().getMethod(
						propertyName,
						new Class[] { firstArgument.getClass(),
								secondArgument.getClass() });
				method.invoke(model, firstArgument, secondArgument);
			} catch (NoSuchMethodException e) {
				handleNoSuchMethodException(e);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LogOperations.logError(ERROR_MESSAGE, e);
			}
		}
	}

	/**
	 * Searches in all registered models for the specified method to call.
	 * Method is called via reflection. Calling method has three arguments.
	 * 
	 * @param propertyName
	 *            method name to call
	 * @param firstArgument
	 *            first argument of called method
	 * @param secondArgument
	 *            second argument of called method
	 * @param thirdArgument
	 *            third argument of called method
	 */
	protected void setModelProperty(String propertyName, Object firstArgument,
			Object secondArgument, Object thirdArgument) {

		for (AbstractModel model : registeredModels) {
			try {
				Method method;
				method = model.getClass().getMethod(
						propertyName,
						new Class[] { firstArgument.getClass(),
								secondArgument.getClass(),
								thirdArgument.getClass() });
				method.invoke(model, firstArgument, secondArgument,
						thirdArgument);
			} catch (NoSuchMethodException e) {
				handleNoSuchMethodException(e);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LogOperations.logError(ERROR_MESSAGE, e);
			}
		}
	}

	/**
	 * Searches in all registered models for the specified method to call.
	 * Method is called via reflection. Calling method has five arguments.
	 * 
	 * @param propertyName
	 *            method name to call
	 * @param firstArgument
	 *            first argument of called method
	 * @param secondArgument
	 *            second argument of called method
	 * @param thirdArgument
	 *            third argument of called method
	 * @param fourthArgument
	 *            fourth argument of called method
	 * @param fifthArgument
	 *            fifth argument of called method
	 */
	protected void setModelProperty(String propertyName, Object firstArgument,
			Object secondArgument, Object thirdArgument, Object fourthArgument,
			Object fifthArgument) {

		for (AbstractModel model : registeredModels) {
			try {
				Method method;
				method = model.getClass().getMethod(
						propertyName,
						new Class[] { firstArgument.getClass(),
								secondArgument.getClass(),
								thirdArgument.getClass(),
								fourthArgument.getClass(),
								fifthArgument.getClass() });
				method.invoke(model, firstArgument, secondArgument,
						thirdArgument, fourthArgument, fifthArgument);
			} catch (NoSuchMethodException e) {
				handleNoSuchMethodException(e);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LogOperations.logError(ERROR_MESSAGE, e);
			}
		}
	}

	/**
	 * Handles NoSuchMethodException. This exception appears if a method do not
	 * exists in a class which was called via reflection. Controller searches in
	 * all registered models for the specified method to call. So, this
	 * exception often appears and has not worse effects.
	 * 
	 * @param e
	 *            NoSuchMethodException
	 */
	private void handleNoSuchMethodException(NoSuchMethodException e) {
		// ignore exception
	}
}
