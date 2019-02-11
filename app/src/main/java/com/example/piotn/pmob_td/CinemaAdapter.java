package com.example.piotn.pmob_td;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.FilmHolder> {

    private List<Film> items = new ArrayList<Film>();

    public List<Film> getItems() {
        return items;
    }

    public void setItems(List<Film> items) {
        this.items = items;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class FilmHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public ImageView image;
        public TextView nom;
        public TextView date;
        public TextView realisateur;
        public FilmHolder(ImageView image, TextView nom, TextView date, TextView realisateur, View v) {
            super(v);
            this.image = image;
            this.nom = nom;
            this.date = date;
            this.realisateur = realisateur;

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CinemaAdapter(List<Film> items) {
        this.items = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CinemaAdapter.FilmHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);

        TextView nom = v.findViewById(R.id.textView_nom);
        TextView date = v.findViewById(R.id.textView_date);
        TextView realisateur = v.findViewById(R.id.textView_real);
        ImageView image = v.findViewById(R.id.imageView);

        FilmHolder f = new FilmHolder(image, nom, date, realisateur, v);
        return f;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FilmHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.realisateur.setText(items.get(position).getReal());
        holder.date.setText(items.get(position).getDate());
        holder.nom.setText(items.get(position).getNom());


        if(!(items.get(position).getImage()==null)) {
            holder.image.setImageBitmap(items.get(position).getImage());
        }
        else{
            holder.image.setImageResource(R.drawable.ic_launcher_background);
        }

        //new DownloadImagesTask().execute(items.get(position).getImageURL());
        //holder.image.setImageResource(R.drawable.ic_launcher_background);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}
