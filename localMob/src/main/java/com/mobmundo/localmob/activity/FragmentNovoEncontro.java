package com.mobmundo.localmob.activity;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mobmundo.localmob.R;
import com.mobmundo.localmob.DAO.LastLocationDAO;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FragmentNovoEncontro extends Fragment implements LocationListener {
	private GoogleMap map;
	MapView mapView;
	View view;
    private Marker marker;
    private Polyline polyline;
    private List<LatLng> list;
    private long distance;
    LatLng lastLatLng;
    EditText etD;
    LocationRequest mLocationRequest;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// try {

		view = inflater.inflate(R.layout.fragment_layout_novo_encontro,
				container, false);
		mapView = (MapView) view.findViewById(R.id.mapOnde);
		mapView.onCreate(savedInstanceState);
        ImageView imvPesquisarLocal = (ImageView) view.findViewById(R.id.imvPesquisarLocal);
        etD = (EditText) view.findViewById(R.id.etOnde);
        initLocationRequest();
        imvPesquisarLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getRouteByGMAV2();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

		if (mapView != null) {
			ParseUser user = ParseUser.getCurrentUser();
			final String idUser = user.getObjectId();
			ParseQuery<ParseObject> query = ParseQuery.getQuery("LastLocation");
			query.whereEqualTo("idUser", idUser);
			query.getFirstInBackground(new GetCallback<ParseObject>() {
				public void done(ParseObject object, ParseException e) {
					ParseGeoPoint point;
					LatLng frameworkSystemLocation;
					LastLocationDAO lDAO = new LastLocationDAO();
					if (object == null) {
						frameworkSystemLocation = new LatLng(-34.397, 150.644);
						saveLastLocation(frameworkSystemLocation);
                        lastLatLng = frameworkSystemLocation;
						// Toast.makeText(FragmentNovoEncontro.this.getActivity(),
						// "Entrou como nulo", Toast.LENGTH_SHORT).show();
					} else {
						point = (ParseGeoPoint) object.get("lastLocation");
						frameworkSystemLocation = new LatLng(point
								.getLatitude(), point.getLongitude());
						saveLastLocation(frameworkSystemLocation);
                        lastLatLng = frameworkSystemLocation;
					}
					LocationManager locationManager = (LocationManager) view.getContext().getSystemService(
                            Context.LOCATION_SERVICE);
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 0, 0,
							FragmentNovoEncontro.this);
					map = mapView.getMap();
					map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					//map.getUiSettings().setMyLocationButtonEnabled(true);
					Marker frameworkSystem = map.addMarker(new MarkerOptions()
							.position(frameworkSystemLocation).title(
									"Sua Posicão"));
					// Move a camera para Framework System com zoom 15.
					MapsInitializer.initialize(view.getContext());
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(
							frameworkSystemLocation, 17));
					map.animateCamera(CameraUpdateFactory.newLatLng(frameworkSystemLocation));
				}
			});
		}
		/*
		 * } catch (InflateException e) { map is already there, just return view
		 * as it is Toast.makeText(FragmentNovoEncontro.this.getActivity(),
		 * "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show(); }
		 */

        configMap();
		return view;
	}

    private void initLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

	@Override
	public void onLocationChanged(final Location location) {
		final String idUser = ParseUser.getCurrentUser().getObjectId();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("LastLocation");
		query.whereEqualTo("idUser", idUser);
		query.getFirstInBackground(new GetCallback<ParseObject>() {
			public void done(ParseObject object, ParseException e) {
				ParseGeoPoint point;
				LatLng frameworkSystemLocation;
				if (object == null) {
					frameworkSystemLocation = new LatLng(-34.397, 150.644);
					saveLastLocation(frameworkSystemLocation);
				} else {
					point = (ParseGeoPoint) object.get("lastLocation");
					frameworkSystemLocation = new LatLng(point.getLatitude(),
							point.getLongitude());
					if ((location.getLatitude() != frameworkSystemLocation.latitude)
							&& (location.getLongitude() != frameworkSystemLocation.longitude)) {
						LatLng latLng = new LatLng(location.getLatitude(),
								location.getLongitude());
						frameworkSystemLocation = latLng;
						//map.clear();
						Marker frameworkSystem = map
								.addMarker(new MarkerOptions().position(
										frameworkSystemLocation).title(
										"Marcador"));
						// Move a camera para Framework System com zoom 15.
                        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(frameworkSystemLocation, 15));
						//map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
						saveLastLocation(frameworkSystemLocation);
                        lastLatLng = frameworkSystemLocation;
						// Toast.makeText(FragmentNovoEncontro.this.getActivity(),
						// "Pegou a localizacao", Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public FragmentNovoEncontro() {

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Add your menu entries here
		super.onCreateOptionsMenu(menu, inflater);
		this.getActivity().getMenuInflater()
				.inflate(R.menu.novo_encontro, menu);
	}

	public void saveLastLocation(LatLng location) {
		ParseGeoPoint point = new ParseGeoPoint(location.latitude,
				location.longitude);
		String idUser = ParseUser.getCurrentUser().getObjectId();
		LastLocationDAO lDAO = new LastLocationDAO();
		lDAO.saveLocation(point, idUser);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		mapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();

		mapView.onLowMemory();
	}


    public void configMap(){
        map = mapView.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        list = new ArrayList<LatLng>();

        if(lastLatLng == null) {
            lastLatLng = new LatLng(-34.397, 150.644);
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(lastLatLng).zoom(8).bearing(0).tilt(90).build();
        MapsInitializer.initialize(getActivity());
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);

        //map.moveCamera(update);
        map.animateCamera(update, 3000, new GoogleMap.CancelableCallback(){
            @Override
            public void onCancel() {
                Log.i("Script", "CancelableCallback.onCancel()");
            }

            @Override
            public void onFinish() {
                Log.i("Script", "CancelableCallback.onFinish()");
            }
        });

        // MARKERS
        //customAddMarker(new LatLng(-23.564224, -46.653156), "Marcador 1", "O Marcador 1 foi reposicionado");
        //customAddMarker(new LatLng(-23.564205, -46.653102), "Marcador 2", "O Marcador 2 foi reposicionado");

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){

            @Override
            public View getInfoContents(Marker marker) {
                TextView tv = new TextView(view.getContext());
                tv.setText(Html.fromHtml("<b><font color=\"#ff0000\">" + marker.getTitle() + ":</font></b> " + marker.getSnippet()));

                return tv;
            }

            @Override
            public View getInfoWindow(Marker marker) {
                LinearLayout ll = new LinearLayout(view.getContext());
                ll.setPadding(20, 20, 20, 20);
                ll.setBackgroundColor(Color.GREEN);

                TextView tv = new TextView(view.getContext());
                tv.setText(Html.fromHtml("<b><font color=\"#ffffff\">"+marker.getTitle()+":</font></b> "+marker.getSnippet()));
                ll.addView(tv);

                Button bt = new Button(view.getContext());
                bt.setText("Botão");
                bt.setBackgroundColor(Color.RED);
                bt.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        Log.i("Script", "Botão clicado");
                    }

                });

                ll.addView(bt);

                return ll;
            }

        });


        // EVENTS
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
					/*Log.i("Script", "setOnCameraChangeListener()");

					if(marker != null){
						marker.remove();
					}
					customAddMarker(new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude), "1: Marcador Alterado", "O Marcador foi reposicionado");
					*/
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("Script", "setOnMapClickListener()");

                if(marker != null){
                    marker.remove();
                }
                customAddMarker(new LatLng(latLng.latitude, latLng.longitude), "2: Marcador Alterado", "O Marcador foi reposicionado");
                list.add(latLng);
                drawRoute();
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("Script", "3: Marker: "+marker.getTitle());
                return false;
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i("Script", "4: Marker: "+marker.getTitle());
            }
        });
    }


    public void customAddMarker(LatLng latLng, String title, String snippet){
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet).draggable(true);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));

        //marker = map.addMarker(options);
    }


    public void drawRoute(){
        PolylineOptions po;

        if(polyline == null){
            po = new PolylineOptions();

            for(int i = 0, tam = list.size(); i < tam; i++){
                po.add(list.get(i));
            }

            po.color(Color.BLACK).width(8);
            polyline = map.addPolyline(po);
        }
        else{
            polyline.setPoints(list);
        }
    }


    public void getDistance(View view){
		/*double distance = 0;

		for(int i = 0, tam = list.size(); i < tam; i++){
			if(i < tam - 1){
				distance += distance(list.get(i), list.get(i+1));
			}
		}*/

        Toast.makeText(view.getContext(), "Distancia: "+distance+" metros", Toast.LENGTH_LONG).show();
    }


    public static double distance(LatLng StartP, LatLng EndP) {
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }


    public void getLocation(View view){
        Geocoder gc = new Geocoder(view.getContext());

        List<Address> addressList;
        try {
            //addressList = gc.getFromLocation(list.get(list.size() - 1).latitude, list.get(list.size() - 1).longitude, 1);
            addressList = gc.getFromLocationName("Rua Vergueiro, São Paulo, São Paulo, Brasil", 1);

            String address = "Rua: "+addressList.get(0).getThoroughfare()+"\n";
            address += "Cidade: "+addressList.get(0).getSubAdminArea()+"\n";
            address += "Estado: "+addressList.get(0).getAdminArea()+"\n";
            address += "País: "+addressList.get(0).getCountryName();

            LatLng ll = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());

            //Toast.makeText(MainActivity.this, "Local: "+address, Toast.LENGTH_LONG).show();
            Toast.makeText(view.getContext(), "LatLng: "+ll, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }














	/* ***************************************** ROTA ***************************************** */

    public void getRouteByGMAV2() throws UnsupportedEncodingException {
        if(etD.getText().toString().trim() != "") {
            String origin = lastLatLng.latitude + "," + lastLatLng.longitude;
            String destination = URLEncoder.encode(etD.getText().toString(), "UTF-8");
            getRoute(origin, destination);
        }else{
            Toast.makeText(view.getContext(),"Informe o destino", Toast.LENGTH_SHORT);
        }
    }





    // WEB CONNECTION
    //public void getRoute(final String origin, final String destination){
    public void getRoute(final String origin, final String destination){
        new Thread(){
            public void run(){
                String url= "http://maps.googleapis.com/maps/api/directions/json?origin="
                        + origin+"&destination="
                        + destination+"&sensor=false";


                HttpResponse response;
                HttpGet request;
                AndroidHttpClient client = AndroidHttpClient.newInstance("route");

                request = new HttpGet(url);
                try {
                    response = client.execute(request);
                    final String answer = EntityUtils.toString(response.getEntity());

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                //Log.i("Script", answer);
                                list = buildJSONRoute(answer);
                                drawRoute();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }




    // PARSER JSON
    public List<LatLng> buildJSONRoute(String json) throws JSONException{
        JSONObject result = new JSONObject(json);
        JSONArray routes = result.getJSONArray("routes");

        distance = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");

        JSONArray steps = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        List<LatLng> lines = new ArrayList<LatLng>();

        for(int i=0; i < steps.length(); i++) {
            Log.i("Script", "STEP: LAT: "+steps.getJSONObject(i).getJSONObject("start_location").getDouble("lat")+" | LNG: "+steps.getJSONObject(i).getJSONObject("start_location").getDouble("lng"));


            String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");

            for(LatLng p : decodePolyline(polyline)) {
                lines.add(p);
            }

            Log.i("Script", "STEP: LAT: "+steps.getJSONObject(i).getJSONObject("end_location").getDouble("lat")+" | LNG: "+steps.getJSONObject(i).getJSONObject("end_location").getDouble("lng"));
        }

        return(lines);
    }




    // DECODE POLYLINE
    private List<LatLng> decodePolyline(String encoded) {

        List<LatLng> listPoints = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            Log.i("Script", "POL: LAT: " + p.latitude + " | LNG: " + p.longitude);
            listPoints.add(p);
        }
        return listPoints;
    }
}
