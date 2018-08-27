package com.esri.apl.ar_world_positioning.view;

import com.esri.apl.ar_world_positioning.model.FoundCoordinate;

public interface GeoCoordinateSelectionListener {
  void onGeoCoordinateSelected(FoundCoordinate foundCoordinate);
}
