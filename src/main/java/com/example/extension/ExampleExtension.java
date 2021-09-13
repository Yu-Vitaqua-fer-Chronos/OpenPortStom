package com.example.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minestom.server.extensions.Extension;

public class ExampleExtension extends Extension {
    public static Logger logger = LoggerFactory.getLogger(ExampleExtension.class);

    @Override
    public void initialize() {
        logger.info("Hello World!");
    }

    @Override
    public void terminate() {
        logger.info("Bye World!");
    }
}
