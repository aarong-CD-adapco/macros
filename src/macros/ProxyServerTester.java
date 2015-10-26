/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.base.neo.NeoProperty;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class ProxyServerTester extends StarMacro {

    Simulation _sim;
    String _proxyhost = "randomname";

    @Override
    public void execute() {
        _sim = getActiveSimulation();

        try {
            createProxyServer();
            _sim.getSimulationIterator().run();
        } catch (Exception ex) {
            _sim.println("Caught exception second time.");
        } finally {
            _sim.println("finally");
        }
    }

    private void createProxyServer() {
        NeoProperty args = new NeoProperty();
        args.put("Host", _proxyhost);
        args.put("ServerCommand", System.getProperty("star.serverCmd"));
        args.put("Verbose", 0);
        try {
            NeoProperty response = getSimulation().execute("SpawnProxyServer", args);
            int port = response.getInt("Port");
            _sim.println("Optimate spawned proxy server started at ivoryhead.seattle.cd-adapco.com:" + port);
        } catch (Exception ex) {
            _sim.println("Unable to spawn proxy server on " + _proxyhost);
        }
    }

}
