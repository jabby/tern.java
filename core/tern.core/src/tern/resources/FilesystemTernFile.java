/*
 * Copyright 2014, Genuitec, LLC
 * All Rights Reserved.
 */
package tern.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import tern.ITernFile;
import tern.ITernProject;
import tern.TernResourcesManager;
import tern.utils.ExtensionUtils;
import tern.utils.IOUtils;

/**
 * Basic Tern file implementation, which provides wrapper for java.io.File
 */
public class FilesystemTernFile extends AbstractTernFile implements ITernFile {

	private File file;
	
	public FilesystemTernFile(File file) {
		assert file.isFile();
		this.file = file;
		//get the canonical path of the file
		try {
			this.file = file.getCanonicalFile();
		} catch (IOException ex) {
			//best effort
		}
	}

	@Override
	public String getFullName(ITernProject project) {
		if (project != null) {
			//check if the file belongs to the project
			try {
				String path = project.getProjectDir().getCanonicalPath().replace('\\', '/');
				if (!path.endsWith("/")) { //$NON-NLS-1$
					path += "/"; //$NON-NLS-1$
				}
				String fileName = file.toString().replace('\\', '/');
				if (fileName.startsWith(path)) {
					return fileName.substring(path.length());
				}
			} catch (Exception ex) {
				//ignore
			}
		}
		//if it doesn't, use the external protocol
		return EXTERNAL_PROTOCOL + file.toString();
	}

	@Override
	public String getFileName() {
		String full = getFullName(null);
		int last = full.lastIndexOf('/');
		if (last >=0 ) {
			return full.substring(last + 1);
		}
		return full;
	}

	@Override
	public String getContents() throws IOException {
		FileInputStream input = new FileInputStream(file);
		try {
			return IOUtils.toString(input);
		} finally {
			input.close();
		}
	}

	@Override
	public ITernFile getRelativeFile(String relativePath) {
		File f = new File(file, relativePath);
		if (f.isFile()) {
			//use cache if possible
			TernResourcesManager.getTernFile(f);
		}
		return null;
	}
	
	@Override
	public String getFileExtension() {
		return ExtensionUtils.getFileExtension(getFileName());
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass) {
		if (adapterClass == File.class) {
			return file;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return file.toString();
	}

}
