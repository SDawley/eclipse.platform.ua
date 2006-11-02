/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help;

import org.eclipse.help.internal.HelpPlugin;

/**
 * An <code>AbstractContentExtensionProvider</code> is a mechanism to provide
 * arbitrary content extensions (e.g. contributions to anchors or element
 * replacements). <code>AbstractContentExtensionProvider</code>s must be
 * registered via the <code>org.eclipse.help.contentExtension</code>
 * extension point.
 * 
 * @since 3.3
 */
public abstract class AbstractContentExtensionProvider {
	
	/**
	 * Returns all extensions for this provider. The document model must
	 * conform to the schema for content extension XML, with the exception that
	 * the <code>content</code> attribute must be a full href with a leading
	 * plug-in id. Providers are free to provide any number of contributions
	 * (zero or more).
	 * 
	 * @param locale the locale for which to get contributions
	 * @return all the content extensions for this provider
	 */
	public abstract Node[] getContentExtensions(String locale);
	
	/**
	 * Notifies the platform that the content managed by this provider may
	 * have changed since the last time <code>getContentExtensions()</code>
	 * was called, and needs to be updated.
	 */
	protected void contentChanged() {
		// will force a reload next time around
		HelpPlugin.getContentExtensionManager().clearCache();
	}
}
