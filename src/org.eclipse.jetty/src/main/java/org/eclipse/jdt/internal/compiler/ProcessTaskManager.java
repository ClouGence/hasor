/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.util.Messages;

public class ProcessTaskManager implements Runnable {

	Compiler compiler;
	private int unitIndex;
	private Thread processingThread;
	CompilationUnitDeclaration unitToProcess;
	private Throwable caughtException;

	// queue
	volatile int currentIndex, availableIndex, size, sleepCount;
	CompilationUnitDeclaration[] units;

	public static final int PROCESSED_QUEUE_SIZE = 12;

public ProcessTaskManager(Compiler compiler) {
	this.compiler = compiler;
	this.unitIndex = 0;

	this.currentIndex = 0;
	this.availableIndex = 0;
	this.size = PROCESSED_QUEUE_SIZE;
	this.sleepCount = 0; // 0 is no one, +1 is the processing thread & -1 is the writing/main thread
	this.units = new CompilationUnitDeclaration[this.size];

	synchronized (this) {
		this.processingThread = new Thread(this, "Compiler Processing Task"); //$NON-NLS-1$
		this.processingThread.setDaemon(true);
		this.processingThread.start();
	}
}

// add unit to the queue - wait if no space is available
private synchronized void addNextUnit(CompilationUnitDeclaration newElement) {
	while (this.units[this.availableIndex] != null) {
		//System.out.print('a');
		//if (this.sleepCount < 0) throw new IllegalStateException(new Integer(this.sleepCount).toString());
		this.sleepCount = 1;
		try {
			wait(250);
		} catch (InterruptedException ignore) {
			// ignore
		}
		this.sleepCount = 0;
	}

	this.units[this.availableIndex++] = newElement;
	if (this.availableIndex >= this.size)
		this.availableIndex = 0;
	if (this.sleepCount <= -1)
		notify(); // wake up writing thread to accept next unit - could be the last one - must avoid deadlock
}

public CompilationUnitDeclaration removeNextUnit() throws Error {
	CompilationUnitDeclaration next = null;
	boolean yield = false;
	synchronized (this) {
		next = this.units[this.currentIndex];
		if (next == null || this.caughtException != null) {
			do {
				if (this.processingThread == null) {
					if (this.caughtException != null) {
						// rethrow the caught exception from the processingThread in the main compiler thread
						if (this.caughtException instanceof Error)
							throw (Error) this.caughtException;
						throw (RuntimeException) this.caughtException;
					}
					return null;
				}
				//System.out.print('r');
				//if (this.sleepCount > 0) throw new IllegalStateException(new Integer(this.sleepCount).toString());
				this.sleepCount = -1;
				try {
					wait(100);
				} catch (InterruptedException ignore) {
					// ignore
				}
				this.sleepCount = 0;
				next = this.units[this.currentIndex];
			} while (next == null);
		}

		this.units[this.currentIndex++] = null;
		if (this.currentIndex >= this.size)
			this.currentIndex = 0;
		if (this.sleepCount >= 1 && ++this.sleepCount > 4) {
			notify(); // wake up processing thread to add next unit but only after removing some elements first
			yield = this.sleepCount > 8;
		}
	}
	if (yield)
		Thread.yield();
	return next;
}

public void run() {
	while (this.processingThread != null) {
		this.unitToProcess = null;
		int index = -1;
		try {
			synchronized (this) {
				if (this.processingThread == null) return;

				this.unitToProcess = this.compiler.getUnitToProcess(this.unitIndex);
				if (this.unitToProcess == null) {
					this.processingThread = null;
					return;
				}
				index = this.unitIndex++;
			}

			try {
				this.compiler.reportProgress(Messages.bind(Messages.compilation_processing, new String(this.unitToProcess.getFileName())));
				if (this.compiler.options.verbose)
					this.compiler.out.println(
						Messages.bind(Messages.compilation_process,
						new String[] {
							String.valueOf(index + 1),
							String.valueOf(this.compiler.totalUnits),
							new String(this.unitToProcess.getFileName())
						}));
				this.compiler.process(this.unitToProcess, index);
			} finally {
				if (this.unitToProcess != null)
					this.unitToProcess.cleanUp();
			}

			addNextUnit(this.unitToProcess);
		} catch (Error e) {
			synchronized (this) {
				this.processingThread = null;
				this.caughtException = e;
			}
			return;
		} catch (RuntimeException e) {
			synchronized (this) {
				this.processingThread = null;
				this.caughtException = e;
			}
			return;
		}
	}
}

public void shutdown() {
	try {
		Thread t = null;
		synchronized (this) {
			if (this.processingThread != null) {
				t = this.processingThread;
				this.processingThread = null;
				notifyAll();
			}
		}
		if (t != null)
			t.join(250); // do not wait forever
	} catch (InterruptedException ignored) {
		// ignore
	}
}
}
