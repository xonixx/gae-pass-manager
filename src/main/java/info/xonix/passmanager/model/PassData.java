package info.xonix.passmanager.model;

import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
public class PassData {
  public final String data;
  public final Date lastUpdated;
}
