package de.spaceai.disvoice.logging.impl;

import de.spaceai.disvoice.logging.ILogger;
import de.spaceai.disvoice.logging.LogPriority;

import java.util.Date;

public class Logger implements ILogger {
    @Override
    public void log(LogPriority logPriority, String message) {
        System.out.println("["+ new Date().toLocaleString() +"] " + logPriority.getMessage() + message);
    }
}
