/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.standalone;

import java.io.*;
import java.util.List;

/**
 * Eclipse launcher.  Spawns eclipse executable
 * or org.eclipse.core.launcher.Main.
 */
public class Eclipse extends Thread {
	// Eclipse exit codes
	private static final int NEEDS_RESTART = 23;
	// Launching status
	public static final int STATUS_INIT = 0;
	public static final int STATUS_STARTED = 1;
	public static final int STATUS_ERROR = 2;

	File dir;
	String[] cmdarray;
	private int status;
	private Exception exception = new Exception("Unknown exception.");
	Process pr;
	/**
	 * Constructor
	 */
	public Eclipse() {
		super();
		this.setName("Eclipse");
		this.dir = Options.getEclipseHome();
	}
	private void prepareCommand() throws Exception {
		if (Options.useExe()) {
			prepareEclipseCommand();
			ensureEclipseExeExists();
		} else {
			prepareJavaCommand();
			ensureStartupJarExists();
		}
		ensureVmExists();
	}
	private void prepareEclipseCommand() {
		List vmArgs = Options.getVmArgs();
		List eclipseArgs = Options.getEclipseArgs();
		cmdarray = new String[3 + vmArgs.size() + 1 + eclipseArgs.size()];
		cmdarray[0] =
			new File(Options.getEclipseHome(), "eclipse").getAbsolutePath();
		cmdarray[1] = "-vm";
		cmdarray[2] = Options.getVm();
		for (int i = 0; i < eclipseArgs.size(); i++) {
			cmdarray[3 + i] = (String) eclipseArgs.get(i);
		}
		cmdarray[3 + eclipseArgs.size()] = "-vmargs";
		for (int i = 0; i < vmArgs.size(); i++) {
			cmdarray[4 + eclipseArgs.size() + i] = (String) vmArgs.get(i);
		}
	}
	private void prepareJavaCommand() {
		List vmArgs = Options.getVmArgs();
		List eclipseArgs = Options.getEclipseArgs();
		cmdarray = new String[1 + vmArgs.size() + 3 + eclipseArgs.size()];
		cmdarray[0] = Options.getVm();
		for (int i = 0; i < vmArgs.size(); i++) {
			cmdarray[1 + i] = (String) vmArgs.get(i);
		}
		cmdarray[1 + vmArgs.size()] = "-cp";
		cmdarray[2 + vmArgs.size()] = "startup.jar";
		cmdarray[3 + vmArgs.size()] = "org.eclipse.core.launcher.Main";
		for (int i = 0; i < eclipseArgs.size(); i++) {
			cmdarray[4 + vmArgs.size() + i] = (String) eclipseArgs.get(i);
		}
	}
	/**
	 * Launches Eclipse process and waits for it.
	 */
	public void run() {
		try {
			prepareCommand();
			if (Options.isDebug()) {
				printCommand();
			}
			launchProcess();
		} catch (Exception exc) {
			exception = exc;
			status = STATUS_ERROR;
		} finally {
			if (status == STATUS_INIT) {
				status = STATUS_ERROR;
			}
		}
	}
	private void launchProcess() throws IOException {
		try {
			do {
				pr = Runtime.getRuntime().exec(cmdarray, (String[]) null, dir);
				(new StreamConsumer(pr.getInputStream())).start();
				(new StreamConsumer(pr.getErrorStream())).start();
				status = STATUS_STARTED;
				pr.waitFor();
				if (Options.isDebug()) {
					System.out.println(
						"Eclipse exited with status code " + pr.exitValue());
					if (pr.exitValue() == NEEDS_RESTART) {
						System.out.println(
							"Updates are installed,  Eclipse will be restarted.");
					}
				}
			} while (pr.exitValue() == NEEDS_RESTART);
		} catch (InterruptedException e) {
		}
	}
	/**
	 * Reads a stream
	 */
	public class StreamConsumer extends Thread {
		BufferedReader bReader;
		public StreamConsumer(InputStream inputStream) {
			super();
			this.setName("Eclipse out/err consumer");
			this.setDaemon(true);
			bReader = new BufferedReader(new InputStreamReader(inputStream));
		}
		public void run() {
			try {
				String line;
				while (null != (line = bReader.readLine())) {
					System.out.println(line);
				}
				bReader.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	private void ensureVmExists() throws Exception {
		File vmExe = new File(Options.getVm());
		if (vmExe.exists() && !vmExe.isDirectory()) {
			return;
		}
		vmExe = new File(Options.getVm() + ".exe");
		if (vmExe.exists() && !vmExe.isDirectory()) {
			return;
		}
		throw new Exception(
			"File "
				+ vmExe.getAbsolutePath()
				+ " does not exists.  Pass a correct -vm option");
	}
	private void ensureEclipseExeExists() throws Exception {
		File eclipseExe =
			new File(
				Options.getEclipseHome(),
				"eclipse"
					+ (System.getProperty("os.name").startsWith("Win")
						? ".exe"
						: ""));
		if (eclipseExe.exists() && !eclipseExe.isDirectory()) {
			return;
		}
		throw new Exception(
			"File "
				+ eclipseExe.getAbsolutePath()
				+ " does not exists.  Pass a correct -eclipsehome option");
	}
	private void ensureStartupJarExists() throws Exception {
		File startupJar = new File(Options.getEclipseHome(), "startup.jar");
		if (startupJar.exists() && !startupJar.isDirectory()) {
			return;
		}
		throw new Exception(
			"File "
				+ startupJar.getAbsolutePath()
				+ " does not exists.  Pass a correct -eclipsehome option");
	}
	/**
	 * @return Exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @return int
	 */
	public int getStatus() {
		return status;
	}
	private void printCommand() {
		System.out.println("Launch command is:");
		for (int i = 0; i < cmdarray.length; i++) {
			System.out.println("  " + (String) cmdarray[i]);
		}

	}
	/**
	 * Used in unit testing.
	 */
	public void killProcess() {
		if (pr != null) {
			pr.destroy();
		}
	}
}