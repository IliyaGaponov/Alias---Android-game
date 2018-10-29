package il.co.alias;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by igapo on 28.09.2018.
 */

public class WordResultAdapter extends RecyclerView.Adapter<WordResultAdapter.WordViewHolder> {

    private List<String> words;
    private List<Boolean> guessedOrSkippedList;
    private IWordResultListener listener;

    public void setListener(IWordResultListener listener)
{
    this.listener = listener;
}

    public WordResultAdapter(List<String> words, List<Boolean> guessedOrSkippedList) {
        this.words = words;
        this.guessedOrSkippedList = guessedOrSkippedList;
    }

    public class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView wordTv;
        TextView wordLineTv;
        ImageView guessedIv;
        ImageView skippedIv;
        ImageView elseIv;

        public WordViewHolder(View itemView) {
            super(itemView);

            wordTv = itemView.findViewById(R.id.word_tv);
            wordLineTv = itemView.findViewById(R.id.line_texview);
            guessedIv = itemView.findViewById(R.id.guessed_iv);
            skippedIv = itemView.findViewById(R.id.skipped_iv);
            elseIv = itemView.findViewById(R.id.else_iv);

            guessedIv.setTag("guessedIv");
            guessedIv.setOnClickListener(this);
            skippedIv.setTag("skippedIv");
            skippedIv.setOnClickListener(this);
            elseIv.setTag("elseIv");
            elseIv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listener != null)
            {
                listener.onWordResultClicked(v, getAdapterPosition());
            }
        }
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.round_word_result, parent, false);
        WordViewHolder wordViewHolder = new WordViewHolder(view);
        return wordViewHolder;
    }

    //holder is the item that will shown on the screen
    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        String word = words.get(position);
        Boolean answer = guessedOrSkippedList.get(position);

        if(answer == null)
        {
            setViewVisibility(holder.wordLineTv, holder.wordTv);
            holder.wordLineTv.setText(word);
            holder.guessedIv.setImageResource(R.drawable.smile_black);
            holder.skippedIv.setImageResource(R.drawable.sad_black);
            holder.elseIv.setImageResource(R.drawable.error_red);
        }
        else if(!answer)
        {
            setViewVisibility(holder.wordTv, holder.wordLineTv);
            holder.wordTv.setText(word);
            holder.guessedIv.setImageResource(R.drawable.smile_black);
            holder.skippedIv.setImageResource(R.drawable.sad_red);
            holder.elseIv.setImageResource(R.drawable.error_black);
        }
        else if(answer)
        {
            setViewVisibility(holder.wordTv, holder.wordLineTv);
            holder.wordTv.setText(word);
            holder.guessedIv.setImageResource(R.drawable.smile_green);
            holder.skippedIv.setImageResource(R.drawable.sad_black);
            holder.elseIv.setImageResource(R.drawable.error_black);
        }
    }

    private void setViewVisibility(View visible, View gone)
    {
        visible.setVisibility(View.VISIBLE);
        gone.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    interface IWordResultListener {
        void onWordResultClicked(View view, int position);
    }
}
