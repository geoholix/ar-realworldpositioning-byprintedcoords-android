package com.esri.apl.ar_world_positioning.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableList;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.esri.apl.ar_world_positioning.R;
import com.esri.apl.ar_world_positioning.model.FoundCoordArrayList;
import com.esri.apl.ar_world_positioning.model.FoundCoordinate;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class MainViewModel extends AndroidViewModel {
  private static final String TAG = "MainViewModel";
  private GraphicsOverlay _graphics = new GraphicsOverlay();

  private ObservableList<FoundCoordinate> _aryFoundCoords = new FoundCoordArrayList();

//  private Point _currentUserLocation;

  public MainViewModel(@NonNull Application application) {
    super(application);

    SimpleMarkerSymbol sms = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,
            Color.parseColor(getApplication().getString(R.string.color_found_location)), 12);
    Renderer rend = new SimpleRenderer(sms);
    _graphics.setRenderer(rend);
  }

  public ObservableList<FoundCoordinate> getAryFoundCoords() {
    return _aryFoundCoords;
  }

/*  public Point getCurrentUserLocation() {
    return _currentUserLocation;
  }

  public void setCurrentUserLocation(Point _currentUserLocation) {
    this._currentUserLocation = _currentUserLocation;
  }*/
}
