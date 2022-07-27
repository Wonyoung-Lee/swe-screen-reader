package edu.brown.cs.student.maps;

public class Way {
  private final String id;
  private final Double startLat;
  private final Double startLon;
  private final Double endLat;
  private final Double endLon;
  private final String name;
  private final String type;

  public Way(String id, Double startLat, Double endLat, Double startLon, Double endLon,
             String name, String type) {
    this.id = id;
    this.startLat = startLat;
    this.startLon = startLon;
    this.endLat = endLat;
    this.endLon = endLon;
    this.name = name;
    this.type = type;
  }

  @Override
  public String toString() {
    return "Way{" +
        "id='" + id + '\'' +
        ", startLat=" + startLat +
        ", startLon=" + startLon +
        ", endLat=" + endLat +
        ", endLon=" + endLon +
        ", name='" + name + '\'' +
        ", type='" + type + '\'' +
        '}';
  }
}
