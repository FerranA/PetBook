package com.example.PETBook.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.PETBook.Conexion;
import com.example.PETBook.Controllers.AsyncResult;
import com.example.PETBook.Models.FriendSuggestionModel;
import com.example.PETBook.Models.Image;
import com.example.PETBook.Models.UserModel;
import com.example.PETBook.SingletonUsuario;
import com.example.pantallafirstview.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends BaseAdapter implements AsyncResult {
    private Context context;
    private ArrayList<UserModel> users;
    private String tipoConexion;
    private ImageView imageProfile;
    UserModel user;

    public UserAdapter(Context context, ArrayList<UserModel> array){
        this.context = context;
        users = array;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.user_design,null);
        }

        final UserModel user_selected = users.get(position);

        TextView inputFullName = (TextView) convertView.findViewById(R.id.fullNameInput);

        inputFullName.setText(users.get(position).getFirstName() +" " + users.get(position).getSecondName());
        imageProfile = (CircleImageView) convertView.findViewById(R.id.imageView);
        Button addButton = (Button) convertView.findViewById(R.id.addButton);
        tipoConexion = "imageFriend";
        Conexion con = new Conexion(this);
        con.execute("http://10.4.41.146:9999/ServerRESTAPI/getPicture/" + user_selected.getEmail(), "GET", null);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = user_selected;
                tipoConexion = "sendRequest";
                SingletonUsuario su = SingletonUsuario.getInstance();
                /* Nueva conexion llamando a la funcion del server */
                Conexion con = new Conexion(UserAdapter.this);
                con.execute("http://10.4.41.146:9999/ServerRESTAPI/sendFriendRequest/" + user_selected.getEmail(), "POST", null);
            }
        });

        return convertView;
    }

    public void OnprocessFinish(JSONObject output) {
        if (output != null) {
            if(tipoConexion.equals("sendRequest")) {
                try {
                    int response = output.getInt("code");
                    if (response == 200) {
                        /*users.remove(friendSuggestion);
                        FriendSuggestionAdapter.this.notifyDataSetChanged();*/
                        Toast.makeText(this.context, "Friend request sent successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this.context, "There was a problem during the process.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(tipoConexion.equals("imageFriend")) {
                try {
                    int response = output.getInt("code");
                    if (response == 200) {
                        // convert string to bitmap
                        SingletonUsuario user = SingletonUsuario.getInstance();
                        Image imagenConversor = Image.getInstance();
                        String image = output.getString("image");
                        Bitmap profileImage = imagenConversor.StringToBitMap(image);
                        imageProfile.setImageBitmap(profileImage);
                        //user.setProfilePicture(profileImage);
                    }   else if (output.getInt("code")==404) { // user does not have profile picture
                        imageProfile.setImageResource(R.drawable.troymcclure);
                    } else {
                        Toast.makeText(this.context, "There was a problem during the process.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {

            Toast toast = Toast.makeText(this.context, "The server does not work.", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}