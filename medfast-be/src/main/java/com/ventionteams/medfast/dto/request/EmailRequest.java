package com.ventionteams.medfast.dto.request;

/**
 * Any request which has email and is handled by an endpoint
 * which can throw framework exceptions is an implementation of this interface.
 */
public interface EmailRequest {
  String getEmail();
}
