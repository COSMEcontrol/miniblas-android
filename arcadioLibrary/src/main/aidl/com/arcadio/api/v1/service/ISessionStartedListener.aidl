package com.arcadio.api.v1.service;

interface ISessionStartedListener {
    void onSessionStarted(int sessionId, String sessionKey);
    void onSessionError(String error);
}