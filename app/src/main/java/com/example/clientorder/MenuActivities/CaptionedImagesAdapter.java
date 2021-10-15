package com.example.clientorder.MenuActivities;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientorder.R;

import org.jetbrains.annotations.NotNull;


public class CaptionedImagesAdapter extends RecyclerView.Adapter<CaptionedImagesAdapter.ViewHolder> {

    private final String[] name;
    private final Double[] price;
    private final String[] description;

    public Listener listener;

    public interface Listener
    {
        void onClick(int position);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


    public CaptionedImagesAdapter(String[] name, Double[] price, String[] description) {
        this.name = name;
        this.price = price;
        this.description = description;

    }

    @NotNull
    @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
  {
      CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_captioned_image, parent, false);
      return new ViewHolder(cv);
  }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CardView cardView = holder.cardView;



        TextView textView = cardView.findViewById(R.id.nameTextView);
        textView.setText(name[position]);

        TextView textView1 = cardView.findViewById(R.id.PriceTextView);
        textView1.setText(price[position] + " PLN");


        TextView textView2 = cardView.findViewById(R.id.DescriptionTextView);
        textView2.setText(description[position]);


        cardView.setOnClickListener(v -> {
            if (listener != null)
            {
                listener.onClick(position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return price.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        private final CardView cardView;

        public ViewHolder(CardView v)
        {
            super(v);
            cardView = v;
        }

    }
}

