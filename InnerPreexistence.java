/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corp. and others
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which accompanies this
 * distribution and is available at https://www.eclipse.org/legal/epl-2.0/
 * or the Apache License, Version 2.0 which accompanies this distribution and
 * is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * This Source Code may also be made available under the following
 * Secondary Licenses when the conditions for such availability set
 * forth in the Eclipse Public License, v. 2.0 are satisfied: GNU
 * General Public License, version 2 with the GNU Classpath
 * Exception [1] and GNU General Public License, version 2 with the
 * OpenJDK Assembly Exception [2].
 *
 * [1] https://www.gnu.org/software/classpath/license.html
 * [2] http://openjdk.java.net/legal/assembly-exception.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
 *******************************************************************************/

import java.util.Timer;


public class InnerPreexistence implements Notifiable {
	
	public static final int TASK_LOAD_C = 1;

	private Timer timer = new Timer();
	private /* volatile */ boolean keepOnGoing = true;
	private A someA = new A();
	private B someB = new B();


	protected void tearDown() throws Exception {
		timer.cancel();
		timer = null;
	}
	
	public void doTest() throws Exception {
	    try {
	        testInnerPreexistence();
	    } finally {
	        tearDown();
	    }
	}

	public void testInnerPreexistence() {
		timer.schedule(new NotifyTask(TASK_LOAD_C, this), 8000);
		while (keepOnGoing)
			foo();
		if (foo() != 'c') {
		    throw new RuntimeException("Test Failed");
		}
		System.out.println("Test passed!");
	}

	public void wakeUp(int eventId) {
	    System.out.println("DEBUG: Waking up: " + eventId);
		someB = new C();
                dummySync(); // to ensure correct ordering on weak memory systems
		keepOnGoing = false;
	}

    private synchronized void dummySync() {}
	
	class A { char bar(B b) { return b.goo(); } }
	class B { char goo() { return 'b'; } }
	class C extends B { char goo() { return 'c'; } }
	
	public long x = 0;
	char foo() {
		// spend some time here to get to scorching
		for (int i = 0; i < 100; ++i)
			x++;
		
		if (x == 0)
			someB = null; // never reached
		
		return someA.bar(someB);
	}
	
    public static void main(String[] args) throws Exception {
        InnerPreexistence inner = new InnerPreexistence();
        inner.doTest();
    }
}
