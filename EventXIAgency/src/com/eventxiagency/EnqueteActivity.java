package com.eventxiagency;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONStringer;
import org.json.simple.JSONArray;

import com.eventxiagency.utils.HttpRequest;
import com.eventxiagency.utils.MyTools;
import com.eventxiagency.utils.Singleton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EnqueteActivity extends Activity implements OnClickListener, OnMenuItemClickListener{

	private Singleton sglt = Singleton.getInstance();
	private String JSONString;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enquete);
		
		((TextView) findViewById(R.id.enquete_prenomCoor)).setText(sglt.coordinateur[1]);
		((TextView) findViewById(R.id.enquete_nomCoor)).setText(sglt.coordinateur[0]);
		((Button) findViewById(R.id.enquete_voirAnimateurs)).setOnClickListener(this);
		((TextView) findViewById(R.id.enquete_nomEnquete)).setText(sglt.enquete[0]);
		((TextView) findViewById(R.id.enquete_detailEnquete)).setText(sglt.enquete[1]);
		((Button) findViewById(R.id.enquete_voirEquipe)).setOnClickListener(this);
		((Button) findViewById(R.id.enquete_commencer)).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_enquete, menu);		
		//menu.getItem(R.menu.activity_enquete).setOnMenuItemClickListener(this);		
		return true;
	}
	
	public void displayAnimateurs() {
		final CharSequence[] items = new CharSequence[sglt.animateurs.length];
		for(int i = 0; i < sglt.animateurs.length; i++)
		{
			items[i] = sglt.animateurs[i][1] + " " + sglt.animateurs[i][0];
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Liste des Animateurs");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public boolean partieDebutee()
	{
		
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.enquete_voirAnimateurs:
			displayAnimateurs();
			break;
		case R.id.enquete_voirEquipe:
			new displayEquipe(this).execute("http://" + sglt._url + ":" + sglt._port + "/equipesFromPartie/");
			break;
		case R.id.enquete_commencer:
			Button btn = (Button) findViewById(R.id.enquete_commencer);
			btn.setEnabled(false);
			if(partieDebutee())
			{
			
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Erreur");
				builder.setMessage("La partie n'a pas encore débuté, veuillez patiener...");
				AlertDialog alert = builder.create();
				alert.show();
				btn.setEnabled(true);
			}
		default:
			Log.i("Event", "NotFound");
			break;
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.menu_quitter:
			this.finish();
			break;
		case R.id.a_propos:
			break;
		}
		return false;
	}

	public class displayEquipe extends AsyncTask<String, Void, Integer>
	{
		EnqueteActivity eq;
		
		public displayEquipe(EnqueteActivity eq)
		{
			this.eq = eq;
		}
		
		@Override
		protected Integer doInBackground(String... arg0) {
			List<String[]> datas = new ArrayList<String[]>();
			datas.add(new String[] {"partie_code", sglt.partie_code});
			HttpRequest req = new HttpRequest(arg0[0], datas);
			
			if(req.responseString.startsWith("["))
			{
				JSONString = req.responseString;
				JSONArray obj = (JSONArray) MyTools.JSONDecode(JSONString);
				int return_code = Integer.parseInt(obj.get(0).toString());
				return return_code;
			}
			else
				return 0;
		}
		
		@Override
		protected void onPostExecute(final Integer code) {			
			switch(code)
			{
				case 12:
					JSONArray obj = (JSONArray) MyTools.JSONDecode(JSONString);		
					JSONArray equipeJson = (JSONArray) obj.get(1);
					sglt.equipes = new String[equipeJson.size()];
					for(int i = 0; i < equipeJson.size(); i++)
					{
						sglt.equipes[i] = equipeJson.get(i).toString();
					}
					final CharSequence[] items = new CharSequence[sglt.equipes.length];
					for(int i = 0; i < sglt.equipes.length; i++)
					{
						items[i] = sglt.equipes[i];
					}
					
					AlertDialog.Builder builder = new AlertDialog.Builder(eq);
					builder.setTitle("Liste des Equipes");
					builder.setItems(items, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
					break;
				default:
					break;	
			}
		}
	}
}
