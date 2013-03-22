/*******************************************************************************
 * Copyright (c) 2011 Google, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *******************************************************************************/
package org.dev.toos.ui.internal.ui.eclipse.wb.swt;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.osgi.framework.Bundle;

/**
 * Utility class for managing OS resources associated with SWT/JFace controls such as colors, fonts, images,
 * etc.
 * 
 * !!! IMPORTANT !!! Application code must explicitly invoke the <code>dispose()</code> method to release the
 * operating system resources managed by cached objects when those objects and OS resources are no longer
 * needed (e.g. on application shutdown)
 * 
 * This class may be freely distributed as part of any application or plugin.
 * <p>
 * 
 * @author scheglov_ke
 * @author Dan Rubel
 */
public class ResourceManager extends SWTResourceManager {
	////////////////////////////////////////////////////////////////////////////
	//
	// Image
	//
	////////////////////////////////////////////////////////////////////////////
	private static Map<ImageDescriptor, Image> m_descriptorImageMap = new HashMap<ImageDescriptor, Image>();
	/**
	 * Returns an {@link ImageDescriptor} stored in the file at the specified path relative to the specified
	 * class.
	 * 
	 * @param clazz
	 *            the {@link Class} relative to which to find the image descriptor.
	 * @param path
	 *            the path to the image file.
	 * @return the {@link ImageDescriptor} stored in the file at the specified path.
	 */
	public static ImageDescriptor getImageDescriptor(Class<?> clazz, String path) {
		return ImageDescriptor.createFromFile(clazz, path);
	}
	/**
	 * Returns an {@link ImageDescriptor} stored in the file at the specified path.
	 * 
	 * @param path
	 *            the path to the image file.
	 * @return the {@link ImageDescriptor} stored in the file at the specified path.
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		try {
			return ImageDescriptor.createFromURL(new File(path).toURI().toURL());
		} catch (MalformedURLException e) {
			return null;
		}
	}
	/**
	 * Returns an {@link Image} based on the specified {@link ImageDescriptor}.
	 * 
	 * @param descriptor
	 *            the {@link ImageDescriptor} for the {@link Image}.
	 * @return the {@link Image} based on the specified {@link ImageDescriptor}.
	 */
	public static Image getImage(ImageDescriptor descriptor) {
		if (descriptor == null) {
			return null;
		}
		Image image = m_descriptorImageMap.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			m_descriptorImageMap.put(descriptor, image);
		}
		return image;
	}
	/**
	 * Maps images to decorated images.
	 */
	@SuppressWarnings("unchecked")
	private static Map<Image, Map<Image, Image>>[] m_decoratedImageMap = new Map[LAST_CORNER_KEY];
	/**
	 * Returns an {@link Image} composed of a base image decorated by another image.
	 * 
	 * @param baseImage
	 *            the base {@link Image} that should be decorated.
	 * @param decorator
	 *            the {@link Image} to decorate the base image.
	 * @return {@link Image} The resulting decorated image.
	 */
	public static Image decorateImage(Image baseImage, Image decorator) {
		return decorateImage(baseImage, decorator, BOTTOM_RIGHT);
	}
	/**
	 * Returns an {@link Image} composed of a base image decorated by another image.
	 * 
	 * @param baseImage
	 *            the base {@link Image} that should be decorated.
	 * @param decorator
	 *            the {@link Image} to decorate the base image.
	 * @param corner
	 *            the corner to place decorator image.
	 * @return the resulting decorated {@link Image}.
	 */
	public static Image decorateImage(final Image baseImage, final Image decorator, final int corner) {
		if (corner <= 0 || corner >= LAST_CORNER_KEY) {
			throw new IllegalArgumentException("Wrong decorate corner");
		}
		Map<Image, Map<Image, Image>> cornerDecoratedImageMap = m_decoratedImageMap[corner];
		if (cornerDecoratedImageMap == null) {
			cornerDecoratedImageMap = new HashMap<Image, Map<Image, Image>>();
			m_decoratedImageMap[corner] = cornerDecoratedImageMap;
		}
		Map<Image, Image> decoratedMap = cornerDecoratedImageMap.get(baseImage);
		if (decoratedMap == null) {
			decoratedMap = new HashMap<Image, Image>();
			cornerDecoratedImageMap.put(baseImage, decoratedMap);
		}
		//
		Image result = decoratedMap.get(decorator);
		if (result == null) {
			final Rectangle bib = baseImage.getBounds();
			final Rectangle dib = decorator.getBounds();
			final Point baseImageSize = new Point(bib.width, bib.height);
			CompositeImageDescriptor compositImageDesc = new CompositeImageDescriptor() {
				@Override
				protected void drawCompositeImage(int width, int height) {
					drawImage(baseImage.getImageData(), 0, 0);
					if (corner == TOP_LEFT) {
						drawImage(decorator.getImageData(), 0, 0);
					} else if (corner == TOP_RIGHT) {
						drawImage(decorator.getImageData(), bib.width - dib.width, 0);
					} else if (corner == BOTTOM_LEFT) {
						drawImage(decorator.getImageData(), 0, bib.height - dib.height);
					} else if (corner == BOTTOM_RIGHT) {
						drawImage(decorator.getImageData(), bib.width - dib.width, bib.height - dib.height);
					}
				}
				@Override
				protected Point getSize() {
					return baseImageSize;
				}
			};
			//
			result = compositImageDesc.createImage();
			decoratedMap.put(decorator, result);
		}
		return result;
	}
	/**
	 * Dispose all of the cached images.
	 */
	public static void disposeImages() {
		SWTResourceManager.disposeImages();
		// dispose ImageDescriptor images
		{
			for (Iterator<Image> I = m_descriptorImageMap.values().iterator(); I.hasNext();) {
				I.next().dispose();
			}
			m_descriptorImageMap.clear();
		}
		// dispose decorated images
		for (int i = 0; i < m_decoratedImageMap.length; i++) {
			Map<Image, Map<Image, Image>> cornerDecoratedImageMap = m_decoratedImageMap[i];
			if (cornerDecoratedImageMap != null) {
				for (Map<Image, Image> decoratedMap : cornerDecoratedImageMap.values()) {
					for (Image image : decoratedMap.values()) {
						image.dispose();
					}
					decoratedMap.clear();
				}
				cornerDecoratedImageMap.clear();
			}
		}
		// dispose plugin images
		{
			for (Iterator<Image> I = m_URLImageMap.values().iterator(); I.hasNext();) {
				I.next().dispose();
			}
			m_URLImageMap.clear();
		}
	}
	////////////////////////////////////////////////////////////////////////////
	//
	// Plugin images support
	//
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Maps URL to images.
	 */
	private static Map<String, Image> m_URLImageMap = new HashMap<String, Image>();
	/**
	 * Provider for plugin resources, used by WindowBuilder at design time.
	 */
	public interface PluginResourceProvider {
		URL getEntry(String symbolicName, String path);
	}
	/**
	 * Instance of {@link PluginResourceProvider}, used by WindowBuilder at design time.
	 */
	private static PluginResourceProvider m_designTimePluginResourceProvider = null;
	/**
	 * Returns an {@link Image} based on a plugin and file path.
	 * 
	 * @param plugin
	 *            the plugin {@link Object} containing the image
	 * @param name
	 *            the path to the image within the plugin
	 * @return the {@link Image} stored in the file at the specified path
	 * 
	 * @deprecated Use {@link #getPluginImage(String, String)} instead.
	 */
	@Deprecated
	public static Image getPluginImage(Object plugin, String name) {
		try {
			URL url = getPluginImageURL(plugin, name);
			if (url != null) {
				return getPluginImageFromUrl(url);
			}
		} catch (Throwable e) {
			// Ignore any exceptions
		}
		return null;
	}
	/**
	 * Returns an {@link Image} based on a {@link Bundle} and resource entry path.
	 * 
	 * @param symbolicName
	 *            the symbolic name of the {@link Bundle}.
	 * @param path
	 *            the path of the resource entry.
	 * @return the {@link Image} stored in the file at the specified path.
	 */
	public static Image getPluginImage(String symbolicName, String path) {
		try {
			URL url = getPluginImageURL(symbolicName, path);
			if (url != null) {
				return getPluginImageFromUrl(url);
			}
		} catch (Throwable e) {
			// Ignore any exceptions
		}
		return null;
	}
	/**
	 * Returns an {@link Image} based on given {@link URL}.
	 */
	private static Image getPluginImageFromUrl(URL url) {
		try {
			try {
				String key = url.toExternalForm();
				Image image = m_URLImageMap.get(key);
				if (image == null) {
					InputStream stream = url.openStream();
					try {
						image = getImage(stream);
						m_URLImageMap.put(key, image);
					} finally {
						stream.close();
					}
				}
				return image;
			} catch (Throwable e) {
				// Ignore any exceptions
			}
		} catch (Throwable e) {
			// Ignore any exceptions
		}
		return null;
	}
	/**
	 * Returns an {@link ImageDescriptor} based on a plugin and file path.
	 * 
	 * @param plugin
	 *            the plugin {@link Object} containing the image.
	 * @param name
	 *            the path to th eimage within the plugin.
	 * @return the {@link ImageDescriptor} stored in the file at the specified path.
	 * 
	 * @deprecated Use {@link #getPluginImageDescriptor(String, String)} instead.
	 */
	@Deprecated
	public static ImageDescriptor getPluginImageDescriptor(Object plugin, String name) {
		try {
			try {
				URL url = getPluginImageURL(plugin, name);
				return ImageDescriptor.createFromURL(url);
			} catch (Throwable e) {
				// Ignore any exceptions
			}
		} catch (Throwable e) {
			// Ignore any exceptions
		}
		return null;
	}
	/**
	 * Returns an {@link ImageDescriptor} based on a {@link Bundle} and resource entry path.
	 * 
	 * @param symbolicName
	 *            the symbolic name of the {@link Bundle}.
	 * @param path
	 *            the path of the resource entry.
	 * @return the {@link ImageDescriptor} based on a {@link Bundle} and resource entry path.
	 */
	public static ImageDescriptor getPluginImageDescriptor(String symbolicName, String path) {
		try {
			URL url = getPluginImageURL(symbolicName, path);
			if (url != null) {
				return ImageDescriptor.createFromURL(url);
			}
		} catch (Throwable e) {
			// Ignore any exceptions
		}
		return null;
	}
	/**
	 * Returns an {@link URL} based on a {@link Bundle} and resource entry path.
	 */
	private static URL getPluginImageURL(String symbolicName, String path) {
		// try runtime plugins
		{
			Bundle bundle = Platform.getBundle(symbolicName);
			if (bundle != null) {
				return bundle.getEntry(path);
			}
		}
		// try design time provider
		if (m_designTimePluginResourceProvider != null) {
			return m_designTimePluginResourceProvider.getEntry(symbolicName, path);
		}
		// no such resource
		return null;
	}
	/**
	 * Returns an {@link URL} based on a plugin and file path.
	 * 
	 * @param plugin
	 *            the plugin {@link Object} containing the file path.
	 * @param name
	 *            the file path.
	 * @return the {@link URL} representing the file at the specified path.
	 * @throws Exception
	 */
	private static URL getPluginImageURL(Object plugin, String name) throws Exception {
		// try to work with 'plugin' as with OSGI BundleContext
		try {
			Class<?> BundleClass = Class.forName("org.osgi.framework.Bundle"); //$NON-NLS-1$
			Class<?> BundleContextClass = Class.forName("org.osgi.framework.BundleContext"); //$NON-NLS-1$
			if (BundleContextClass.isAssignableFrom(plugin.getClass())) {
				Method getBundleMethod = BundleContextClass.getMethod("getBundle", new Class[0]); //$NON-NLS-1$
				Object bundle = getBundleMethod.invoke(plugin, new Object[0]);
				//
				Class<?> PathClass = Class.forName("org.eclipse.core.runtime.Path"); //$NON-NLS-1$
				Constructor<?> pathConstructor = PathClass.getConstructor(new Class[]{String.class});
				Object path = pathConstructor.newInstance(new Object[]{name});
				//
				Class<?> IPathClass = Class.forName("org.eclipse.core.runtime.IPath"); //$NON-NLS-1$
				Class<?> PlatformClass = Class.forName("org.eclipse.core.runtime.Platform"); //$NON-NLS-1$
				Method findMethod = PlatformClass.getMethod("find", new Class[]{BundleClass, IPathClass}); //$NON-NLS-1$
				return (URL) findMethod.invoke(null, new Object[]{bundle, path});
			}
		} catch (Throwable e) {
			// Ignore any exceptions
		}
		// else work with 'plugin' as with usual Eclipse plugin
		{
			Class<?> PluginClass = Class.forName("org.eclipse.core.runtime.Plugin"); //$NON-NLS-1$
			if (PluginClass.isAssignableFrom(plugin.getClass())) {
				//
				Class<?> PathClass = Class.forName("org.eclipse.core.runtime.Path"); //$NON-NLS-1$
				Constructor<?> pathConstructor = PathClass.getConstructor(new Class[]{String.class});
				Object path = pathConstructor.newInstance(new Object[]{name});
				//
				Class<?> IPathClass = Class.forName("org.eclipse.core.runtime.IPath"); //$NON-NLS-1$
				Method findMethod = PluginClass.getMethod("find", new Class[]{IPathClass}); //$NON-NLS-1$
				return (URL) findMethod.invoke(plugin, new Object[]{path});
			}
		}
		return null;
	}
	////////////////////////////////////////////////////////////////////////////
	//
	// General
	//
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Dispose of cached objects and their underlying OS resources. This should only be called when the cached
	 * objects are no longer needed (e.g. on application shutdown).
	 */
	public static void dispose() {
		disposeColors();
		disposeFonts();
		disposeImages();
	}
}