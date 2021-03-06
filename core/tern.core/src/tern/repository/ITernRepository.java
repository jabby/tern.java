/**
 *  Copyright (c) 2013-present Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package tern.repository;

import java.io.File;

import tern.TernException;
import tern.server.ITernModule;
import tern.server.ITernPlugin;

/**
 * Tern repository is a local base dir which contains the tern.js JS files :
 * 
 * <ul>
 * <li>bin folder</li>
 * <li>defs folder</li>
 * <li>plugin folder</li>
 * <li>node_modules folder</li>
 * </ul>
 *
 */
public interface ITernRepository {

	/**
	 * Returns the tern repository name.
	 * 
	 * @return the tern repository name.
	 */
	String getName();

	/**
	 * Returns the tern base dir which contains ternjs and their custom plugins.
	 * 
	 * @return the tern base dir which contains ternjs and their custom plugins.
	 */
	File getTernBaseDir();

	/**
	 * Returns the tern base dir which contains ternjs and their custom plugins
	 * as string.
	 * 
	 * @return the tern base dir which contains ternjs and their custom plugins
	 *         as string.
	 */
	String getTernBaseDirAsString();

	/**
	 * Update the tern base dir which contains ternjs and their custom plugins.
	 * 
	 * @param ternFile
	 */
	void setTernBaseDir(File ternFile);

	/**
	 * Returns true if the repository is a default repository and false
	 * otherwise (custom repository).
	 * 
	 * @return true if the repository is a default repository and false
	 *         otherwise (custom repository).
	 */
	boolean isDefault();

	/**
	 * Refresh the tern modules of this repository;
	 */
	void refresh();

	/**
	 * Returns the tern modules (plugins and JSON type defitions).
	 * 
	 * @return the tern modules (plugins and JSON type defitions).
	 * @throws TernException
	 */
	ITernModule[] getModules() throws TernException;

	/**
	 * Returns the file of the given module and null otherwise.
	 * 
	 * @param module
	 * @return the file of the given module and null otherwise.
	 */
	File getFile(ITernModule module);

	/**
	 * Returns the module by name and null otherwise.
	 * 
	 * @param name
	 * @return the module by name and null otherwise.
	 */
	ITernModule getModule(String name);

	/**
	 * Returns the module by origin and null otherwise.
	 * 
	 * @param origin
	 * @return the module by origin and null otherwise.
	 */
	ITernModule getModuleByOrigin(String origin);

	/**
	 * Returns the list fo tern plugin which are linter.
	 * 
	 * @return the list fo tern plugin which are linter.
	 */
	ITernPlugin[] getLinters();
}
