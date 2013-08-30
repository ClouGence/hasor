/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

public class ReadManager implements Runnable {
	ICompilationUnit[] units;
	int nextFileToRead;
	ICompilationUnit[] filesRead;
	char[][] contentsRead;
	int readyToReadPosition;
	int nextAvailablePosition;
	Thread[] readingThreads;
	char[] readInProcessMarker = new char[0];
	int sleepingThreadCount;
	private Throwable caughtException;

	static final int START_CUSHION = 5;
	public static final int THRESHOLD = 10;
	static final int CACHE_SIZE = 15; // do not waste memory by keeping too many files in memory

public ReadManager(ICompilationUnit[] files, int length) {
	// start the background threads to read the file's contents
	int threadCount = 0;
	try {
		Class runtime = Class.forName("java.lang.Runtime"); //$NON-NLS-1$
		java.lang.reflect.Method m = runtime.getDeclaredMethod("availableProcessors", new Class[0]); //$NON-NLS-1$
		if (m != null) {
			Integer result = (Integer) m.invoke(Runtime.getRuntime(), null);
			threadCount = result.intValue() + 1;
			if (threadCount < 2)
				threadCount = 0;
			else if (threadCount > CACHE_SIZE)
				threadCount = CACHE_SIZE;
		}
	} catch (IllegalAccessException ignored) { // ignored
	} catch (ClassNotFoundException e) { // ignored
	} catch (SecurityException e) { // ignored
	} catch (NoSuchMethodException e) { // ignored
	} catch (IllegalArgumentException e) { // ignored
	} catch (InvocationTargetException e) { // ignored
	}

	if (threadCount > 0) {
		synchronized (this) {
			this.units = new ICompilationUnit[length];
			System.arraycopy(files, 0, this.units, 0, length);
			this.nextFileToRead = START_CUSHION; // skip some files to reduce the number of times we have to wait
			this.filesRead = new ICompilationUnit[CACHE_SIZE];
			this.contentsRead = new char[CACHE_SIZE][];
			this.readyToReadPosition = 0;
			this.nextAvailablePosition = 0;
			this.sleepingThreadCount = 0;
			this.readingThreads = new Thread[threadCount];
			for (int i = threadCount; --i >= 0;) {
				this.readingThreads[i] = new Thread(this, "Compiler Source File Reader"); //$NON-NLS-1$
				this.readingThreads[i].setDaemon(true);
				this.readingThreads[i].start();
			}
		}
	}
}

public char[] getContents(ICompilationUnit unit) throws Error {
	if (this.readingThreads == null || this.units.length == 0) {
		if (this.caughtException != null) {
			// rethrow the caught exception from the readingThreads in the main compiler thread
			if (this.caughtException instanceof Error)
				throw (Error) this.caughtException;
			throw (RuntimeException) this.caughtException;
		}
		return unit.getContents();
	}

	boolean yield = false;
	char[] result = null;
	synchronized (this) {
		if (unit == this.filesRead[this.readyToReadPosition]) {
			result = this.contentsRead[this.readyToReadPosition];
			while (result == this.readInProcessMarker || result == null) {
				// let the readingThread know we're waiting
				//System.out.print('|');
				this.contentsRead[this.readyToReadPosition] = null;
				try {
					wait(250);
				} catch (InterruptedException ignore) { // ignore
				}
				if (this.caughtException != null) {
					// rethrow the caught exception from the readingThreads in the main compiler thread
					if (this.caughtException instanceof Error)
						throw (Error) this.caughtException;
					throw (RuntimeException) this.caughtException;
				}
				result = this.contentsRead[this.readyToReadPosition];
			}
			// free spot for next file
			this.filesRead[this.readyToReadPosition] = null;
			this.contentsRead[this.readyToReadPosition] = null;
			if (++this.readyToReadPosition >= this.contentsRead.length)
				this.readyToReadPosition = 0;
			if (this.sleepingThreadCount > 0) {
				//System.out.print('+');
				//System.out.print(this.nextFileToRead);
				notify();
				yield = this.sleepingThreadCount == this.readingThreads.length;
			}
		} else {
			// must make sure we're reading ahead of the unit
			int unitIndex = 0;
			for (int l = this.units.length; unitIndex < l; unitIndex++)
				if (this.units[unitIndex] == unit) break;
			if (unitIndex == this.units.length) {
				// attempting to read a unit that was not included in the initial files - should not happen
				this.units = new ICompilationUnit[0]; // stop looking for more
			} else if (unitIndex >= this.nextFileToRead) {
				// start over
				//System.out.println(unitIndex + " vs " + this.nextFileToRead);
				this.nextFileToRead = unitIndex + START_CUSHION;
				this.readyToReadPosition = 0;
				this.nextAvailablePosition = 0;
				this.filesRead = new ICompilationUnit[CACHE_SIZE];
				this.contentsRead = new char[CACHE_SIZE][];
				notifyAll();
			}
		}
	}
	if (yield)
		Thread.yield(); // ensure other threads get a chance
	if (result != null)
		return result;
	//System.out.print('-');
	return unit.getContents();
}

public void run() {
	try {
		while (this.readingThreads != null && this.nextFileToRead < this.units.length) {
			ICompilationUnit unit = null;
			int position = -1;
			synchronized (this) {
				if (this.readingThreads == null) return;

				while (this.filesRead[this.nextAvailablePosition] != null) {
					this.sleepingThreadCount++;
					try {
						wait(250); // wait until a spot in contents is available
					} catch (InterruptedException e) { // ignore
					}
					this.sleepingThreadCount--;
					if (this.readingThreads == null) return;
				}

				if (this.nextFileToRead >= this.units.length) return;
				unit = this.units[this.nextFileToRead++];
				position = this.nextAvailablePosition;
				if (++this.nextAvailablePosition >= this.contentsRead.length)
					this.nextAvailablePosition = 0;
				this.filesRead[position] = unit;
				this.contentsRead[position] = this.readInProcessMarker; // mark the spot so we know its being read
			}
			char[] result = unit.getContents();
			synchronized (this) {
				if (this.filesRead[position] == unit) {
					if (this.contentsRead[position] == null) // wake up main thread which is waiting for this file
						notifyAll();
					this.contentsRead[position] = result;
				}
			}
		}
	} catch (Error e) {
		synchronized (this) {
			this.caughtException = e;
			shutdown();
		}
		return;
	} catch (RuntimeException e) {
		synchronized (this) {
			this.caughtException = e;
			shutdown();
		}
		return;
	}
}

public synchronized void shutdown() {
	this.readingThreads = null; // mark the read manager as shutting down so that the reading threads stop
	notifyAll();
}
}