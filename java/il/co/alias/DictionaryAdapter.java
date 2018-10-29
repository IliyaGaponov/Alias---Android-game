package il.co.alias;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.*;

/**
 * Created by igapo on 26.10.2018.
 */

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>
{
    private List<Dictionary> dictionariesList;
    private ClickListener clickListener;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setDictionariesList(List<Dictionary> dictionariesList) {
        this.dictionariesList = dictionariesList;
    }

    public List<Dictionary> getDictionariesList() {
        return dictionariesList;
    }

    public class DictionaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        TextView level;
        TextView example;
        TextView wordsNum;


        public DictionaryViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = itemView.findViewById(R.id.dict_name);
            level = itemView.findViewById(R.id.dict_level);
            example = itemView.findViewById(R.id.dict_example);
            wordsNum = itemView.findViewById(R.id.dict_num_of_words);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    @Override
    public DictionaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dictionary_card, parent,false);
        DictionaryViewHolder teamViewHolder = new DictionaryViewHolder(view);

        return teamViewHolder;
    }

    @Override
    public void onBindViewHolder(DictionaryViewHolder holder, int position) {
        Dictionary dictionary = dictionariesList.get(position);

        holder.name.setText(dictionary.getTitle());
        holder.level.setText(dictionary.getLevel());
        holder.example.setText(dictionary.getExample());
        holder.wordsNum.setText(dictionary.getNumOfWords());
    }

    @Override
    public int getItemCount() {
        return dictionariesList.size();
    }
}
