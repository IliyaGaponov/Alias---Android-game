package il.co.alias;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import static il.co.alias.ConstantsHolder.*;


/**
 * Created by igapo on 01.10.2018.
 */

public class TeamHolderFactory
{
    public static class NewTeamHolder extends RecyclerView.ViewHolder
    {
        private TextView teamName;
        private ImageView removeTeam;
        private ImageView editTeamName;
        private IAddTeamListener listener;

        interface IAddTeamListener {
            void onButtonRemoveClicked(int adapterPosition, View view);
            void onButtonEditClicked(int adapterPosition, View view);
        }

        public TextView getTeamName() {
            return teamName;
        }

        public void setListener(IAddTeamListener listener)
        {
            this.listener = listener;
        }

        public NewTeamHolder(View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.team_name);
            removeTeam = itemView.findViewById(R.id.delete_team);
            editTeamName = itemView.findViewById(R.id.edit_name);

            removeTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        listener.onButtonRemoveClicked(getAdapterPosition(), v);
                    }
                }
            });

            editTeamName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teamName.setCursorVisible(true);
                    listener.onButtonEditClicked(getAdapterPosition(), teamName);
            }
            });
        }
    }

    public static class TeamResultHolder extends RecyclerView.ViewHolder
    {
        TextView teamName;
        TextView teamScore;

        public TeamResultHolder(View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.team_name);
            teamScore = itemView.findViewById(R.id.score);
        }
    }

    public static RecyclerView.ViewHolder create(ViewGroup parent, TeamCardTypeEnum viewType) {

        switch (viewType) {
            case NEW_TEAM_CARD:
                View newTeamCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_team_card, parent, false);
                return new NewTeamHolder(newTeamCard);

            case TEAM_RESULT_CARD:
                View teamResultCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_result_card, parent, false);
                return new TeamResultHolder(teamResultCard);

            default:
                return null;
        }
    }
}
