/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly;

/**
 *
 * @author Christian Prinz
 */
public class GlobalSettings {

    private static boolean runInConsole;
    private static boolean runAsTest;

    /**
     * @return the runInConsole
     */
    public static boolean isRunInConsole() {
        return runInConsole;
    }

    /**
     * @param aRunInConsole the runInConsole to set
     */
    public static void setRunInConsole(boolean aRunInConsole) {
        runInConsole = aRunInConsole;
        runAsTest = false;
    }

    /**
     * @return the runAsTest
     */
    public static boolean isRunAsTest() {
        return runAsTest;
    }

    /**
     * @param aRunAsTest the runAsTest to set
     */
    public static void setRunAsTest(boolean aRunAsTest) {
        runAsTest = aRunAsTest;
    }

}
