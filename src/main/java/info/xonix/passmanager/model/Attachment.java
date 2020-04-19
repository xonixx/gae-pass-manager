package info.xonix.passmanager.model;

import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
public class Attachment {
  public final String data;
  public final Date uploaded;
}
