package de.spaceai.disvoice.logging;

public enum LogPriority {

    VOICE("DisVoice - VOICE"),
    MINECRAFT("DisVoice - MINECRAFT"),
    DEBUG("DisVoice - DEBUG"),
    INFO("DisVoice - INFO"),
    WARN("DisVoice - WARN");

    private final String message;

    LogPriority(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message+" | ";
    }
}
