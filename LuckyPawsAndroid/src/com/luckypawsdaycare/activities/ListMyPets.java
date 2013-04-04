/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.luckypawsdaycare.R;
import com.luckypawsdaycare.database.PetsDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListMyPets extends ListActivity {
    public final String TAG = "ListMyPets";

    Map<String, Integer> petsData;
    List<Pet> myPets;

    Button addPet;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.my_pets_index);

        PetsDAO db = new PetsDAO(this);
        petsData = db.getPetsIndex();

        findAndWireElements();
    }

    public void findAndWireElements() {
        addPet = (Button)findViewById(R.id.add_pet_button);
        addPet.setOnClickListener(addPetListener);

        prepareListView();
    }

    Button.OnClickListener addPetListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent add = new Intent(ListMyPets.this, EditMyPets.class);
            startActivity(add);
            ListMyPets.this.finish();
        }
    };

    private void prepareListView(){
        myPets = new ArrayList<Pet>();
        for(String name : petsData.keySet()) {
            myPets.add(new Pet(name, petsData.get(name)));
        }
        setListAdapter(new PetAdapter(this, R.layout.plain_list_layout, myPets));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        int petId = myPets.get(position).id;
        Intent view = new Intent(this, ViewMyPet.class);
        view.putExtra("com.luckypawsdaycare.petId", id);
        startActivity(view);
    }

    //////////////////////////////////////
    //  Pets List Inflation Utilities   //
    //////////////////////////////////////

    static class Pet{
        String name;
        int id;

        Pet(String catalogName, int id){
            name = catalogName;
            this.id = id;
        }
    }

    class PetAdapter extends ArrayAdapter<Pet> {

        int listLayout;

        PetAdapter(Context context, int listLayout, List<Pet> list){
            super(context, listLayout, R.id.plain_list_text_view, list);
            this.listLayout = listLayout;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            PetWrapper wrapper = null;

            if (convertView == null){
                convertView = getLayoutInflater().inflate(listLayout, null);
                wrapper = new PetWrapper(convertView);
                convertView.setTag(wrapper);
            }
            else{
                wrapper = (PetWrapper)convertView.getTag();
            }

            wrapper.populateFrom(getItem(position));

            return convertView;
        }
    }

    class PetWrapper{

        private TextView name = null;
        private View row = null;

        PetWrapper(View row){
            this.row = row;
        }

        TextView getName(){
            if (name == null){
                name = (TextView)row.findViewById(R.id.plain_list_text_view);
            }
            return name;
        }

        void populateFrom(Pet pet){
            getName().setText(pet.name);
        }
    }
}
