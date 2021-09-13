package com.technisha.openportstom;

import java.lang.Exception;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.PortMappingEntry;

import net.minestom.server.extensions.Extension;
import net.minestom.server.MinecraftServer;

public class OpenPortStom extends Extension {
    public static Logger logger = LoggerFactory.getLogger(OpenPortStom.class);
    public static Integer port = MinecraftServer.getServer().getPort();
    public Boolean portOpened = false;
    public GatewayDevice d = null;

    @Override
    public void initialize() {
        logger.info("Starting weupnp");

        GatewayDiscover discover = new GatewayDiscover();
        logger.info("Looking for Gateway Devices");

        try {
            discover.discover();
        } catch (Exception e) {
            logger.error(e.toString());
        }

        d = discover.getValidGateway();

        if (null != d) {
            logger.info("Found gateway device.\n{0} ({1})", new Object[]{d.getModelName(), d.getModelDescription()});
        } else {
            logger.error("Your gateway device doesn't support write access to UPnP! Does the router have it enabled or support it?");
            return;
        }

        logger.info(String.format("Attempting to map port %d", port));
        PortMappingEntry portMapping = new PortMappingEntry();

        logger.info(String.format("Querying device to see if mapping for port %d already exists", port));
        try {
            if (d.getSpecificPortMappingEntry(port,"TCP",portMapping)) {
                logger.info("Port is already mapped.");
            } else {
                logger.info("Sending port mapping request");
                InetAddress localAddress = d.getLocalAddress();
                if (!d.addPortMapping(port, port, localAddress.getHostAddress(),"TCP","MinestomServer")) {
                    logger.info("Port cannot be opened.");
                } else {
                    logger.info("Port successfully opened.");
                    portOpened = true;
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void terminate() {
        if (portOpened) {
            try {
                d.deletePortMapping(port, "TCP");
                logger.info("Port successfully removed.");
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
    }
}
