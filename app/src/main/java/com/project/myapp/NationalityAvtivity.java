package com.project.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NationalityAvtivity extends Activity {
	public ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();

	ListView lv_categories;
	ListAdapterSearchCategory searchAdapter;
	// TextView tv_nation_selected;
	public static String str_nation_selected = "";
	public static int ic_nation_selected = 0;
	private List<SpinnerModel> data_filtered;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nationality);
		setModelDat();
		initialize();
		makeFilter();
	}

	private void initialize() {
		// tv_nation_selected = (TextView)
		// findViewById(R.id.tv_nation_selected);
		searchAdapter = new ListAdapterSearchCategory(NationalityAvtivity.this,
				CustomListViewValuesArr);
		lv_categories = (ListView) findViewById(R.id.lv_nationality);
		lv_categories.setAdapter(searchAdapter);

		lv_categories.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				TextView tv = (TextView) view.findViewById(R.id.tv_nationality);
				String nationSelected = (String) tv.getText();
				// tv_nation_selected.setText(nationSelected);
				str_nation_selected = nationSelected;

				ic_nation_selected = data_filtered.get(pos).getFlag();
				// Toast.makeText(getApplicationContext(), nationSelected,
				// Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	void setModelDat() {
		SpinnerModel model;

		model = new SpinnerModel();
		model.setNationality("Afghanistan");
		model.setFlag(R.drawable.afghanistan_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Aland Islands");
		model.setFlag(R.drawable.aland_islands);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Albania");
		model.setFlag(R.drawable.albania_flag);
		
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Algeria");
		model.setFlag(R.drawable.algeria_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("America Samoa");
		model.setFlag(R.drawable.american_samoa);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Andorra");
		model.setFlag(R.drawable.andorra_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Angola");
		model.setFlag(R.drawable.angola_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Anguilla");
		model.setFlag(R.drawable.anguilla);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Antarctica");
		model.setFlag(R.drawable.antarctica);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Antigua and Barbuda");
		model.setFlag(R.drawable.antigua_and_barbuda);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Argentina");
		model.setFlag(R.drawable.argentina_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Armenia");
		model.setFlag(R.drawable.armenia_flag);		
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();		
		model.setNationality("Aruba");
		model.setFlag(R.drawable.aruba);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Australia");
		model.setFlag(R.drawable.australia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Austria");
		model.setFlag(R.drawable.austria_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Azerbaijan");
		model.setFlag(R.drawable.azerbaijan_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Bahamas");
		model.setFlag(R.drawable.bahamas_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Bahrain");
		model.setFlag(R.drawable.bahrain_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Bangladesh");
		model.setFlag(R.drawable.bangladesh_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Barbados");
		model.setFlag(R.drawable.barbados_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Belarus");
		model.setFlag(R.drawable.belarus_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Belgium");
		model.setFlag(R.drawable.belgium_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Belize");
		model.setFlag(R.drawable.belize_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Benin");
		model.setFlag(R.drawable.benin_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Bermuda");
		model.setFlag(R.drawable.bermuda);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Bhutan");
		model.setFlag(R.drawable.bhutan_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Biot");
		model.setFlag(R.drawable.biot);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Bolivia");
		model.setFlag(R.drawable.bolivia_flag);
		CustomListViewValuesArr.add(model);

		// change names
		model = new SpinnerModel();
		model.setNationality("Bonaire");
		model.setFlag(R.drawable.bonaire_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Bosnia");
		model.setFlag(R.drawable.bosnianbflag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Botswana");
		model.setFlag(R.drawable.botswana_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Bouvet Island");
		model.setFlag(R.drawable.bouvet_island);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Brazil");
		model.setFlag(R.drawable.brazil_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("British Antarctic Territory");
		model.setFlag(R.drawable.british_antarctic_territory);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("British Virgin Islands");
		model.setFlag(R.drawable.british_virgin_islands);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Brunei");
		model.setFlag(R.drawable.brunei_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Bulgaria");
		model.setFlag(R.drawable.bulgaria_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Burkina Faso");
		model.setFlag(R.drawable.burkina_faso_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Burma");
		model.setFlag(R.drawable.burma_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Burundi");
		model.setFlag(R.drawable.burundi_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Cambodia");
		model.setFlag(R.drawable.cambodia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Cameroon");
		model.setFlag(R.drawable.cameroon_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Canada");
		model.setFlag(R.drawable.canada_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Cape Verde");
		model.setFlag(R.drawable.cape_verde_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Cascadia");
		model.setFlag(R.drawable.cascadia);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Central African Republic");
		model.setFlag(R.drawable.centralafricanrepublic256);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Chad");
		model.setFlag(R.drawable.chad_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Chile");
		model.setFlag(R.drawable.chile_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("China");
		model.setFlag(R.drawable.china_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Christmas Island");
		model.setFlag(R.drawable.christmas_island);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Cocos Islands");
		model.setFlag(R.drawable.cocos_islands);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Colombia");
		model.setFlag(R.drawable.colombia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Comoros");
		model.setFlag(R.drawable.comoros_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Congo");
		model.setFlag(R.drawable.congo_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Congo Kinshasa");
		model.setFlag(R.drawable.congo_kinshasa);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Cook Islands");
		model.setFlag(R.drawable.cook_islands);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Costa Rica");
		model.setFlag(R.drawable.costa_rica_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Croatian");
		model.setFlag(R.drawable.croatian_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Cuba");
		model.setFlag(R.drawable.cuba_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Curacao");
		model.setFlag(R.drawable.curacao_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Cyprus");
		model.setFlag(R.drawable.cyprus_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Czech Republic");
		model.setFlag(R.drawable.czech_republic);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Denmark");
		model.setFlag(R.drawable.denmark);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Djibouti");
		model.setFlag(R.drawable.djibouti_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Dominica");
		model.setFlag(R.drawable.dominica);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Dominican Republic");
		model.setFlag(R.drawable.dominican_republic_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("East Timor");
		model.setFlag(R.drawable.east_timor);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Ecuador");
		model.setFlag(R.drawable.ecuador_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Egypt");
		model.setFlag(R.drawable.egypt_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("El Salvador");
		model.setFlag(R.drawable.el_salvador_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("England");
		model.setFlag(R.drawable.england_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Equatorial Guinea");
		model.setFlag(R.drawable.equatorial_guinea_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Eritrea");
		model.setFlag(R.drawable.eritrea_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Estonia");
		model.setFlag(R.drawable.estonia);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Ethiopia");
		model.setFlag(R.drawable.ethiopia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("European Union");
		model.setFlag(R.drawable.european_union_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Ex Yugoslavia");
		model.setFlag(R.drawable.ex_yugoslavia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Falkland Islands");
		model.setFlag(R.drawable.falkland_islands);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Faroe Islands");
		model.setFlag(R.drawable.faroe_islands);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Fiji");
		model.setFlag(R.drawable.fiji);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Finland");
		model.setFlag(R.drawable.finland_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("France");
		model.setFlag(R.drawable.france_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("French Polynesia");
		model.setFlag(R.drawable.french_polynesia);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("French Southern Territories");
		model.setFlag(R.drawable.french_southern_territories);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Gabon");
		model.setFlag(R.drawable.gabon_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Gambia");
		model.setFlag(R.drawable.gambia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Georgia");
		model.setFlag(R.drawable.georgia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Germany");
		model.setFlag(R.drawable.germany_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Ghana");
		model.setFlag(R.drawable.ghana_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Gibraltar");
		model.setFlag(R.drawable.gibraltar_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Greece");
		model.setFlag(R.drawable.greece_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Greenland");
		model.setFlag(R.drawable.greenland_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Grenada");
		model.setFlag(R.drawable.grenada);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Guadeloupe");
		model.setFlag(R.drawable.guadeloupe);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Guam");
		model.setFlag(R.drawable.guam_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Guatemala");
		model.setFlag(R.drawable.guatemala_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Guernsey");
		model.setFlag(R.drawable.guernsey);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Guinea Bissau");
		model.setFlag(R.drawable.guinea_bissau_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Guinea");
		model.setFlag(R.drawable.guinea_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Guyana");
		model.setFlag(R.drawable.guyana_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Haiti");
		model.setFlag(R.drawable.haiti_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Holy See");
		model.setFlag(R.drawable.holy_see_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Honduras");
		model.setFlag(R.drawable.honduras_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Hong Kong");
		model.setFlag(R.drawable.hong_kong_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Hungary");
		model.setFlag(R.drawable.hungary_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Iceland");
		model.setFlag(R.drawable.iceland_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("India");
		model.setFlag(R.drawable.india_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Indonesia");
		model.setFlag(R.drawable.indonesia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Iran");
		model.setFlag(R.drawable.iran_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Iraq");
		model.setFlag(R.drawable.iraq_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Ireland");
		model.setFlag(R.drawable.ireland_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Isle of Man");
		model.setFlag(R.drawable.isle_of_man_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Israel");
		model.setFlag(R.drawable.israel_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Italy");
		model.setFlag(R.drawable.italy_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Ivory Coast");
		model.setFlag(R.drawable.ivory_coast_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Jamaica");
		model.setFlag(R.drawable.jamaica_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Jan Mayen");
		model.setFlag(R.drawable.jan_mayen_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Japan");
		model.setFlag(R.drawable.japan_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Jarvis Island");
		model.setFlag(R.drawable.jarvis_island_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Jersey");
		model.setFlag(R.drawable.jersey_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Jordan");
		model.setFlag(R.drawable.jordan_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Kazakhstan");
		model.setFlag(R.drawable.kazakhstan_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Kenya");
		model.setFlag(R.drawable.kenya_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Kiribati");
		model.setFlag(R.drawable.kiribati);
		CustomListViewValuesArr.add(model);		

		model = new SpinnerModel();
		model.setNationality("Korea");
		model.setFlag(R.drawable.korea_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Kosovo");
		model.setFlag(R.drawable.kosovo);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Kuwait");
		model.setFlag(R.drawable.kuwait_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Kyrgyzstan");
		model.setFlag(R.drawable.kyrgyzstan_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Laos");
		model.setFlag(R.drawable.laos_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Latvia");
		model.setFlag(R.drawable.latvia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Lebanon");
		model.setFlag(R.drawable.lebanon_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Lesotho");
		model.setFlag(R.drawable.lesotho_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Liberia");
		model.setFlag(R.drawable.liberia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Libya");
		model.setFlag(R.drawable.libya_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Liechtenstein");
		model.setFlag(R.drawable.liechtenstein_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Lithuania");
		model.setFlag(R.drawable.lithuania_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Luxembourg");
		model.setFlag(R.drawable.luxembourg_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Macau");
		model.setFlag(R.drawable.macau_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Macedonia");
		model.setFlag(R.drawable.macedonia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Madagascar");
		model.setFlag(R.drawable.madagascar_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Malawi");
		model.setFlag(R.drawable.malawi_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Malaysia");
		model.setFlag(R.drawable.malaysia);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Maldives");
		model.setFlag(R.drawable.maldives_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Mali");
		model.setFlag(R.drawable.mali_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Malta");
		model.setFlag(R.drawable.malta_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Marshall Islands");
		model.setFlag(R.drawable.marshall_islands_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Martinique");
		model.setFlag(R.drawable.martinique);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Mauritania");
		model.setFlag(R.drawable.mauritania_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Mauritius");
		model.setFlag(R.drawable.mauritius_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Mayotte");
		model.setFlag(R.drawable.mayotte_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Mexico");
		model.setFlag(R.drawable.mexico_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Micronesia");
		model.setFlag(R.drawable.micronesia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Moldova");
		model.setFlag(R.drawable.moldova_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Monaco");
		model.setFlag(R.drawable.monaco_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Mongolia");
		model.setFlag(R.drawable.mongolia_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Montenegro");
		model.setFlag(R.drawable.montenegro);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Montserrat");
		model.setFlag(R.drawable.montserrat_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Morocco");
		model.setFlag(R.drawable.morocco_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Mozambique");
		model.setFlag(R.drawable.mozambique_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Myanmar");
		model.setFlag(R.drawable.myanmar);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Namibia");
		model.setFlag(R.drawable.namibia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Nauru");
		model.setFlag(R.drawable.nauru_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Nepal");
		model.setFlag(R.drawable.nepal_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Netherlands Antilles");
		model.setFlag(R.drawable.netherlands_antilles);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Netherlands");
		model.setFlag(R.drawable.netherlands_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("New Caledonia");
		model.setFlag(R.drawable.new_caledonia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("New Zealand");
		model.setFlag(R.drawable.new_zealand);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Nicaragua");
		model.setFlag(R.drawable.nicaragua_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Niger");
		model.setFlag(R.drawable.niger_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Nigeria");
		model.setFlag(R.drawable.nigeria_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Niue");
		model.setFlag(R.drawable.niue_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Norfolk Island");
		model.setFlag(R.drawable.norfolk_island);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("North Korea");
		model.setFlag(R.drawable.north_korea_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Northern Ireland");
		model.setFlag(R.drawable.northern_ireland);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Northern Mariana Islands");
		model.setFlag(R.drawable.northern_mariana_islands);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Norway");
		model.setFlag(R.drawable.norway_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Oman");
		model.setFlag(R.drawable.oman_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Pakistan");
		model.setFlag(R.drawable.pakistan_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Palau");
		model.setFlag(R.drawable.palau_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Palestinian");
		model.setFlag(R.drawable.palestinian_territory);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Panama");
		model.setFlag(R.drawable.panama_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Papua New Guinea");
		model.setFlag(R.drawable.papua_new_guinea_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Paraguay");
		model.setFlag(R.drawable.paraguay_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Peru");
		model.setFlag(R.drawable.peru_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Philippines");
		model.setFlag(R.drawable.philippines_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Pitcairn");
		model.setFlag(R.drawable.pitcairn);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Poland");
		model.setFlag(R.drawable.poland_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Portugal");
		model.setFlag(R.drawable.portugal_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Puerto Rico");
		model.setFlag(R.drawable.puerto_rico_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Qatar");
		model.setFlag(R.drawable.qatar_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Reunion");
		model.setFlag(R.drawable.reunion_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Romania");
		model.setFlag(R.drawable.romania_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Russia");
		model.setFlag(R.drawable.russia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Rwanda");
		model.setFlag(R.drawable.rwanda_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Saint Barthelemy");
		model.setFlag(R.drawable.saint_barthelemy);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Saint Helena and Dependencies");
		model.setFlag(R.drawable.saint_helena);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Saint Kitts and Nevis");
		model.setFlag(R.drawable.saint_kitts_and_nevis);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Saint Lucia");
		model.setFlag(R.drawable.saint_lucia_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Saint Martin");
		model.setFlag(R.drawable.saint_martin);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Saint Pierre and Miquelon");
		model.setFlag(R.drawable.saint_pierre_and_miquelon);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Samoa");
		model.setFlag(R.drawable.samoa_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("San Marino");
		model.setFlag(R.drawable.san_marino_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Sao Tome and Principe");
		model.setFlag(R.drawable.sao_tome_and_principe);
		CustomListViewValuesArr.add(model);		

		model = new SpinnerModel();
		model.setNationality("Saudi Arabia");
		model.setFlag(R.drawable.saudi_arabia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Scotland");
		model.setFlag(R.drawable.scotland);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Senegal");
		model.setFlag(R.drawable.senegal_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Serbia");
		model.setFlag(R.drawable.serbia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Seychelles");
		model.setFlag(R.drawable.seychelles_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Sierra Leone");
		model.setFlag(R.drawable.sierra_leone_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Singapore");
		model.setFlag(R.drawable.singapore_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Sint Eustatius");
		model.setFlag(R.drawable.sint_eustatius_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Sint Maarten");
		model.setFlag(R.drawable.sint_maarten_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Slovakia");
		model.setFlag(R.drawable.slovakia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Slovenia");
		model.setFlag(R.drawable.slovenia_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Smom");
		model.setFlag(R.drawable.smom);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Solomon Islands");
		model.setFlag(R.drawable.solomon_islands_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Somalia");
		model.setFlag(R.drawable.somalia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("South Africa");
		model.setFlag(R.drawable.south_africa_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("South Georgia");
		model.setFlag(R.drawable.south_georgia);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("South Sudan");
		model.setFlag(R.drawable.south_sudan_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Spain");
		model.setFlag(R.drawable.spain_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Spm");
		model.setFlag(R.drawable.spm);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Sri Lanka");
		model.setFlag(R.drawable.sri_lanka_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Sudan");
		model.setFlag(R.drawable.sudan_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Suriname");
		model.setFlag(R.drawable.suriname_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Svalbard");
		model.setFlag(R.drawable.svalbard_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Svg");
		model.setFlag(R.drawable.svg);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Swaziland");
		model.setFlag(R.drawable.swaziland_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Sweden");
		model.setFlag(R.drawable.sweden_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Switzerland");
		model.setFlag(R.drawable.switzerland_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Syria");
		model.setFlag(R.drawable.syria_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Taiwan");
		model.setFlag(R.drawable.taiwan_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Tajikistan");
		model.setFlag(R.drawable.tajikistan_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Tanzania");
		model.setFlag(R.drawable.tanzania_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Thailand");
		model.setFlag(R.drawable.thailand_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Timor Leste");
		model.setFlag(R.drawable.timor_leste_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Togo");
		model.setFlag(R.drawable.togo_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Tokelau");
		model.setFlag(R.drawable.tokelau);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Tonga");
		model.setFlag(R.drawable.tonga_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Trinidad and Tobago");
		model.setFlag(R.drawable.trinidad_and_tobago);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Tunisia");
		model.setFlag(R.drawable.tunisia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Turkey");
		model.setFlag(R.drawable.turkey_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Turkmenistan");
		model.setFlag(R.drawable.turkmenistan_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Turks and Caicos Islands");
		model.setFlag(R.drawable.turks_and_caicos_islands);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Tuvalu");
		model.setFlag(R.drawable.tuvalu);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Uganda");
		model.setFlag(R.drawable.uganda_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Ukraine");
		model.setFlag(R.drawable.ukraine_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("United Arab Emirates");
		model.setFlag(R.drawable.united_arab_emirates);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("United Kingdom");
		model.setFlag(R.drawable.united_kingdom_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("United States");
		model.setFlag(R.drawable.united_states_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Uruguay");
		model.setFlag(R.drawable.uruguay_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Ussr");
		model.setFlag(R.drawable.ussr_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Uzbekistan");
		model.setFlag(R.drawable.uzbekistan_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Vanuatu");
		model.setFlag(R.drawable.vanuatu_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Vatican City");
		model.setFlag(R.drawable.vatican_city);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Venezuela");
		model.setFlag(R.drawable.venezuela_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Vietnam");
		model.setFlag(R.drawable.vietnam_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Virgin Islands");
		model.setFlag(R.drawable.virgin_islands_flag);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Wales");
		model.setFlag(R.drawable.wales);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Wallis and Futuna");
		model.setFlag(R.drawable.wallis_and_futuna_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Western Sahara");
		model.setFlag(R.drawable.western_sahara);
		CustomListViewValuesArr.add(model);
		
		model = new SpinnerModel();
		model.setNationality("Yemen");
		model.setFlag(R.drawable.yemen_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Yugoslavia");
		model.setFlag(R.drawable.yugoslavia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Zambia");
		model.setFlag(R.drawable.zambia_flag);
		CustomListViewValuesArr.add(model);

		model = new SpinnerModel();
		model.setNationality("Zimbabwe");
		model.setFlag(R.drawable.zimbabwe_flag);
		CustomListViewValuesArr.add(model);

	}

	private void makeFilter() {
		EditText filterEditText = (EditText) findViewById(R.id.input_search);

		filterEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					lv_categories.setVisibility(View.VISIBLE);
				} else {
					lv_categories.setVisibility(View.INVISIBLE);
				}
			}
		});
		// Add Text Change Listener to EditText
		filterEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Call back the Adapter with current character to Filter
				// adapter.getFilter().filter(s.toString());
				if (s.length() == 0) {
					searchAdapter.getFilter().filter(s.toString());
				} else {
					searchAdapter.getFilter().filter(s.toString());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	class ListAdapterSearchCategory extends BaseAdapter implements Filterable {

		List<SpinnerModel> originalData;
		List<String> strData;
		Context context;

		public ListAdapterSearchCategory(Context context,
				List<SpinnerModel> data) {
			data_filtered = data;
			originalData = data;
			this.context = context;
			strData = new ArrayList<String>();
			for (int i = 0; i < data.size(); i++) {
				strData.add(data.get(i).getNationality());
			}
		}

		private class ViewHolderSubCategory {
			TextView textView;
			ImageView img_flag;
		}

		@Override
		public int getCount() {

			return data_filtered.size();
		}

		@Override
		public Object getItem(int position) {

			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int pos, View catView, ViewGroup parent) {
			ViewHolderSubCategory holder = null;
			if (catView == null) {

				holder = new ViewHolderSubCategory();
				catView = ((Activity) context).getLayoutInflater().inflate(
						R.layout.nationality, parent, false);
				holder.textView = (TextView) catView
						.findViewById(R.id.tv_nationality);
				holder.img_flag = (ImageView) catView
						.findViewById(R.id.img_flag);
				catView.setTag(holder);
			} else {
				holder = (ViewHolderSubCategory) catView.getTag();
			}
			holder.textView.setText(data_filtered.get(pos).getNationality());
			holder.img_flag.setImageResource(data_filtered.get(pos).getFlag());
			return catView;
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					List<SpinnerModel> FilteredList = new ArrayList<SpinnerModel>();
					FilterResults results = new FilterResults();

					if (constraint == null || constraint.length() == 0) {
						// set the Original result to return
						results.count = originalData.size();
						results.values = originalData;
					} else {
						constraint = constraint.toString().toLowerCase(Locale.getDefault());
						for (int i = 0; i < strData.size(); i++) {
							String str = strData.get(i);
							if (str.toLowerCase(Locale.getDefault()).startsWith(
									constraint.toString())) {
								FilteredList.add(originalData.get(i));
							}
						}
						// set the Filtered result to return
						results.count = FilteredList.size();
						results.values = FilteredList;
					}
					return results;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					data_filtered = (List<SpinnerModel>) results.values;
					notifyDataSetChanged();
				}

			};
			return filter;
		}

	}
	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
			case KeyEvent.KEYCODE_MENU:
				// doSomething();
				// Toast.makeText(getApplicationContext(), "Menu Button Pressed",
				// Toast.LENGTH_SHORT).show();
				try {
					Intent i = new Intent(this, Bl_Settings.class);
					startActivity(i);
				} catch (Exception dd) {

				}

				return true;
		}

		return super.onKeyDown(keycode, e);
	}
}
