package com.example.PETBook.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.PETBook.Adapters.ForumAdapter;
import com.example.PETBook.Conexion;
import com.example.PETBook.Controllers.AsyncResult;
import com.example.PETBook.ForumInfo;
import com.example.PETBook.MainActivity;
import com.example.PETBook.Models.CommentForumModel;
import com.example.PETBook.Models.ForumModel;
import com.example.PETBook.NewForum;
import com.example.PETBook.SingletonUsuario;
import com.example.pantallafirstview.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyEventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumFragment extends Fragment implements AsyncResult {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private View MyView;
    private ListView lista;
    private ForumAdapter forumAdapter;
    private ArrayList<ForumModel> forumModel;
    private OnFragmentInteractionListener mListener;

    public ForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForumFragment newInstance(String param1, String param2) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        SingletonUsuario usuario = SingletonUsuario.getInstance();

        if (!usuario.isMailConfirmed()) {

            /*
            Show dialog to inform user that does not have email confirmed
             */
            AlertDialog.Builder emailConfirmedDialog = new AlertDialog.Builder(getActivity());
            emailConfirmedDialog.setMessage("Confirme su correo para acceder a todas las funciones de PetBook")
                    .setCancelable(true)
                    .setPositiveButton("Reenviar correo de confirmación", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            /*
                            sends confirmation mail to the user's email
                             */
                            SingletonUsuario user = SingletonUsuario.getInstance();
                            Conexion conexion = new Conexion(ForumFragment.this);
                            conexion.execute("http://10.4.41.146:9999/ServerRESTAPI/SendConfirmationEmail", "POST", null);
                            dialog.cancel();
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("Más adelante", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            /*

                            Comentado para poder testear

                            Intent i = new Intent(getActivity(), MainActivity.class);
                            startActivity(i);
                            getActivity().finish();
                            */
                        }
                    });
            AlertDialog dialog = emailConfirmedDialog.create();
            dialog.setTitle("Confirmación del correo");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        MyView =  inflater.inflate(R.layout.activity_forum_list, container, false);

        // Set tittle to the fragment
        getActivity().setTitle("Forum");


        Conexion con = new Conexion(ForumFragment.this);
        SingletonUsuario su = SingletonUsuario.getInstance();

        con.execute("http://10.4.41.146:9999/ServerRESTAPI/forum/GetAllForumThreads" ,"GET", null);

        lista = MyView.findViewById(R.id.list_forums);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ForumModel forumSeleccionado = forumModel.get(position);
                Intent intent = new Intent(getActivity(), ForumInfo.class);
                intent.putExtra("forum", forumSeleccionado);
                startActivity(intent);
            }
        });
        FloatingActionButton fab = MyView.findViewById(R.id.addForum);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewForum.class);
                startActivity(intent);
            }
        });

        return MyView;
    }


    private String transformacionFechaHora(String fechaHora){
        Integer fin = 0;
        String result = fechaHora.replace("T", " ");
        result = result.replace("+0000", " ");
        System.out.println(result.split("."));
        return result;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void OnprocessFinish(JSONObject json) {
        try{
            if(json.getInt("code") == 200){
                forumModel = new ArrayList<>();
                JSONArray jsonArray = json.getJSONArray("array");
                ArrayList<CommentForumModel> comments = new ArrayList<CommentForumModel>();
                for(int i = 0; i < jsonArray.length(); ++i){
                    JSONObject forum = jsonArray.getJSONObject(i);
                    ForumModel f = new ForumModel();
                    f.setIDForum(forum.getInt("id"));
                    f.setCreationDate(transformacionFechaHora(forum.getString("creationDate")));
                    f.setCreatorMail(forum.getString("creatorMail"));
                    f.setDescription(forum.getString("description"));
                    f.setTitle(forum.getString("title"));
                    JSONArray c = forum.getJSONArray("comments");
                    for(int j = 0; j < c.length(); ++j){
                        JSONObject com = c.getJSONObject(j);
                        CommentForumModel cfm = new CommentForumModel();
                        cfm.setCreatorMail(com.getString("creatorMail"));
                        cfm.setCreationDate(transformacionFechaHora(com.getString("creationDate")));
                        cfm.setDescription(com.getString("description"));
                        cfm.setIDComment(com.getInt("id"));
                        comments.add(cfm);
                    }

                    f.setComments(comments);
                    forumModel.add(f);
                }
                forumAdapter = new ForumAdapter(getActivity(), forumModel);
                lista = (ListView) MyView.findViewById(R.id.list_forums);
                lista.setAdapter(forumAdapter);
                /*System.out.println(forumModel.get(2).getTitle());
                System.out.println(forumModel.get(2).getComments().get(1).getDescription());*/
                System.out.print(json.getInt("code") + " se muestran correctamente la lista de foros\n");
            }
            else{
                System.out.print("El sistema no logra mostrar la lista de foros del creador\n");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }




}