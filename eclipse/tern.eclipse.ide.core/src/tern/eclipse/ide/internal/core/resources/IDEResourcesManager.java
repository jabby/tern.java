/*
 * Copyright 2014, Genuitec, LLC
 * All Rights Reserved.
 */
package tern.eclipse.ide.internal.core.resources;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

import tern.ITernCacheManager;
import tern.ITernResourcesManagerDelegate;
import tern.ITernFile;
import tern.ITernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.eclipse.ide.internal.core.Trace;
import tern.resources.FilesystemTernFile;
import tern.utils.ExtensionUtils;

public class IDEResourcesManager implements ITernResourcesManagerDelegate {

	static final QualifiedName TERN_PROJECT = new QualifiedName(
			TernCorePlugin.PLUGIN_ID + ".sessionprops", "TernProject"); //$NON-NLS-1$ //$NON-NLS-2$

	static final QualifiedName TERN_FILE = new QualifiedName(
			TernCorePlugin.PLUGIN_ID + ".sessionprops", "TernFile"); //$NON-NLS-1$ //$NON-NLS-2$

	private static IDEResourcesManager instance = new IDEResourcesManager();
	
	private IDEResourcesManager() {
	}
	
	public static IDEResourcesManager getInstance() {
		return instance;
	}

	@Override
	public ITernProject getTernProject(Object obj) {
		if (obj instanceof IProject) {
			IProject project = (IProject) obj;
			try {
				if (!IDETernProject.hasTernNature(project)) {
					return null;
				}
				IDETernProject ternProject = (IDETernProject) project
						.getSessionProperty(TERN_PROJECT);
				if (ternProject == null) {
					ternProject = new IDETernProject(project);
					project.setSessionProperty(TERN_PROJECT, ternProject);
					try {
						ternProject.load();
					} catch (IOException e) {
						Trace.trace(Trace.SEVERE, "Error while loading tern project", e);
					}
				}
				return ternProject;
			} catch (CoreException ex) {
				Trace.trace(Trace.SEVERE, "Error creating " + 
						project.getName() + ": " + ex.getMessage(), ex);
			}
		}
		return null;
	}

	@Override
	public ITernFile getTernFile(ITernProject project, String name) {
		Object file;
		if (name.startsWith(ITernFile.EXTERNAL_PROTOCOL)) {
			file = new File(name.substring(ITernFile.EXTERNAL_PROTOCOL.length() + 1));
		} else if (name.startsWith(ITernFile.PROJECT_PROTOCOL)) {
			file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(
					name.substring(ITernFile.PROJECT_PROTOCOL.length() + 1)));
		} else {
			IProject ip = (IProject) project.getAdapter(IProject.class); 
			file = ip.getFile(name);
		}
		return getTernFile(file);
	}

	@Override
	public ITernFile getTernFile(Object fileObject) {
		if (fileObject instanceof File) {
			File file = (File)fileObject;
			if (file.isFile()) {
				//it is possible that this file maps to a file in workspace
				IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(file.toURI());
				if (files.length == 0 && files[0].exists()) {
					return new FilesystemTernFile(file);
				}
				//create IDETernFile
				fileObject = files[0];
			}
		}
		if (fileObject instanceof IFile) {
			IFile file = (IFile)fileObject;
			ITernFile tf = null;
			try {
				tf = (ITernFile) file.getSessionProperty(TERN_FILE);
			} catch (CoreException e) {
				//ignore
			}
			if (tf == null) {
				tf = IDETernFile.create((IFile)fileObject);
				try {
					file.setSessionProperty(TERN_FILE, tf);
				} catch (CoreException e) {
					//ignore
				}
			}
			return tf;
		}
		return null;
	}

	@Override
	public ITernCacheManager createTernCacheManager(ITernProject project) {
		return new IDETernCacheManager(project);
	}

	protected String getExtension(Object fileObject) {
		if (fileObject instanceof IFile) {
			return ((IFile)fileObject).getFileExtension();
		} else if (fileObject instanceof ITernFile) {
			return ((ITernFile)fileObject).getFileExtension();
		} else if (fileObject instanceof File) {
			return ExtensionUtils.getFileExtension(((File)fileObject).getName());
		}
		return null;
	}
	
	@Override
	public boolean isHTMLFile(Object fileObject) {
		String ext = getExtension(fileObject);
		return ext != null && ExtensionUtils.HTML_EXTENSIONS.contains(ext.toLowerCase());
	}

	@Override
	public boolean isJSFile(Object fileObject) {
		String ext = getExtension(fileObject);
		return ext != null && ExtensionUtils.JS_EXTENSION.equals(ext.toLowerCase());
	}
}
