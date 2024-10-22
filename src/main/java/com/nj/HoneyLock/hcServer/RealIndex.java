package com.nj.HoneyLock.hcServer;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "RealIndex")
public class RealIndex {
  @Id
  private String id;
  private int index;

  public RealIndex(String id, int index) {
    this.id = id;
    this.index = index;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }
}
