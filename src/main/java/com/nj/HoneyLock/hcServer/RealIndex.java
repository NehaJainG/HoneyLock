package com.nj.HoneyLock.hcServer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "RealIndex")
public class RealIndex {
  @Id
  private String id;
  private int index;

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
